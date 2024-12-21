package com.burukeyou.uniapi.http.core.channel;


import com.burukeyou.uniapi.exception.BaseUniApiException;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.ssl.*;
import com.burukeyou.uniapi.http.support.HttpApiConfigContext;
import com.burukeyou.uniapi.http.support.HttpCallConfig;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author caizhihao
 */
public abstract class AbstractInvokeCache {

    private static final Map<Method, HttpInterface> httpInterfaceCache = new ConcurrentHashMap<>();


    private static final Map<Method, OkHttpClient> httpClientCache = new ConcurrentHashMap<>();

    protected static final SslConnectionContextFactory sslConnectionContextFactory = new DefaultSslConnectionContextFactory();

    protected HttpApiMethodInvocationImpl httpApiMethodInvocation;

    protected  <T  extends Annotation> T getMergeAnnotationFormObjectOrMethod(HttpApiMethodInvocation<Annotation> methodInvocation, Class<T> clz) {
        Method method = methodInvocation.getMethod();
        T annotation = AnnotatedElementUtils.getMergedAnnotation(method, clz);
        if (annotation == null) {
            Class<?> proxyClass = httpApiMethodInvocation.getProxyClass();
            annotation =  AnnotatedElementUtils.getMergedAnnotation(proxyClass, clz);
        }
        return annotation;
    }

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

    protected OkHttpClient getCallHttpClient(OkHttpClient defaultClient, HttpApiConfigContext apiConfigContext) {
        Method method = httpApiMethodInvocation.getMethod();
        OkHttpClient methodClient = httpClientCache.get(method);
        if (methodClient != null){
            return methodClient;
        }

        if (apiConfigContext.isNotClientConfig()){
            return defaultClient;
        }
        OkHttpClient callHttpClient = createCallHttpClient(defaultClient, apiConfigContext);
        httpClientCache.put(method,callHttpClient);
        return callHttpClient;
    }
    private OkHttpClient createCallHttpClient(OkHttpClient defaultClient,HttpApiConfigContext apiConfigContext){
        OkHttpClient.Builder newBuilder = defaultClient.newBuilder();

        HttpCallConfig callConfig = apiConfigContext.getHttpCallMeta();
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
                newBuilder.sslSocketFactory(sslContext.getSocketFactory(),new TrustAllX509ExtendedTrustManager());
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
