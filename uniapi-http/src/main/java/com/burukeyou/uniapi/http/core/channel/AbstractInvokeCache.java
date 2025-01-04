package com.burukeyou.uniapi.http.core.channel;


import com.burukeyou.uniapi.http.core.ssl.DefaultSslConnectionContextFactory;
import com.burukeyou.uniapi.http.core.ssl.SslConfig;
import com.burukeyou.uniapi.http.core.ssl.SslConnectionContext;
import com.burukeyou.uniapi.http.core.ssl.SslConnectionContextFactory;
import com.burukeyou.uniapi.http.support.HttpApiConfigContext;
import com.burukeyou.uniapi.http.support.HttpCallConfig;
import com.burukeyou.uniapi.http.support.HttpRequestConfig;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * @author caizhihao
 */
public abstract class AbstractInvokeCache {

    private static final Map<Method, OkHttpClient> httpClientCache = new ConcurrentHashMap<>();

    private static final Map<Method, Map<Class<? extends  Annotation>,Annotation>> methodAnnotationCache = new ConcurrentHashMap<>();

    private static final Map<Method, Set<Class<? extends  Annotation>>> methodNotAnnotationCache = new ConcurrentHashMap<>();

    protected static final SslConnectionContextFactory sslConnectionContextFactory = new DefaultSslConnectionContextFactory();

    protected HttpApiMethodInvocationImpl httpApiMethodInvocation;


    private  <T  extends Annotation> T getMergeAnnotationFormObjectOrMethod(Class<T> clz) {
        Method method = httpApiMethodInvocation.getMethod();
        T annotation = AnnotatedElementUtils.getMergedAnnotation(method, clz);
        if (annotation == null) {
            Class<?> proxyClass = httpApiMethodInvocation.getProxyClass();
            annotation =  AnnotatedElementUtils.getMergedAnnotation(proxyClass, clz);
        }
        return annotation;
    }

    protected  <T  extends Annotation> T getMergeAnnotationFormObjectOrMethodCache(Class<T> clz) {
        // check is not exist
        Method method = httpApiMethodInvocation.getMethod();
        Set<Class<? extends Annotation>> notExistSet = methodNotAnnotationCache.get(method);
        if (notExistSet != null && notExistSet.contains(clz)){
            return null;
        }

        // check from cache
        Map<Class<? extends Annotation>, Annotation> annoMap = methodAnnotationCache.get(method);
        if (annoMap != null && annoMap.containsKey(clz)){
            Annotation annotation = annoMap.get(clz);
            if (annotation != null){
                return (T)annotation;
            }
        }

        // check is contain
        T annotation = getMergeAnnotationFormObjectOrMethod(clz);
        if (annotation == null){
            // record this method not thi clz annotation
            methodNotAnnotationCache.putIfAbsent(method,new CopyOnWriteArraySet<>());
            notExistSet = methodNotAnnotationCache.get(method);
            if (notExistSet != null){
                notExistSet.add(clz);
            }
            return null;
        }
        methodAnnotationCache.putIfAbsent(method,new ConcurrentHashMap<>());
        methodAnnotationCache.get(method).put(clz,annotation);
        return annotation;
    }


    protected OkHttpClient getCallHttpClient(OkHttpClient defaultClient, HttpApiConfigContext apiConfigContext) {
        Method method = httpApiMethodInvocation.getMethod();
        OkHttpClient methodClient = httpClientCache.get(method);
        if (methodClient != null){
            return methodClient;
        }

        // check is not config
        if (apiConfigContext.isNotClientConfig()){
            return defaultClient;
        }
        OkHttpClient callHttpClient = createCallHttpClient(defaultClient, apiConfigContext);
        httpClientCache.put(method,callHttpClient);
        return callHttpClient;
    }
    private OkHttpClient createCallHttpClient(OkHttpClient defaultClient,HttpApiConfigContext apiConfigContext){
        OkHttpClient.Builder newBuilder = defaultClient.newBuilder();

        HttpCallConfig callConfig = apiConfigContext.getHttpCallConfig();
        if (callConfig != null){
            newBuilder.callTimeout(callConfig.getCallTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(callConfig.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(callConfig.getWriteTimeout(), TimeUnit.MILLISECONDS)
                    .connectTimeout(callConfig.getConnectTimeout(), TimeUnit.MILLISECONDS);
        }

        SslConfig sslConfig = apiConfigContext.getSslConfig();
        if (sslConfig != null && Boolean.TRUE.equals(sslConfig.isEnabled())){
            SslConnectionContext sslConnectionContext = sslConnectionContextFactory.create(sslConfig);
            configSslForOkhttp(sslConnectionContext, newBuilder);
        }

        HttpRequestConfig requestConfig = apiConfigContext.getHttpRequestConfig();
        if (requestConfig != null){
            if (requestConfig.getFollowRedirect() != null){
                newBuilder.followRedirects(requestConfig.getFollowRedirect());
            }
            if (requestConfig.getFollowSslRedirect() != null){
                newBuilder.followSslRedirects(requestConfig.getFollowSslRedirect());
            }
        }

        return newBuilder.build();
    }

    private static void configSslForOkhttp(SslConnectionContext sslConnectionContext, OkHttpClient.Builder newBuilder) {
        if (sslConnectionContext == null){
            return;
        }
        SSLContext sslContext = sslConnectionContext.getSslContext();
        List<TrustManager> trustManagers = sslConnectionContext.getTrustManagers();
        if (sslContext != null){
            TrustManager trustManager = trustManagers.stream().findFirst().orElse(null);
            if (trustManager != null){
                newBuilder.sslSocketFactory(sslContext.getSocketFactory(),(X509TrustManager) trustManager);
            }else{
                newBuilder.sslSocketFactory(sslContext.getSocketFactory(),SslConnectionContextFactory.trustAllTrustManager);
            }
        }

        // cipherSuites
        ConnectionSpec.Builder conSpecBuilder = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS);
        boolean flag = false;
        List<String> ciphers = sslConnectionContext.getCipherSuites();
        if (!CollectionUtils.isEmpty(ciphers)){
            conSpecBuilder.cipherSuites(ciphers.toArray(new String[0]));
            flag = true;
        }

        // tlsVersion protocols
        List<String> enabledProtocols = sslConnectionContext.getEnableProtocols();
        if (!CollectionUtils.isEmpty(enabledProtocols)){
            conSpecBuilder.tlsVersions(enabledProtocols.toArray(new String[0]));
            flag = true;
        }

        if (flag){
            newBuilder.connectionSpecs(Collections.singletonList(conSpecBuilder.build()));
        }

        // hostnameVerifier
        HostnameVerifier hostnameVerifier = sslConnectionContext.getHostnameVerifier();
        if (hostnameVerifier != null){
            newBuilder.hostnameVerifier(hostnameVerifier);
        }
    }
}
