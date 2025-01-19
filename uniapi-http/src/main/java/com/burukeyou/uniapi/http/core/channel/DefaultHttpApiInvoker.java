package com.burukeyou.uniapi.http.core.channel;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONPath;
import com.burukeyou.uniapi.config.SpringBeanContext;
import com.burukeyou.uniapi.http.annotation.*;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.exception.BaseUniHttpException;
import com.burukeyou.uniapi.http.core.exception.UniHttpIOException;
import com.burukeyou.uniapi.http.core.exception.UniHttpResponseDeserializeException;
import com.burukeyou.uniapi.http.core.exception.UniHttpRetryTimeOutException;
import com.burukeyou.uniapi.http.core.httpclient.response.OkHttpResponse;
import com.burukeyou.uniapi.http.core.request.HttpUrl;
import com.burukeyou.uniapi.http.core.request.*;
import com.burukeyou.uniapi.http.core.response.*;
import com.burukeyou.uniapi.http.core.retry.HttpRetry;
import com.burukeyou.uniapi.http.core.retry.HttpRetryStrategy;
import com.burukeyou.uniapi.http.core.serialize.json.JsonSerializeConverter;
import com.burukeyou.uniapi.http.core.serialize.xml.XmlSerializeConverter;
import com.burukeyou.uniapi.http.core.ssl.SslConfig;
import com.burukeyou.uniapi.http.extension.processor.EmptyHttpApiProcessor;
import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import com.burukeyou.uniapi.http.support.*;
import com.burukeyou.uniapi.http.support.config.HttpRetryConfig;
import com.burukeyou.uniapi.http.utils.BizUtil;
import com.burukeyou.uniapi.support.ClassUtil;
import com.burukeyou.uniapi.support.arg.MethodArgList;
import com.burukeyou.uniapi.support.arg.Param;
import com.burukeyou.uniapi.util.FileBizUtil;
import com.burukeyou.uniapi.util.ListsUtil;
import com.burukeyou.uniapi.util.StrUtil;
import com.burukeyou.uniapi.util.TimeUtil;
import com.jayway.jsonpath.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author caizhihao
 */
@Slf4j
public class DefaultHttpApiInvoker extends AbstractHttpMetadataParamFinder implements HttpSender {

    protected Class<?> targetClass;

    protected HttpApiAnnotationMeta annotationMeta;

    private final OkHttpClient client;

    private static final EmptyHttpApiProcessor emptyHttpProcessor = new EmptyHttpApiProcessor();

    private final Class<? extends HttpApiProcessor<?>> apiProcessorClass;
    private final HttpApiProcessor<Annotation> requestProcessor;

    private final HttpApiConfigContext apiConfigContext;

    private final Type bodyResultType;

    private FilterProcessor ignoredProcessorAnno;



    public DefaultHttpApiInvoker(HttpApiAnnotationMeta annotationMeta,
                                 Class<?> targetClass,
                                 HttpInterface httpInterface,
                                 MethodInvocation methodInvocation,
                                 OkHttpClient httpClient,
                                 XmlSerializeConverter xmlSerializeConverter,
                                 JsonSerializeConverter jsonSerializeConverter) {
        super(annotationMeta.getHttpApi(), httpInterface, annotationMeta.getProxySupport().getEnvironment());
        this.targetClass = targetClass;
        this.annotationMeta = annotationMeta;
        this.methodInvocation = methodInvocation;
        this.client = httpClient;

        this.apiProcessorClass = getHttpApiProcessorClass(api, httpInterface);
        this.requestProcessor = buildRequestHttpApiProcessor(apiProcessorClass);
        this.xmlSerializeConverter = xmlSerializeConverter;
        this.jsonSerializeConverter = jsonSerializeConverter;

        httpApiMethodInvocation = new HttpApiMethodInvocationImpl();
        httpApiMethodInvocation.setProxyApiAnnotation(annotationMeta.getProxyAnnotation());
        httpApiMethodInvocation.setProxyInterface(httpInterface);
        httpApiMethodInvocation.setProxyClass(targetClass);
        httpApiMethodInvocation.setMethodInvocation(methodInvocation);

        apiConfigContext = initHttpApiConfigContext();

        bodyResultType = httpApiMethodInvocation.getBodyResultType();
        ignoredProcessorAnno = methodInvocation.getMethod().getAnnotation(FilterProcessor.class);
    }

    public Class<? extends HttpApiProcessor<?>> getHttpApiProcessorClass(HttpApi api, HttpInterface httpInterface) {
        if (httpInterface.processor().length > 0) {
            return httpInterface.processor()[0];
        }
        if (api.processor().length > 0) {
            return api.processor()[0];
        }
        return EmptyHttpApiProcessor.class;
    }

    public HttpApiProcessor<Annotation> buildRequestHttpApiProcessor(Class<? extends HttpApiProcessor<?>> apiProcessor) {
        if (EmptyHttpApiProcessor.class.equals(apiProcessor)) {
            return emptyHttpProcessor;
        }

        // 优先先从spring context获取,
        HttpApiProcessor<Annotation> processor = (HttpApiProcessor<Annotation>) SpringBeanContext.getMultiBean(apiProcessor);
        if (processor != null) {
            return processor;
        }

        try {
            return (HttpApiProcessor<Annotation>) apiProcessor.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BaseUniHttpException(e);
        }
    }

    private boolean isOverrideProcessorMethod(ProcessorMethod processorMethod) {
        if (requestProcessor.getClass() == EmptyHttpApiProcessor.class){
            return false;
        }
        return getProcessorMethodOverrideFlag(requestProcessor,processorMethod.getMethodNames()[0]);
    }

    private boolean isProcessorMethod(ProcessorMethod methodName) {
        if (ignoredProcessorAnno == null) {
            return true;
        }
        if (ignoredProcessorAnno.ignoreAll()) {
            return false;
        }

        if (ignoredProcessorAnno.ignoreSending() && ProcessorMethod.SENDING_HTTP_REQUEST.equals(methodName)){
            return false;
        }

        for (ProcessorMethod ignoreMethod : ignoredProcessorAnno.excludeMethods()) {
            if (ignoreMethod.equals(methodName)) {
                return false;
            }
        }

        if (ignoredProcessorAnno.includeMethods().length > 0){
            // 指定了的则处理， 其他不指定都不处理
            for (ProcessorMethod processorMethod : ignoredProcessorAnno.includeMethods()) {
                if (processorMethod.equals(methodName)){
                    return true;
                }
            }
            return false;
        }

        // 未指定includeMethods 则都处理
        return true;
    }


    public Object invoke() throws Throwable  {
        Method method = methodInvocation.getMethod();
        UniHttpRequest requestMetadata = createHttpMetadata(methodInvocation);

        // check
        ParameterizedType paramTypeHttpApiProcessor = ClassUtil.getSuperInterfacesParameterizedType(apiProcessorClass, HttpApiProcessor.class);
        if (paramTypeHttpApiProcessor == null) {
            throw new IllegalArgumentException(apiProcessorClass.getName() + " must be implement interface HttpApiProcessor");
        }
        Type actualTypeArgument = paramTypeHttpApiProcessor.getActualTypeArguments()[0];
        Annotation proxyAnnotation = annotationMeta.getProxyAnnotation();
        if (!actualTypeArgument.equals(Annotation.class) && !actualTypeArgument.equals(proxyAnnotation.annotationType())) {
            throw new IllegalArgumentException("The specified HttpApiProcessor cannot handle this annotation type " + proxyAnnotation.annotationType().getSimpleName());
        }

        // check async
        Class<?> methodReturnType = method.getReturnType();
        boolean isAsync = Boolean.TRUE.equals(apiConfigContext.isAsyncRequest());
        if (isAsync && !Future.class.isAssignableFrom(methodReturnType) && void.class != methodReturnType && Void.class != methodReturnType){
            throw new IllegalStateException("when config async request, the method return type must be Future or void");
        }

        return doInvoke(requestMetadata);
    }

    private Object doInvoke(UniHttpRequest requestMetadata) throws Throwable {
        HttpRetryConfig retryConfig = apiConfigContext.getRetryConfig();
        boolean isAsync = Boolean.TRUE.equals(apiConfigContext.isAsyncRequest());
        boolean isRetry = retryConfig != null && retryConfig.getMaxAttempts() != 0;

        if (!isRetry){
            if (!isAsync){
                return doSyncInvoke(requestMetadata).getMethodReturnValue();
            }else {
                if (!postBeforeAllProcessor(requestMetadata)){
                    return null;
                }

                // request async
                CompletableFuture<UniHttpResponse> asyncFuture = sendAsyncHttpRequest(requestMetadata);
                final UniHttpRequest finalRequestMetadata = requestMetadata;
                return new HttpFuture<>(asyncFuture, bodyResultType, (info, ex, future) -> convertUniHttpResponse(new HttpRequestExecuteInfo(ex, info), finalRequestMetadata, future));
            }
        }

        // request sync for retry
        if (!isAsync){
            return doInvokeForSyncRetry(requestMetadata);
        }

        // request async for retry
        HttpFuture<Object> retryFuture = new HttpFuture<>();

        //
//        CompletableFuture.runAsync(() -> {
//            try {
//                Object o = doInvokeForSyncRetry(requestMetadata);
//                retryFuture.complete()
//            } catch (Throwable e) {
//                retryFuture.completeExceptionally(e);
//            }
//        });

        return retryFuture;
    }

    private Object doInvokeForSyncRetry(UniHttpRequest requestMetadata) throws Throwable {
        HttpRetryConfig retryConfig = apiConfigContext.getRetryConfig();
        HttpRetryStrategy<Object> retryStrategy = (HttpRetryStrategy<Object>)BizUtil.getBeanOrNew(retryConfig.getRetryStrategy());
        Long delay = retryConfig.getDelay();

        Integer maxAttempts = retryConfig.getMaxAttempts();
        Exception curException;
        Exception lastException = null;
        UniHttpResponseParseInfo curParseInfo;
        long executeCount = 0;
        while (true){
            curException = null;
            curParseInfo = null;
            executeCount++;
            long curRetryCount = executeCount -1;
            // 小于0一直重试，直到拿到结果
            if (maxAttempts > 0 && curRetryCount > maxAttempts){
                throw new UniHttpRetryTimeOutException("Exceeded the maximum retry count " + maxAttempts + ", stop retry",lastException);
            }
            boolean retryFlag = false;
            boolean isException =false;
            try {
                curParseInfo = doSyncInvoke(requestMetadata);
            } catch (Exception e) {
                lastException = e;
                curException = e;
                isException = true;
                if (ListsUtil.isEmpty(retryConfig.getInclude()) && ListsUtil.isEmpty(retryConfig.getExclude())){
                    retryFlag = true;
                }else {
                    if (ListsUtil.isNotEmpty(retryConfig.getInclude())){
                        retryFlag = retryConfig.isIncludeException(e.getClass());
                    }
                    if (ListsUtil.isNotEmpty(retryConfig.getExclude())){
                        retryFlag = !retryConfig.isExcludeException(e.getClass());
                    }
                }
            }

            if (!isException){
                retryFlag = retryStrategy.canRetry(executeCount,requestMetadata,curParseInfo.getUniHttpResponse(),curParseInfo.getBodyResult(),httpApiMethodInvocation);
            }

            if (!retryFlag){
                break;
            }

            if (delay > 0){
                Thread.sleep(delay);
            }
        }

        if (curException != null){
            throw curException;
        }

        return curParseInfo.getMethodReturnValue();

    }

    protected boolean postBeforeAllProcessor(UniHttpRequest requestMetadata){
        if (isProcessorMethod(ProcessorMethod.BEFORE_HTTP_REQUEST)){
            requestMetadata = requestProcessor.postBeforeHttpRequest(requestMetadata, httpApiMethodInvocation);
        }
        if (requestMetadata == null) {
            return false;
        }

        // before send
        if (isProcessorMethod(ProcessorMethod.BEFORE_SEND_HTTP_REQUEST)){
            requestProcessor.postBeforeSendHttpRequest(requestMetadata,this,httpApiMethodInvocation);
        }
        return true;
    }

    private UniHttpResponseParseInfo doSyncInvoke(UniHttpRequest requestMetadata) throws Throwable{
        if (!postBeforeAllProcessor(requestMetadata)){
            return new UniHttpResponseParseInfo();
        }

        // request sync
        return doAfterSyncInvoke(requestMetadata);
    }

    private UniHttpResponseParseInfo doAfterSyncInvoke(UniHttpRequest requestMetadata) throws Throwable {
        HttpRequestExecuteInfo executeInfo = new HttpRequestExecuteInfo();
        try {
            // post sending
            try {
                UniHttpResponse uniHttpResponse = null;
                if (isProcessorMethod(ProcessorMethod.SENDING_HTTP_REQUEST)){
                    uniHttpResponse = requestProcessor.postSendingHttpRequest(this, requestMetadata, httpApiMethodInvocation);
                }else {
                    uniHttpResponse = this.sendHttpRequest(requestMetadata);
                }
                executeInfo.setUniHttpResponse(uniHttpResponse);
            } catch (Throwable e) {
                Throwable throwable = e;
                if (throwable instanceof UniHttpIOException){
                    throwable = throwable.getCause();
                }
                executeInfo.setException(throwable);
            }
            return convertUniHttpResponse(executeInfo, requestMetadata,null);
        }finally {
            if(!InputStream.class.equals(bodyResultType)){
                BizUtil.closeQuietly(executeInfo.getUniHttpResponse());
            }
        }
    }


    /**
     *  response 转成 future之内类型
     */
    public UniHttpResponseParseInfo convertUniHttpResponse(HttpRequestExecuteInfo executeInfo, UniHttpRequest request,HttpFuture<Object> asyncFuture) throws Throwable {
        UniHttpResponse uniHttpResponse = executeInfo.getUniHttpResponse();

        // post after http response
        if (isProcessorMethod(ProcessorMethod.AFTER_HTTP_RESPONSE)){
            requestProcessor.postAfterHttpResponse(executeInfo.getException(),request,uniHttpResponse,httpApiMethodInvocation);
        }

        if (executeInfo.getException() != null) {
            // request error not response info
            return null;
        }

        Method method = httpApiMethodInvocation.getMethod();
        DefaultHttpResponse httpResponse = new DefaultHttpResponse(uniHttpResponse);

        // post after string
        BodyParseResult convertResult = parseBodyResult(uniHttpResponse);
        httpResponse.setOriginBodyPrintString(convertResult.getOriginBodyPrintString());

        // post after result
        Object bodyResult = convertResult.getBodyResult();

        if (isProcessorMethod(ProcessorMethod.AFTER_HTTP_RESPONSE_BODY_RESULT)){
            bodyResult = requestProcessor.postAfterHttpResponseBodyResult(bodyResult, uniHttpResponse, httpApiMethodInvocation);
        }
        httpResponse.setBodyResult(bodyResult);

        // convert to method return value
        // DTO、 HttpResponse<DTO>、Future<DTO>, Future<HttpResponse<DTO>>
        Object methodReturnValue = null;
        Class<?> methodReturnType = method.getReturnType();
        Class<?> currentClass = method.getReturnType();

        boolean isSyncFlag = !apiConfigContext.isAsyncRequest();

        if (!Future.class.isAssignableFrom(methodReturnType) && !HttpResponse.class.isAssignableFrom(methodReturnType)){
            methodReturnValue = httpResponse.getBodyResult();
        }else {
            boolean isFuture = false;
            Object innerResult = null;
            if (Future.class.isAssignableFrom(currentClass)){
                currentClass = getTypeClass(getParameterizedTypeFirst(method.getGenericReturnType()));
                isFuture = true;
            }
            if (HttpResponse.class.equals(currentClass)){
                innerResult = httpResponse;
            }else if (HttpFileResponse.class.equals(currentClass)){
                innerResult = new DefaultHttpFileResponse(uniHttpResponse, httpResponse.getOriginBodyPrintString(), httpResponse.getBodyResult());
            }

            if (isFuture){
                if (isSyncFlag){
                    methodReturnValue =  new HttpFuture<>(innerResult, httpResponse);
                }else {
                    methodReturnValue = asyncFuture;
                }
            }else {
                methodReturnValue = innerResult;
            }
        }

        // post after method
        if (isProcessorMethod(ProcessorMethod.AFTER_METHOD_RETURN_VALUE)){
            methodReturnValue = requestProcessor.postAfterMethodReturnValue(methodReturnValue, uniHttpResponse, httpApiMethodInvocation);
        }

        if (methodReturnValue != null && !methodReturnType.equals(Object.class) && Void.class != methodReturnType && void.class != methodReturnType){
             if (!methodReturnType.isInstance(methodReturnValue))    {
                 throw new ClassCastException("method return value class type err , can not from " +  methodReturnValue.getClass().getName() + " cast to " + methodReturnType.getName());
             }
        }

        UniHttpResponseParseInfo result = new UniHttpResponseParseInfo();
        result.setMethodReturnValue(methodReturnValue);
        result.setHttpResponse(httpResponse);
        result.setUniHttpResponse(uniHttpResponse);
        result.setBodyResult(bodyResult);
        return result;
    }



    public UniHttpResponse sendHttpRequest(UniHttpRequest uniHttpRequest) {
        Request request = initOkHttpRequest(uniHttpRequest);
        OkHttpClient callClient = getCallHttpClient(client, apiConfigContext);
        Call call = callClient.newCall(request);
        Response response = null;
        try {
            uniHttpRequest.setRequestTime(System.currentTimeMillis());
            response = call.execute();
        } catch (IOException e) {
            throw new UniHttpIOException(e);
        }
        return new UniHttpResponse(uniHttpRequest, new OkHttpResponse(response));
    }


    public CompletableFuture<UniHttpResponse> sendAsyncHttpRequest(UniHttpRequest uniHttpRequest) {
        Request request = initOkHttpRequest(uniHttpRequest);
        OkHttpClient callClient = getCallHttpClient(client, apiConfigContext);
        Call call = callClient.newCall(request);
        CompletableFuture<UniHttpResponse> completableFuture = new CompletableFuture<>();
        uniHttpRequest.setRequestTime(System.currentTimeMillis());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                completableFuture.completeExceptionally(e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                completableFuture.complete(new UniHttpResponse(uniHttpRequest, new OkHttpResponse(response)));
            }
        });
        return completableFuture;
    }

    private Request initOkHttpRequest(UniHttpRequest uniHttpRequest) {
        RequestMethod requestMethod = uniHttpRequest.getRequestMethod();
        HttpUrl httpUrl = uniHttpRequest.getHttpUrl();
        Map<String, String> headers = uniHttpRequest.getHeaders();

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder = requestBuilder.url(httpUrl.toUrl());

        // config header
        if (headers != null && !headers.isEmpty()) {
            Headers.Builder headerBuilder = new Headers.Builder();
            headers.forEach(headerBuilder::add);
            requestBuilder = requestBuilder.headers(headerBuilder.build());
        }

        // config cookie
        String cookie = uniHttpRequest.getCookiesToString();
        if (StringUtils.isNotBlank(cookie)){
            requestBuilder.header("Cookie", cookie);
        }

        // config requestBody
        RequestBody requestBody = null;
        if (uniHttpRequest.getBody()!= null && !uniHttpRequest.getBody().emptyContent()){
            requestBody = convertToRequestBody(uniHttpRequest);
            requestBuilder = requestBuilder.post(requestBody);
        }

        if (requestBody == null && requestMethod.needBody()){
            requestBody = RequestBody.create(MediaType.parse(uniHttpRequest.getContentType()), "");
        }

        requestBuilder = requestBuilder.method(uniHttpRequest.getRequestMethod().name(), requestBody);
        Request request = requestBuilder.build();
        return request;
    }

    private UniHttpRequest createHttpMetadata(MethodInvocation methodInvocation) {
        return find(methodInvocation);
    }


    protected RequestBody convertToRequestBody(UniHttpRequest metadata) {
        HttpBody body = metadata.getBody();
        if (body.emptyContent()) {
            return null;
        }

        MediaType mediaTypeJson = MediaType.parse(metadata.getContentType());
        RequestBody requestBody = null;
        if (body instanceof HttpBodyJSON) {
            requestBody = RequestBody.create(mediaTypeJson, body.toStringBody());
        } else if (body instanceof HttpBodyText) {
            requestBody = RequestBody.create(mediaTypeJson, body.toStringBody());
        }else if (body instanceof HttpBodyXML) {
            requestBody = RequestBody.create(mediaTypeJson, body.toStringBody());
        }else if (body instanceof HttpBodyBinary) {
            InputStream inputStream = ((HttpBodyBinary) body).getFile();
            requestBody = RequestBody.create(mediaTypeJson, streamToByteArray(inputStream));
        } else if (body instanceof HttpBodyFormData) {
            FormBody.Builder builder = new FormBody.Builder();
            HttpBodyFormData formDataBody = (HttpBodyFormData) body;
            formDataBody.getFormData().forEach(builder::add);
            requestBody = builder.build();
        } else if (body instanceof HttpBodyMultipart) {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            HttpBodyMultipart multipartFormData = (HttpBodyMultipart) body;
            for (MultipartDataItem dataItem : multipartFormData.getMultiPartData()) {
                if (!dataItem.isFileFlag()) {
                    builder.addFormDataPart(dataItem.getKey(), dataItem.getValueString());
                } else {
                    Object fileValue = dataItem.getFieldValue();
                    String fileName = dataItem.getFileName();
                    if (fileValue == null) {
                        continue;
                    }
                    if (fileValue instanceof File) {
                        File file = (File) fileValue;
                        RequestBody fileBody = RequestBody.create(MultipartBody.FORM, file);
                        builder.addFormDataPart(dataItem.getKey(), fileName, fileBody);
                    } else if (fileValue instanceof InputStream) {
                        InputStream inputStream = (InputStream) fileValue;
                        RequestBody fileBody = this.create(MultipartBody.FORM, inputStream);
                        builder.addFormDataPart(dataItem.getKey(), fileName, fileBody);
                    } else if (fileValue instanceof byte[]) {
                        byte[] bytes = (byte[]) fileValue;
                        RequestBody fileBody = RequestBody.create(MultipartBody.FORM, bytes);
                        builder.addFormDataPart(dataItem.getKey(), fileName, fileBody);
                    }

                }
            }
            requestBody = builder.build();
        }
        return requestBody;
    }

    /**
     * Returns a new request body that transmits the content of {@code file}.
     */
    public RequestBody create(final MediaType contentType, final InputStream inputStream) {
        if (inputStream == null) throw new NullPointerException("inputStream == null");
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return -1;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (Source source = Okio.source(inputStream)) {
                    sink.writeAll(source);
                }
            }
        };
    }


    public static byte[] streamToByteArray(InputStream is) {
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


    private HttpApiConfigContext initHttpApiConfigContext() {
        HttpApiConfigContext apiConfigContext = new HttpApiConfigContext();
        apiConfigContext.setHttpRequestConfig(getHttpRequestConfig());
        apiConfigContext.setHttpCallConfig(getHttpCallConfig());
        apiConfigContext.setSslConfig(getSslConfig());
        apiConfigContext.setHttpResponseConfig(getHttpResponseConfig());
        apiConfigContext.setRetryConfig(getHttpRetryConfig());
        return apiConfigContext;
    }

    private HttpRetryConfig getHttpRetryConfig() {
        HttpRetry anno = getMergeAnnotationFormObjectOrMethodCache(HttpRetry.class);
        if (anno == null) {
            return null;
        }
        HttpRetryConfig config = new HttpRetryConfig();
        config.setMaxAttempts(anno.maxAttempts());
        config.setDelay(anno.delay());
        config.setInclude(Arrays.asList(anno.include()));
        config.setExclude(Arrays.asList(anno.exclude()));
        config.setRetryStrategy(anno.retryStrategy());
        return config;
    }

    private HttpRequestConfig getHttpRequestConfig() {
        HttpRequestCfg anno = getMergeAnnotationFormObjectOrMethodCache(HttpRequestCfg.class);
        if (anno != null) {
            HttpRequestConfig config = new HttpRequestConfig();
            config.setAsync(anno.async());
            config.setFollowRedirect(anno.followRedirect());
            config.setFollowSslRedirect(anno.followSslRedirect());
            return config;
        }

        boolean async = httpInterface.async();
        if (Boolean.TRUE.equals(async)){
            HttpRequestConfig config = new HttpRequestConfig();
            config.setAsync(Boolean.TRUE);
            return config;
        }

        return null;
    }


    private HttpCallConfig getHttpCallConfig() {
        HttpCallCfg anno = getMergeAnnotationFormObjectOrMethodCache(HttpCallCfg.class);
        if (anno == null) {
            return null;
        }
        HttpCallConfig config = new HttpCallConfig();
        config.setCallTimeout(anno.callTimeout());
        config.setConnectTimeout(anno.connectTimeout());
        config.setWriteTimeout(anno.writeTimeout());
        config.setReadTimeout(anno.readTimeout());
        return config;
    }

    private HttpResponseConfig getHttpResponseConfig() {
        HttpResponseCfg anno = getMergeAnnotationFormObjectOrMethodCache(HttpResponseCfg.class);
        if (anno == null) {
            return null;
        }
        HttpResponseConfig config = new HttpResponseConfig();
        config.setJsonPathUnPack(getEnvironmentValueList(anno.jsonPathUnPack()));
        config.setAfterJsonPathUnPack(getEnvironmentValueList(anno.afterJsonPathUnPack()));
        config.setExtractJsonPath(getEnvironmentValue(anno.extractJsonPath()));
        return config;
    }

    private SslConfig getSslConfig() {
        SslCfg sslCfgAnno = getMergeAnnotationFormObjectOrMethodCache(SslCfg.class);
        if (sslCfgAnno == null) {
            return null;
        }
        Boolean enable = sslCfgAnno.enabled();
        if (!Boolean.TRUE.equals(enable)) {
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
        sslConfig.setCloseHostnameVerify(sslCfgAnno.closeHostnameVerify());
        sslConfig.setCloseCertificateTrustVerify(sslCfgAnno.closeCertificateTrustVerify());
        return sslConfig;
    }


    // ===============================================================================================
    private BodyParseResult parseBodyResult(UniHttpResponse responseMetadata) {
        BodyParseResult result = new BodyParseResult();
        boolean isFileResponse = responseMetadata.isFileResponse();
        String responseFileName = responseMetadata.getContentDispositionFileName();
        if (!isFileResponse) {
            result.setOriginBodyPrintString(responseMetadata.getBodyToString());
        } else {
            result.setOriginBodyPrintString(responseFileName);
        }

        if (!responseMetadata.isSuccessful()){
            result.setOriginBodyPrintString(responseMetadata.getBodyToString());
            return result;
        }

        if (InputStream.class.equals(bodyResultType)) {
            result.setBodyResult(new UniHttpInputStream(responseMetadata,responseMetadata.getBodyToInputStream()));
            if (isFileResponse) {
                result.setOriginBodyPrintString("【file InputStream】 fileName: " + responseFileName);
            }
            return result;
        }

        if (byte[].class.equals(bodyResultType)) {
            byte[] bodyToBytes = responseMetadata.getBodyToBytes();
            result.setBodyResult(bodyToBytes);
            if (isFileResponse) {
                result.setOriginBodyPrintString("【file byte】 fileName: " + responseFileName + " fileSize: " + bodyToBytes.length);
            }
            return result;
        }

        if (File.class.equals(bodyResultType)) {
            String savePath = getSavePath(responseMetadata, methodInvocation);
            if (StringUtils.isBlank(savePath)) {
                throw new IllegalArgumentException("when the return type is File, can not get the  file save path");
            }
            InputStream inputStream = responseMetadata.getBodyToInputStream();
            File file = FileBizUtil.saveFile(inputStream, savePath);
            result.setBodyResult(file);
            if (isFileResponse) {
                result.setOriginBodyPrintString("【file】 fileName: " + responseFileName + " savePath: " + savePath);
            }
            return result;
        }

        // unpack before
        String bodyString = responseMetadata.getBodyToString();
        if (StringUtils.isNotBlank(bodyString) && JSON.isValid(bodyString)) {
            List<String> jsonPathPackList = apiConfigContext.getJsonPathUnPackList();
            if (!jsonPathPackList.isEmpty()) {
                bodyString = unPackJsonPath(bodyString, jsonPathPackList);
            }
        }

        // post after body string
        if (isProcessorMethod(ProcessorMethod.AFTER_HTTP_RESPONSE_BODY_STRING)){
            bodyString = requestProcessor.postAfterHttpResponseBodyString(bodyString, responseMetadata, httpApiMethodInvocation);
        }

        // unpack after
        if (StringUtils.isNotBlank(bodyString) && JSON.isValid(bodyString)) {
            List<String> afterJsonStringFormatPath = apiConfigContext.getAfterJsonPathUnPackList();
            if (!afterJsonStringFormatPath.isEmpty()) {
                bodyString = unPackJsonPath(bodyString, afterJsonStringFormatPath);
            }
        }

        // extract json path
        HttpResponseConfig responseConfig = apiConfigContext.getHttpResponseConfig();
        if (StrUtil.isNotBlank(bodyString) && responseConfig != null && StrUtil.isNotBlank(responseConfig.getExtractJsonPath())){
            Object jsonPathValue = JSONPath.extract(bodyString, responseConfig.getExtractJsonPath());
            if (jsonPathValue == null){
                bodyString = "";
            }else {
                bodyString = jsonPathValue.toString();
            }
        }

        Object bodyResult = null;
        if (isProcessorMethod(ProcessorMethod.AFTER_HTTP_RESPONSE_BODY_STRING_DESERIALIZE) && isOverrideProcessorMethod(ProcessorMethod.AFTER_HTTP_RESPONSE_BODY_STRING_DESERIALIZE)){
            bodyResult = requestProcessor.postAfterHttpResponseBodyStringDeserialize(bodyString, bodyResultType, responseMetadata, httpApiMethodInvocation);
            result.setBodyResult(bodyResult);
            return result;
        }
        if (String.class.equals(bodyResultType)) {
            result.setBodyResult(bodyString);
            return result;
        }

        if (StringUtils.isBlank(bodyString)) {
            return result;
        }

        /// body result is null
        if (void.class == bodyResultType || Void.class == bodyResultType){
            return result;
        }

        if (Object.class.equals(bodyResultType) ||  WildcardType.class.isAssignableFrom(bodyResultType.getClass())) {
            result.setBodyResult(bodyString);
            return result;
        }

        if (JSON.isValid(bodyString)) {
            result.setBodyResult(parseBodyJsonStringToResultObject(bodyString));
            return result;
        }

        String contentType = responseMetadata.getContentType();
        if (isXml(contentType, bodyString)) {
            bodyResult = getXmlSerializeConverter().deserialize(bodyString, bodyResultType);
            result.setBodyResult(bodyResult);
            return result;
        }

        throw new UniHttpResponseDeserializeException("can not convert response body to " + bodyResultType.getTypeName() + " from response body string: " + bodyString);
    }


    private String unPackJsonPath(String originBodyString, List<String> jsonStringFormatPath) {
        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        DocumentContext documentContext = JsonPath.using(conf).parse(originBodyString);
        for (String jsonPath : jsonStringFormatPath) {
            try {
                documentContext.map(jsonPath, new MapFunction() {
                    @Override
                    public Object map(Object currentValue, Configuration configuration) {
                        if (isJsonString(currentValue)) {
                            return JSON.parse(currentValue.toString());
                        }
                        return currentValue;
                    }
                });
            } catch (PathNotFoundException e) {
                // ignore
            }
        }

        return documentContext.jsonString();
    }

    private boolean isJsonString(Object value) {
        return value != null && value.getClass().equals(String.class) && JSON.isValid(value.toString());
    }


    protected Type getBodyResultType(MethodInvocation methodInvocation) {
        Method method = methodInvocation.getMethod();
        Class<?> currentClass = method.getReturnType();
        Type currentType = method.getGenericReturnType();
        if (Future.class.isAssignableFrom(currentClass)){
            currentType = getParameterizedTypeFirst(currentType);
            currentClass = getTypeClass(currentType);
        }

        if (HttpResponse.class.isAssignableFrom(currentClass)) {
            currentType = getParameterizedTypeFirst(currentType);
        }
        return currentType;
    }

    private Type getParameterizedTypeFirst(Type type) {
      return  ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    private Class<?> getTypeClass(Type type){
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getTypeClass(((ParameterizedType) type).getRawType());
        }
        throw new IllegalStateException("can not get class for type " + type);
    }

    private String getSavePath(UniHttpResponse response, MethodInvocation methodInvocation) {
        String savePath = null;
        // 方法参数上获取
        ResponseFile responseFileAnno = null;
        for (Param methodArg : new MethodArgList(methodInvocation.getMethod(), methodInvocation.getArguments())) {
            responseFileAnno = methodArg.getAnnotation(ResponseFile.class);
            if (responseFileAnno == null) {
                continue;
            }

            if (!String.class.equals(methodArg.getType())) {
                throw new IllegalArgumentException("@ResponseFile use in method param must be marked on String type ");
            }
            savePath = (String) methodArg.getValue();
            break;
        }

        String fileName = response.getContentDispositionFileName();
        if (StringUtils.isBlank(fileName)) {
            fileName = TimeUtil.formatPure(LocalDateTime.now());
        }

        if (savePath != null) {
            if (isFileForPath(savePath)) {
                return savePath;
            }
            savePath = joinPath(savePath, fileName);
        }else {
            //
            String saveDir = UniHttpApiConstant.DEFAULT_FILE_SAVE_DIR;
            saveDir = getEnvironmentValue(saveDir);
            responseFileAnno = AnnotatedElementUtils.getMergedAnnotation(methodInvocation.getMethod(), ResponseFile.class);
            if (responseFileAnno != null) {
                saveDir = responseFileAnno.saveDir();
            }
            if (responseFileAnno == null || responseFileAnno.uuid()) {
                saveDir = joinPath(saveDir, getUUID());
            }
            if (saveDir.contains("{YYYYMMDD}")) {
                saveDir = saveDir.replace("{YYYYMMDD}", TimeUtil.formatPure(LocalDate.now()));
            }

            savePath = joinPath(saveDir, fileName);
        }

        if (responseFileAnno != null && !responseFileAnno.overwrite() && new File(savePath).exists()){
            throw new IllegalStateException("The file already exists and cannot be overwritten " + savePath);
        }

        return savePath;
    }

    protected boolean isFileForPath(String path) {
        return Paths.get(path).getFileName().toString().contains(".");
    }


    protected  String joinPath(Object... paths) {
        if(paths == null || paths.length == 0) {
            return "";
        }
        if (paths.length == 1) {
            return paths[0].toString();
        }

        List<String> cleanPaths = new ArrayList<>();
        String firstPath = paths[0].toString();
        // 第一个拆后不拆前
        if (firstPath.endsWith(File.separator)) {
            firstPath = firstPath.substring(0, firstPath.length() - 1);
        }
        for (int i = 1; i < paths.length; i++) {
            String path = paths[i].toString();
            if (path.startsWith(File.separator)) {
                path = path.substring(1);
            }
            if (path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }
            cleanPaths.add(path);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(firstPath).append(File.separator);
        for (String cleanPath : cleanPaths) {
            stringBuilder.append(cleanPath).append(File.separator);
        }
        return stringBuilder.toString();
    }

    protected String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public boolean isXml(String contentType, String bodyString) {
        if (contentType == null){
            contentType = "";
        }
        if (StrUtil.isBlank(bodyString)) {
            return false;
        }
        if (!bodyString.startsWith("<")){
            return false;
        }
        if (contentType.contains(MediaTypeEnum.APPLICATION_XML.getType())){
            return true;
        }
        if (bodyString.startsWith("<?xml")){
            return true;
        }
        return contentType.contains("xml");
    }

    private Object parseBodyJsonStringToResultObject(String bodyString) {
        JsonPathMapping methodJsonPathAnno = AnnotatedElementUtils.getMergedAnnotation(methodInvocation.getMethod(), JsonPathMapping.class);
        if (methodJsonPathAnno != null && !StrUtil.isBlank(methodJsonPathAnno.value())){
            Object extract = JSONPath.extract(bodyString, methodJsonPathAnno.value());
            if (extract == null){
                return null;
            }
            return deserializeJsonToObject(JSON.toJSONString(extract), bodyResultType);
        }


        Object bodyResult = deserializeJsonToObject(bodyString, bodyResultType);
        if (bodyResult == null){
            return null;
        }

        Class<?> bodyResultClass = bodyResult.getClass();
        boolean bindingFlag = bodyResultClass.isAnnotationPresent(ModelBinding.class) || methodInvocation.getMethod().isAnnotationPresent(ModelBinding.class);
        if(!bindingFlag || !isObject(bodyResultClass)){
            return bodyResult;
        }
        DocumentContext documentContext = JsonPath.parse(bodyString);
        populateResponseModel(bodyResultClass, bodyResult, documentContext);
        return bodyResult;
    }


}
