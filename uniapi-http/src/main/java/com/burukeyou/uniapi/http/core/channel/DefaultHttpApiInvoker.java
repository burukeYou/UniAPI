package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.config.SpringBeanContext;
import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.ResponseFile;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.exception.SendHttpRequestException;
import com.burukeyou.uniapi.http.core.request.HttpUrl;
import com.burukeyou.uniapi.http.core.request.*;
import com.burukeyou.uniapi.http.core.response.*;
import com.burukeyou.uniapi.http.extension.EmptyHttpApiProcessor;
import com.burukeyou.uniapi.http.extension.HttpApiProcessor;
import com.burukeyou.uniapi.http.support.HttpApiAnnotationMeta;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import com.burukeyou.uniapi.http.support.RequestMethod;
import com.burukeyou.uniapi.http.support.UniHttpApiConstant;
import com.burukeyou.uniapi.support.ClassUtil;
import com.burukeyou.uniapi.support.arg.MethodArgList;
import com.burukeyou.uniapi.support.arg.Param;
import com.burukeyou.uniapi.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author  caizhihao
 */
@Slf4j
public class DefaultHttpApiInvoker extends AbstractHttpMetadataParamFinder implements HttpSender {

    protected Class<?> targetClass;

    protected HttpApiAnnotationMeta annotationMeta;

    private static final Pattern pattern = Pattern.compile("filename\\s*=\\s*\\\"(.*)\\\"");

    private OkHttpClient client;

    private static final EmptyHttpApiProcessor emptyHttpProcessor = new EmptyHttpApiProcessor();

    public DefaultHttpApiInvoker(HttpApiAnnotationMeta annotationMeta,
                                 Class<?> targetClass,
                                 HttpInterface httpInterface,
                                 MethodInvocation methodInvocation,OkHttpClient httpClient) {
        super(annotationMeta.getHttpApi(),httpInterface,annotationMeta.getProxySupport().getEnvironment());
        this.targetClass = targetClass;
        this.annotationMeta = annotationMeta;
        this.methodInvocation = methodInvocation;
        this.client = httpClient;
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
            //throw new IllegalStateException("can not find " + apiProcessor.getName() + " from spring context");
            return processor;
        }

        try {
            // 如果不存在则直接手动new一个
            return (HttpApiProcessor<Annotation>) apiProcessor.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object invoke() {
        Method method = methodInvocation.getMethod();
        HttpMetadata httpMetadata = createHttpMetadata(methodInvocation);
        HttpApiMethodInvocationImpl param = new HttpApiMethodInvocationImpl();
        param.setProxyApiAnnotation(annotationMeta.getProxyAnnotation());
        param.setProxyInterface(httpInterface);
        param.setProxyClass(targetClass);
        param.setMethodInvocation(methodInvocation);

        Class<? extends HttpApiProcessor<?>> apiProcessor = getHttpApiProcessorClass(api,httpInterface);

        HttpApiProcessor<Annotation> requestProcessor = buildRequestHttpApiProcessor(apiProcessor);

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
        httpMetadata = requestProcessor.postBeforeHttpMetadata(httpMetadata,param);

        // sendHttpRequest processor
        HttpResponse<?> response = requestProcessor.postSendingHttpRequest(this,httpMetadata,param);

        //  http response body string processor
        if (response instanceof HttpJsonResponse){
            HttpJsonResponse<?> jsonResponse = ((HttpJsonResponse<?>)response);
            String newJsonRsp = requestProcessor.postAfterHttpResponseBodyString(jsonResponse.getTextValue(), response, httpMetadata,param);
            jsonResponse.setTextValue(newJsonRsp);
        }

        // http response result processor
        Object result = requestProcessor.postAfterHttpResponseBodyResult(response.getBodyResult(), response, httpMetadata,param);
        ((AbstractHttpResponse<Object>)response).setBodyResult(result);
        Object methodReturnValue = HttpResponse.class.isAssignableFrom(method.getReturnType()) ? response : response.getBodyResult();

        // MethodReturnValue processor
        return requestProcessor.postAfterMethodReturnValue(methodReturnValue, response, httpMetadata,param);
    }


    private HttpMetadata createHttpMetadata(MethodInvocation methodInvocation) {
        return find(methodInvocation);
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
            requestBody = convertToRequestBody(httpMetadata.getBody());
            requestBuilder = requestBuilder.post(requestBody);
        }

        if (requestBody == null && requestMethod.needBody()){
            requestBody = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        requestBuilder = requestBuilder.method(httpMetadata.getRequestMethod().name(),requestBody);

        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful()){
                throw new SendHttpRequestException("Http请求异常 响应状态码【" + response.code()+"】结果:【"+response.body().string() + "】");
            }

            AbstractHttpResponse<?> httpResponse = null;
            Class<?> returnType = methodInvocation.getMethod().getReturnType();
            if (isFileDownloadResponse(response)){
                if (File.class.isAssignableFrom(returnType)){
                    httpResponse =  doWithHttpFileResponse(response);
                }else if (InputStream.class.isAssignableFrom(returnType)){
                    httpResponse = new HttpInputStreamResponse(response.body().byteStream(),getFileResponseName(response));
                }else {
                    httpResponse =  doWithHttpBinaryResponse(response);
                }
            }else {
                httpResponse = new HttpJsonResponse<>(response.body().string(),methodInvocation.getMethod());
            }
            httpResponse.setMethod(methodInvocation.getMethod());
            httpResponse.setRequest(request);
            httpResponse.setResponse(response);
            httpResponse.setHttpMetadata(httpMetadata);
            return httpResponse;
        } catch (IOException e){
            throw new SendHttpRequestException("Http请求网络异常", e);
        } catch (SendHttpRequestException e){
            throw e;
        }catch (Exception e) {
            throw new SendHttpRequestException("Http请求异常", e);
        }
    }

    private AbstractHttpResponse<?> doWithHttpFileResponse(Response response) {
        String savePath = getSavePath(response);
        ResponseBody responseBody = response.body();
        InputStream inputStream = responseBody.byteStream();

        createDirIfNotExist(Paths.get(savePath).getParent().toString());
        try {
            File file = new File(savePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            FileCopyUtils.copy(inputStream,fileOutputStream);
            return new HttpFileResponse(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getSavePath(Response response) {
        String savePath = null;
        // 方法参数上获取
        for (Param methodArg : new MethodArgList(methodInvocation.getMethod(),methodInvocation.getArguments())) {
            ResponseFile annotation = methodArg.getAnnotation(ResponseFile.class);
            if (annotation == null){
                continue;
            }

            if (!String.class.equals(methodArg.getType())){
               throw new IllegalArgumentException("@ResponseFile use in method param must be marked on String type ");
            }
            savePath = (String)methodArg.getValue();
        }

        String fileName = getFileResponseName(response);
        if (StringUtils.isBlank(fileName)){
            fileName = TimeUtil.formatPure(LocalDateTime.now());
        }

        if (savePath != null){
            if (isFileForPath(savePath)){
                return savePath;
            }
            return joinPath(savePath,fileName);
        }

        //
        String saveDir = UniHttpApiConstant.DEFAULT_FILE_SAVE_DIR;
        saveDir = getEnvironmentValue(saveDir);
        ResponseFile responseFileAnno = AnnotatedElementUtils.getMergedAnnotation(methodInvocation.getMethod(),ResponseFile.class);
        if (responseFileAnno != null){
            saveDir = responseFileAnno.saveDir();
        }
        if (responseFileAnno == null || responseFileAnno.uuid()){
            saveDir = joinPath(saveDir, getUUID());
        }
        if (saveDir.contains("{YYYYMMDD}")){
            saveDir = saveDir.replace("{YYYYMMDD}", TimeUtil.formatPure(LocalDate.now()));
        }

        savePath = joinPath(saveDir,fileName);
        return savePath;
    }

    private boolean isFileForPath(String path) {
        return Paths.get(path).getFileName().toString().contains(".");
    }

    private String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private AbstractHttpResponse<?> doWithHttpBinaryResponse(Response response) throws IOException {
        ResponseBody responseBody = response.body();
        String fileName = getFileResponseName(response);
        byte[] bytes = responseBody.bytes();
        return new HttpBinaryResponse(fileName, bytes);
    }


    protected boolean isFileDownloadResponse(Response response){
        String contentType = response.header("Content-Type");
        if (MediaTypeEnum.isFileDownLoadType(contentType)){
            return true;
        }

        // Content-Disposition: attachment; filename=xxx.txt
        String disposition = response.header("Content-Disposition");
        if(StringUtils.isNotBlank(disposition) && disposition.contains("attachment")){
            return true;
        }
        return false;
    }

    protected String getFileResponseName(Response response){
        String header = response.header("Content-Disposition");
        if(StringUtils.isBlank(header)){
            return null;
        }
        String fileName = null;
        Matcher matcher = pattern.matcher(header);
        if (matcher.find()){
            fileName = matcher.group(1);
        }
        return fileName;
    }


    protected RequestBody convertToRequestBody(HttpBody body) {
        if (body.emptyContent()){
            return null;
        }

        MediaType mediaTypeJson = MediaType.parse(body.getContentType());
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


    public  String joinPath(Object...paths){
        List<String> cleanPaths = new ArrayList<>();
        for (Object tmp : paths) {
            String path = tmp.toString();
            if (path.startsWith(File.separator)){
                path = path.substring(1);
            }
            if (path.endsWith(File.separator)){
                path = path.substring(0, path.length() - 1);
            }
            cleanPaths.add(path);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(File.separator);
        for (String cleanPath : cleanPaths) {
            stringBuilder.append(cleanPath).append(File.separator);
        }
        return stringBuilder.toString();
    }

    private static void createDirIfNotExist(String baseDir) {
        Path dir = Paths.get(baseDir);
        boolean notExists = Files.notExists(dir);
        if (notExists) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException("创建目录失败" + baseDir,e);
            }
        }
    }

}
