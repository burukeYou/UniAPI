package com.burukeyou.uniapi.http.core.channel;


import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.burukeyou.uniapi.exception.BaseUniApiException;
import com.burukeyou.uniapi.http.annotation.HttpCallConfig;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import okhttp3.OkHttpClient;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author caizhihao
 */
public abstract class AbstractInvokeCache {

    private static final Map<Method, HttpInterface> httpInterfaceCache = new ConcurrentHashMap<>();


    private static final Map<Method, OkHttpClient> httpClientCache = new ConcurrentHashMap<>();

    protected HttpInterface getHttpInterfaceInfo(Method method) {
        HttpInterface httpInterface = httpInterfaceCache.get(method);
        if (httpInterface == null){
            httpInterface = AnnotatedElementUtils.getMergedAnnotation(method, HttpInterface.class);
            if (httpInterface == null) {
                throw new BaseUniApiException("please mask @HttpInterface in this method " + method.getName() + "and config http path");
            }
            httpInterfaceCache.put(method,httpInterface);
        }
        return httpInterface;
    }

    protected OkHttpClient getCallHttpClient(Method method, OkHttpClient defaultClient) {
        OkHttpClient methodClient = httpClientCache.get(method);
        if (methodClient != null){
            return methodClient;
        }
        HttpCallConfig callConfigAnno = method.getAnnotation(HttpCallConfig.class);
        if (callConfigAnno == null){
            return defaultClient;
        }
        methodClient = defaultClient.newBuilder()
                .callTimeout(callConfigAnno.callTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(callConfigAnno.readTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(callConfigAnno.writeTimeout(), TimeUnit.MILLISECONDS)
                .connectTimeout(callConfigAnno.connectTimeout(), TimeUnit.MILLISECONDS)
                .build();
        httpClientCache.put(method,methodClient);
        return methodClient;
    }
}
