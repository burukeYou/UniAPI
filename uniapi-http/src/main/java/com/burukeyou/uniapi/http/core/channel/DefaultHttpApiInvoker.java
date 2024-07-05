package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.config.SpringBeanContext;
import com.burukeyou.uniapi.http.annotation.ResponseFile;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.exception.SendHttpRequestException;
import com.burukeyou.uniapi.http.core.request.HttpUrl;
import com.burukeyou.uniapi.http.core.request.*;
import com.burukeyou.uniapi.http.core.response.*;
import com.burukeyou.uniapi.http.extension.HttpApiProcessor;
import com.burukeyou.uniapi.http.support.DataApiConstant;
import com.burukeyou.uniapi.http.support.HttpApiAnnotationMeta;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import com.burukeyou.uniapi.http.support.RequestMethod;
import com.burukeyou.uniapi.support.BaseUtil;
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
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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

    private static OkHttpClient client;

    public DefaultHttpApiInvoker(HttpApiAnnotationMeta annotationMeta,
                                 Class<?> targetClass,
                                 HttpInterface httpInterface,
                                 MethodInvocation methodInvocation) {
        super(annotationMeta.getHttpApi(),httpInterface,annotationMeta.getProxySupport().getEnvironment());
        this.targetClass = targetClass;
        this.annotationMeta = annotationMeta;
        this.methodInvocation = methodInvocation;

        if (client == null){
            synchronized (DefaultHttpApiInvoker.class){
                if (client == null){
                    client = SpringBeanContext.getBean(OkHttpClient.class);
                }
            }
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

        Class<? extends HttpApiProcessor<?>> beforeProcessor = api.processor();
        HttpApiProcessor<Annotation> requestProcessor = null;
        requestProcessor =  (HttpApiProcessor<Annotation>) SpringBeanContext.getBean(api.processor());
        if (requestProcessor == null){
            throw new IllegalStateException("can not find " + beforeProcessor.getName() + "from spring context");
        }

        // check
        Type argument = BaseUtil.getSuperInterfaceActualTypeArguments(beforeProcessor)[0];
        Annotation proxyAnnotation = annotationMeta.getProxyAnnotation();
        if (!argument.equals(Annotation.class) && !argument.equals(proxyAnnotation.annotationType())){
            throw new IllegalArgumentException("指定的HttpRequestBeforeProcessor无法处理该注解类型" + proxyAnnotation.annotationType().getSimpleName());
        }

        // before processor
        httpMetadata = requestProcessor.postBefore(httpMetadata,param);

        // sendHttpRequest processor
        HttpResponse<?> response = requestProcessor.postSendHttpRequest(this,httpMetadata);

        // http response result processor
        Object result = requestProcessor.postAfterHttpResponseResult(response.getResult(), response, method, httpMetadata);
        ((AbstractHttpResponse<Object>)response).setResult(result);
        Object methodReturnValue = HttpResponse.class.isAssignableFrom(method.getReturnType()) ? response : response.getResult();

        // MethodReturnValue processor
        return requestProcessor.postAfterMethodReturnValue(methodReturnValue, response, method, httpMetadata);
    }


    private HttpMetadata createHttpMetadata(MethodInvocation methodInvocation) {
        return find(methodInvocation);
    }


    public HttpResponse sendHttpRequest(HttpMetadata httpMetadata){
        RequestMethod requestMethod = httpMetadata.getRequestMethod();
        HttpUrl httpUrl = httpMetadata.getHttpUrl();
        Map<String, String> oldHeaders = httpMetadata.getHeaders();

        Map<String, String> headers = new HashMap<>();
        oldHeaders.forEach((key,value) -> {
            try {
                headers.put(key, URLEncoder.encode(value,"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder = requestBuilder.url(httpUrl.toUrl());

        // config header
        if (headers != null && !headers.isEmpty()){
            Headers.Builder headerBuilder = new Headers.Builder();
            headers.forEach(headerBuilder::add);
            requestBuilder = requestBuilder.headers(headerBuilder.build());
        }

        // config cookie
        String cookie = httpMetadata.getCookie();
        if (StringUtils.isNotBlank(cookie)){
            requestBuilder.header("Cookie",cookie);
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
            return httpResponse;
        } catch (IOException e){
            throw new SendHttpRequestException("Http请求网络异常", e);
        } catch (SendHttpRequestException e){
            throw e;
        }catch (Exception e) {
            throw new SendHttpRequestException("Http请求异常", e);
        }
    }

    private AbstractHttpResponse doWithHttpFileResponse(Response response) {
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
        String saveDir = DataApiConstant.DEFAULT_FILE_SAVE_DIR;
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

    private AbstractHttpResponse doWithHttpBinaryResponse(Response response) throws IOException {
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
            requestBody = RequestBody.create(mediaTypeJson,body.toString());
        }else if (body instanceof HttpBodyBinary){
            InputStream inputStream = ((HttpBodyBinary) body).getFile();
            requestBody = RequestBody.create(mediaTypeJson,streamToByteArray(inputStream));
        }else if (body instanceof HttpBodyFormData){
            FormBody.Builder builder = new FormBody.Builder();
            HttpBodyFormData formDataBody = (HttpBodyFormData)body;
            formDataBody.getFormData().forEach(builder::add);
            requestBody = builder.build();
        }else if (body instanceof HttpBodyMultipartFormData){
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);;
            HttpBodyMultipartFormData multipartFormData = (HttpBodyMultipartFormData)body;
            for (MultipartFormDataItem dataItem : multipartFormData.getMultiPartData()) {
                if (!dataItem.isFileFlag()){
                    builder.addFormDataPart(dataItem.getKey(),dataItem.getValue());
                }else {
                    File file = dataItem.getFile();
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
