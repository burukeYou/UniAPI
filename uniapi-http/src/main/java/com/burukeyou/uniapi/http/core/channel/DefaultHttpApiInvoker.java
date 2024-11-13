package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.config.SpringBeanContext;
import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.conveter.response.HttpResponseBodyConverterChain;
import com.burukeyou.uniapi.http.core.conveter.response.HttpResponseConverter;
import com.burukeyou.uniapi.http.core.conveter.response.ResponseConvertContext;
import com.burukeyou.uniapi.http.core.exception.BaseUniHttpException;
import com.burukeyou.uniapi.http.core.exception.SendHttpRequestException;
import com.burukeyou.uniapi.http.core.exception.UniHttpResponseException;
import com.burukeyou.uniapi.http.core.request.HttpUrl;
import com.burukeyou.uniapi.http.core.request.*;
import com.burukeyou.uniapi.http.core.response.AbstractHttpResponse;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.extension.EmptyHttpApiProcessor;
import com.burukeyou.uniapi.http.extension.HttpApiProcessor;
import com.burukeyou.uniapi.http.support.HttpApiAnnotationMeta;
import com.burukeyou.uniapi.http.support.RequestMethod;
import com.burukeyou.uniapi.support.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author  caizhihao
 */
@Slf4j
public class DefaultHttpApiInvoker extends AbstractHttpMetadataParamFinder implements HttpSender {

    protected Class<?> targetClass;

    protected HttpApiAnnotationMeta annotationMeta;

    private final OkHttpClient client;

    private static final EmptyHttpApiProcessor emptyHttpProcessor = new EmptyHttpApiProcessor();

    private final HttpResponseConverter responseChain;


    private final Class<? extends HttpApiProcessor<?>> apiProcessor;
    private  final HttpApiProcessor<Annotation> requestProcessor;

    private HttpApiMethodInvocationImpl httpApiMethodInvocation;

    public DefaultHttpApiInvoker(HttpApiAnnotationMeta annotationMeta,
                                 Class<?> targetClass,
                                 HttpInterface httpInterface,
                                 MethodInvocation methodInvocation,OkHttpClient httpClient) {
        super(annotationMeta.getHttpApi(),httpInterface,annotationMeta.getProxySupport().getEnvironment());
        this.targetClass = targetClass;
        this.annotationMeta = annotationMeta;
        this.methodInvocation = methodInvocation;
        this.client = httpClient;
        this.responseChain = new HttpResponseBodyConverterChain().getChain();

        this.apiProcessor = getHttpApiProcessorClass(api,httpInterface);
        this.requestProcessor = buildRequestHttpApiProcessor(apiProcessor);

        httpApiMethodInvocation = new HttpApiMethodInvocationImpl();
        httpApiMethodInvocation.setProxyApiAnnotation(annotationMeta.getProxyAnnotation());
        httpApiMethodInvocation.setProxyInterface(httpInterface);
        httpApiMethodInvocation.setProxyClass(targetClass);
        httpApiMethodInvocation.setMethodInvocation(methodInvocation);
    }


    public  Class<? extends HttpApiProcessor<?>> getHttpApiProcessorClass(HttpApi api, HttpInterface httpInterface){
        if (httpInterface.processor().length > 0){
            return httpInterface.processor()[0];
        }
        if (api.processor().length > 0){
            return api.processor()[0];
        }
        return EmptyHttpApiProcessor.class;
    }

    public HttpApiProcessor<Annotation> buildRequestHttpApiProcessor(Class<? extends HttpApiProcessor<?>> apiProcessor){
        if (EmptyHttpApiProcessor.class.equals(apiProcessor)){
            return emptyHttpProcessor;
        }

        // 优先先从spring context获取,
        HttpApiProcessor<Annotation> processor = (HttpApiProcessor<Annotation>) SpringBeanContext.getMultiBean(apiProcessor);
        if (processor != null){
            return processor;
        }

        try {
            // 如果不存在则直接手动new一个 todo cache
            return (HttpApiProcessor<Annotation>) apiProcessor.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BaseUniHttpException(e);
        }
    }

    protected Type getBodyResultType() {
        Type bodyResultType = null;
        Method method = methodInvocation.getMethod();
        if (HttpResponse.class.isAssignableFrom(method.getReturnType())){
            Type genericReturnType = method.getGenericReturnType();
            if (genericReturnType instanceof ParameterizedType){
                Type actualTypeArgument = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                bodyResultType = actualTypeArgument;
            }
        }else {
            bodyResultType = method.getGenericReturnType();
        }
        return bodyResultType;
    }

    public Object invoke() {
        Method method = methodInvocation.getMethod();
        HttpMetadata httpMetadata = createHttpMetadata(methodInvocation);

        // check
        ParameterizedType paramTypeHttpApiProcessor = ClassUtil.getSuperInterfacesParameterizedType(apiProcessor, HttpApiProcessor.class);
        if (paramTypeHttpApiProcessor == null){
            throw new IllegalArgumentException(apiProcessor.getName() + " must be implement interface HttpApiProcessor");
        }
        Type actualTypeArgument = paramTypeHttpApiProcessor.getActualTypeArguments()[0];
        Annotation proxyAnnotation = annotationMeta.getProxyAnnotation();
        if (!actualTypeArgument.equals(Annotation.class) && !actualTypeArgument.equals(proxyAnnotation.annotationType())){
            throw new IllegalArgumentException("The specified HttpApiProcessor cannot handle this annotation type " + proxyAnnotation.annotationType().getSimpleName());
        }

        // before processor
        httpMetadata = requestProcessor.postBeforeHttpMetadata(httpMetadata,httpApiMethodInvocation);

        // sendHttpRequest processor
        HttpResponse<?> response = requestProcessor.postSendingHttpRequest(this,httpMetadata,httpApiMethodInvocation);

        // http response result processor
        Object result = requestProcessor.postAfterHttpResponseBodyResult(response.getBodyResult(), response, httpMetadata,httpApiMethodInvocation);
        ((AbstractHttpResponse<Object>)response).setBodyResult(result);
        Object methodReturnValue = HttpResponse.class.isAssignableFrom(method.getReturnType()) ? response : response.getBodyResult();

        // MethodReturnValue processor
        return requestProcessor.postAfterMethodReturnValue(methodReturnValue, response, httpMetadata,httpApiMethodInvocation);
    }

    public HttpResponse<?> sendHttpRequest(HttpMetadata httpMetadata){
        RequestMethod requestMethod = httpMetadata.getRequestMethod();
        HttpUrl httpUrl = httpMetadata.getHttpUrl();
        Map<String, String> headers = httpMetadata.getHeaders();

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder = requestBuilder.url(httpUrl.toUrl());

        // config header
        if (headers != null && !headers.isEmpty()){
            Headers.Builder headerBuilder = new Headers.Builder();
            headers.forEach(headerBuilder::add);
            requestBuilder = requestBuilder.headers(headerBuilder.build());
        }

        // config cookie
        String cookie = httpMetadata.getCookieString();
        if (StringUtils.isNotBlank(cookie)){
            requestBuilder.header("Cookie", cookie);
        }

        // config requestBody
        RequestBody requestBody = null;
        if (httpMetadata.getBody()!= null && !httpMetadata.getBody().emptyContent()){
            requestBody = convertToRequestBody(httpMetadata);
            requestBuilder = requestBuilder.post(requestBody);
        }

        if (requestBody == null && requestMethod.needBody()){
            requestBody = RequestBody.create(MediaType.parse(headers.get(HEADER_CONTENT_TYPE)), "");
        }

        requestBuilder = requestBuilder.method(httpMetadata.getRequestMethod().name(),requestBody);

        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful()){
                throw new SendHttpRequestException("Http请求异常 响应状态码【" + response.code()+"】结果:【"+response.body().string() + "】");
            }

            ResponseConvertContext responseConvertContext = new ResponseConvertContext();
            responseConvertContext.setResponse(response);
            responseConvertContext.setRequest(request);
            responseConvertContext.setHttpMetadata(httpMetadata);
            responseConvertContext.setMethodInvocation(httpApiMethodInvocation);
            responseConvertContext.setProcessor(requestProcessor);
            HttpResponse<?> httpResponse = responseChain.convert(responseConvertContext);
            if (httpResponse == null){
                throw new UniHttpResponseException("Unable to find a suitable converter to deserialize for response content-type " +  getResponseContentType(response));
            }
            return httpResponse;
        } catch (IOException e){
            throw new SendHttpRequestException("Http请求网络IO异常", e);
        } catch (SendHttpRequestException e){
            throw e;
        }catch (Exception e) {
            throw new SendHttpRequestException("Http请求异常", e);
        }
    }

    protected String getResponseContentType(Response response){
        return response.header(HEADER_CONTENT_TYPE);
    }

    private HttpMetadata createHttpMetadata(MethodInvocation methodInvocation) {
        return find(methodInvocation);
    }



    protected RequestBody convertToRequestBody(HttpMetadata metadata) {
        HttpBody body = metadata.getBody();
        if (body.emptyContent()){
            return null;
        }

        MediaType mediaTypeJson = MediaType.parse(metadata.getHeaders().get(HEADER_CONTENT_TYPE));
        RequestBody requestBody = null;
        if (body instanceof HttpBodyJSON){
            requestBody = RequestBody.create(mediaTypeJson,body.toStringBody());
        }else if (body instanceof HttpBodyBinary){
            InputStream inputStream = ((HttpBodyBinary) body).getFile();
            requestBody = RequestBody.create(mediaTypeJson,streamToByteArray(inputStream));
        }else if (body instanceof HttpBodyFormData){
            FormBody.Builder builder = new FormBody.Builder();
            HttpBodyFormData formDataBody = (HttpBodyFormData)body;
            formDataBody.getFormData().forEach(builder::add);
            requestBody = builder.build();
        }else if (body instanceof HttpBodyMultipart){
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);;
            HttpBodyMultipart multipartFormData = (HttpBodyMultipart)body;
            for (MultipartDataItem dataItem : multipartFormData.getMultiPartData()) {
                if (!dataItem.isFileFlag()){
                    builder.addFormDataPart(dataItem.getKey(),dataItem.getTextValue());
                }else {
                    File file = dataItem.getFileValue();
                    if (file == null){
                        continue;
                    }

                    RequestBody fileBody = RequestBody.create(MultipartBody.FORM, file);
                    builder.addFormDataPart(dataItem.getKey(), file.getName(), fileBody);
                }
            }
            requestBody = builder.build();
        }
        return requestBody;
    }



    public static byte[] streamToByteArray(InputStream is)  {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len;
            while ((len = is.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            byte[] array = bos.toByteArray();
            bos.close();
            return array;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }






}
