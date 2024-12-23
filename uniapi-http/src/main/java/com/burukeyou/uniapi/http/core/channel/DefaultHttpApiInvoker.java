package com.burukeyou.uniapi.http.core.channel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.burukeyou.uniapi.config.SpringBeanContext;
import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.HttpCallCfg;
import com.burukeyou.uniapi.http.annotation.HttpRequestCfg;
import com.burukeyou.uniapi.http.annotation.HttpResponseCfg;
import com.burukeyou.uniapi.http.annotation.SslCfg;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.conveter.response.HttpResponseBodyConverterChain;
import com.burukeyou.uniapi.http.core.conveter.response.HttpResponseConverter;
import com.burukeyou.uniapi.http.core.conveter.response.ResponseConvertContext;
import com.burukeyou.uniapi.http.core.exception.BaseUniHttpException;
import com.burukeyou.uniapi.http.core.exception.HttpResponseException;
import com.burukeyou.uniapi.http.core.exception.SendHttpRequestException;
import com.burukeyou.uniapi.http.core.exception.UniHttpResponseException;
import com.burukeyou.uniapi.http.core.http.request.OkHttpRequest;
import com.burukeyou.uniapi.http.core.http.response.OkHttpResponse;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpBodyBinary;
import com.burukeyou.uniapi.http.core.request.HttpBodyFormData;
import com.burukeyou.uniapi.http.core.request.HttpBodyJSON;
import com.burukeyou.uniapi.http.core.request.HttpBodyMultipart;
import com.burukeyou.uniapi.http.core.request.HttpBodyText;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.request.HttpUrl;
import com.burukeyou.uniapi.http.core.request.MultipartDataItem;
import com.burukeyou.uniapi.http.core.response.AbstractHttpResponse;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.core.ssl.SslConfig;
import com.burukeyou.uniapi.http.extension.processor.EmptyHttpApiProcessor;
import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import com.burukeyou.uniapi.http.support.HttpApiAnnotationMeta;
import com.burukeyou.uniapi.http.support.HttpApiConfigContext;
import com.burukeyou.uniapi.http.support.HttpCallConfig;
import com.burukeyou.uniapi.http.support.HttpRequestConfig;
import com.burukeyou.uniapi.http.support.HttpResponseConfig;
import com.burukeyou.uniapi.http.support.RequestMethod;
import com.burukeyou.uniapi.support.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

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


    private final Class<? extends HttpApiProcessor<?>> apiProcessorClass;
    private  final HttpApiProcessor<Annotation> requestProcessor;


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

        this.apiProcessorClass = getHttpApiProcessorClass(api,httpInterface);
        this.requestProcessor = buildRequestHttpApiProcessor(apiProcessorClass);

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
        ParameterizedType paramTypeHttpApiProcessor = ClassUtil.getSuperInterfacesParameterizedType(apiProcessorClass, HttpApiProcessor.class);
        if (paramTypeHttpApiProcessor == null){
            throw new IllegalArgumentException(apiProcessorClass.getName() + " must be implement interface HttpApiProcessor");
        }
        Type actualTypeArgument = paramTypeHttpApiProcessor.getActualTypeArguments()[0];
        Annotation proxyAnnotation = annotationMeta.getProxyAnnotation();
        if (!actualTypeArgument.equals(Annotation.class) && !actualTypeArgument.equals(proxyAnnotation.annotationType())){
            throw new IllegalArgumentException("The specified HttpApiProcessor cannot handle this annotation type " + proxyAnnotation.annotationType().getSimpleName());
        }

        // before processor
        httpMetadata = requestProcessor.postBeforeHttpMetadata(httpMetadata,httpApiMethodInvocation);
        if (httpMetadata == null){
            return null;
        }

        // sendHttpRequest processor
        HttpResponse<?> response = requestProcessor.postSendingHttpRequest(this,httpMetadata,httpApiMethodInvocation);

        // http response result processor
        Object result = requestProcessor.postAfterHttpResponseBodyResult(response.getBodyResult(), response, httpMetadata,httpApiMethodInvocation);
        ((AbstractHttpResponse<Object>)response).setBodyResult(result);
        Object methodReturnValue = HttpResponse.class.isAssignableFrom(method.getReturnType()) ? response : response.getBodyResult();

        // MethodReturnValue processor
        return requestProcessor.postAfterMethodReturnValue(methodReturnValue, response, httpMetadata,httpApiMethodInvocation);
    }

    public HttpResponse<?> sendHttpRequest(HttpMetadata httpMetadata)  {
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
        String cookie = httpMetadata.getCookiesToString();
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
            requestBody = RequestBody.create(MediaType.parse(httpMetadata.getContentType()), "");
        }

        requestBuilder = requestBuilder.method(httpMetadata.getRequestMethod().name(),requestBody);
        Request request = requestBuilder.build();

        HttpApiConfigContext apiConfigContext = getHttpApiConfigContext();
        OkHttpClient callClient = getCallHttpClient(client,apiConfigContext);
        Call call = callClient.newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful()){
                throw new HttpResponseException("Http请求响应异常 响应状态码【" + response.code()+"】结果:【"+response.body().string() + "】");
            }
            ResponseConvertContext responseConvertContext = new ResponseConvertContext();
            responseConvertContext.setRequest(new OkHttpRequest(request));
            responseConvertContext.setResponse(new OkHttpResponse(request,response));
            responseConvertContext.setHttpMetadata(httpMetadata);
            responseConvertContext.setMethodInvocation(httpApiMethodInvocation);
            responseConvertContext.setProcessor(requestProcessor);
            responseConvertContext.setHttpApi(api);
            responseConvertContext.setHttpInterface(httpInterface);
            responseConvertContext.setConfigContext(apiConfigContext);

            HttpResponse<?> httpResponse = responseChain.convert(responseConvertContext);
            if (httpResponse == null){
                throw new UniHttpResponseException("Unable to find a suitable converter to deserialize for response content-type " +  getResponseContentType(response));
            }
            return httpResponse;
        } catch (IOException e){
            throw new SendHttpRequestException("Http请求网络IO异常", e);
        } catch (HttpResponseException e){
            throw e;
        } catch (Exception e) {
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

        MediaType mediaTypeJson = MediaType.parse(metadata.getContentType());
        RequestBody requestBody = null;
        if (body instanceof HttpBodyJSON){
            requestBody = RequestBody.create(mediaTypeJson,body.toStringBody());
        } else if (body instanceof HttpBodyText) {
            requestBody = RequestBody.create(mediaTypeJson,body.toStringBody());
        } else if (body instanceof HttpBodyBinary){
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
                    builder.addFormDataPart(dataItem.getKey(),dataItem.getValueString());
                }else {
                    Object fileValue = dataItem.getFieldValue();
                    String fileName = dataItem.getFileName();
                    if (fileValue == null){
                        continue;
                    }
                    if (fileValue instanceof File){
                        File file = (File) fileValue;
                        RequestBody fileBody = RequestBody.create(MultipartBody.FORM, file);
                        builder.addFormDataPart(dataItem.getKey(), fileName, fileBody);
                    }else if (fileValue instanceof InputStream){
                        InputStream inputStream = (InputStream) fileValue;
                        RequestBody fileBody = this.create(MultipartBody.FORM, inputStream);
                        builder.addFormDataPart(dataItem.getKey(), fileName,fileBody);
                    }else if (fileValue instanceof byte[]){
                        byte[] bytes = (byte[]) fileValue;
                        RequestBody fileBody = RequestBody.create(MultipartBody.FORM, bytes);
                        builder.addFormDataPart(dataItem.getKey(), fileName,fileBody);
                    }

                }
            }
            requestBody = builder.build();
        }
        return requestBody;
    }

    /** Returns a new request body that transmits the content of {@code file}. */
    public  RequestBody create(final MediaType contentType, final InputStream inputStream) {
        if (inputStream == null) throw new NullPointerException("inputStream == null");
        return new RequestBody() {
            @Override public MediaType contentType() {
                return contentType;
            }

            @Override public long contentLength() {
                return -1;
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                try (Source source = Okio.source(inputStream)) {
                    sink.writeAll(source);
                }
            }
        };
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


    private HttpApiConfigContext getHttpApiConfigContext() {
        HttpApiConfigContext apiConfigContext = new HttpApiConfigContext();
        apiConfigContext.setHttpRequestConfig(getHttpRequestConfig());
        apiConfigContext.setHttpCallConfig(getHttpCallConfig());
        apiConfigContext.setSslConfig(getSslConfig());
        apiConfigContext.setHttpResponseConfig(getHttpResponseConfig());
        return apiConfigContext;
    }

    private HttpRequestConfig getHttpRequestConfig() {
        HttpRequestCfg anno = getMergeAnnotationFormObjectOrMethodCache(HttpRequestCfg.class);
        if (anno == null){
            return null;
        }
        HttpRequestConfig config = new HttpRequestConfig();
        config.setJsonPathPack(getEnvironmentValueList(anno.jsonPathPack()));
        return config;
    }


    private HttpCallConfig getHttpCallConfig() {
        HttpCallCfg anno = getMergeAnnotationFormObjectOrMethodCache(HttpCallCfg.class);
        if (anno == null){
            return null;
        }
        HttpCallConfig config = new HttpCallConfig();
        config.setCallTimeout(getEnvironmentValue(anno.callTimeout()));
        config.setConnectTimeout(getEnvironmentValue(anno.connectTimeout()));
        config.setWriteTimeout(getEnvironmentValue(anno.writeTimeout()));
        config.setReadTimeout(getEnvironmentValue(anno.readTimeout()));
        return config;
    }

    private HttpResponseConfig getHttpResponseConfig() {
        HttpResponseCfg anno = getMergeAnnotationFormObjectOrMethodCache(HttpResponseCfg.class);
        if (anno == null){
            return null;
        }
        HttpResponseConfig config = new HttpResponseConfig();
        config.setJsonPathUnPack(getEnvironmentValueList(anno.jsonPathUnPack()));
        return config;
    }

    private SslConfig getSslConfig() {
        SslCfg sslCfgAnno = getMergeAnnotationFormObjectOrMethodCache(SslCfg.class);
        if (sslCfgAnno == null) {
            return null;
        }
        Boolean enable = getEnvironmentValue(sslCfgAnno.enabled());
        if(!Boolean.TRUE.equals(enable)){
            return null;
        }
        SslConfig sslConfig = new SslConfig();
        sslConfig.setEnabled(enable);
        sslConfig.setCiphers(getEnvironmentValueList(sslCfgAnno.ciphers()));
        sslConfig.setEnabledProtocols(getEnvironmentValueList(sslCfgAnno.enabledProtocols()));
        sslConfig.setKeyAlias(getEnvironmentValue(sslCfgAnno.keyAlias()));
        sslConfig.setKeyPassword(getEnvironmentValue(sslCfgAnno.keyPassword()));
        sslConfig.setKeyStore(getEnvironmentValue(sslCfgAnno.keyStore()));
        sslConfig.setKeyStorePassword(getEnvironmentValue(sslCfgAnno.keyStorePassword()));
        sslConfig.setKeyStoreType(getEnvironmentValue(sslCfgAnno.keyStoreType()));
        sslConfig.setKeyStoreProvider(getEnvironmentValue(sslCfgAnno.keyStoreProvider()));
        sslConfig.setCertificate(getEnvironmentValue(sslCfgAnno.certificate()));
        sslConfig.setCertificatePrivateKey(getEnvironmentValue(sslCfgAnno.certificatePrivateKey()));
        sslConfig.setTrustStore(getEnvironmentValue(sslCfgAnno.trustStore()));
        sslConfig.setTrustStorePassword(getEnvironmentValue(sslCfgAnno.trustStorePassword()));
        sslConfig.setTrustAlias(getEnvironmentValue(sslCfgAnno.trustAlias()));
        sslConfig.setTrustStoreType(getEnvironmentValue(sslCfgAnno.trustStoreType()));
        sslConfig.setTrustStoreProvider(getEnvironmentValue(sslCfgAnno.trustStoreProvider()));
        sslConfig.setTrustCertificate(getEnvironmentValue(sslCfgAnno.trustCertificate()));
        sslConfig.setTrustCertificatePrivateKey(getEnvironmentValue(sslCfgAnno.trustCertificatePrivateKey()));
        sslConfig.setProtocol(getEnvironmentValue(sslCfgAnno.protocol()));
        sslConfig.setCloseHostnameVerify(getEnvironmentValue(sslCfgAnno.closeHostnameVerify()));
        sslConfig.setCloseCertificateTrustVerify(getEnvironmentValue(sslCfgAnno.closeCertificateTrustVerify()));
        return sslConfig;
    }




}
