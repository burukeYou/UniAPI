package com.burukeyou.uniapi.http.core.channel;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.burukeyou.uniapi.exception.BaseUniApiException;
import com.burukeyou.uniapi.http.annotation.HttpCallConfig;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.ssl.DefaultSslConnectionContextFactory;
import com.burukeyou.uniapi.http.core.ssl.SslConfig;
import com.burukeyou.uniapi.http.core.ssl.SslConnectionContext;
import com.burukeyou.uniapi.http.core.ssl.SslConnectionContextFactory;
import com.burukeyou.uniapi.http.core.ssl.TrustAllX509ExtendedTrustManager;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author caizhihao
 */
public abstract class AbstractInvokeCache {

    private static final Map<Method, HttpInterface> httpInterfaceCache = new ConcurrentHashMap<>();


    private static final Map<Method, OkHttpClient> httpClientCache = new ConcurrentHashMap<>();

    protected static final SslConnectionContextFactory sslConnectionContextFactory = new DefaultSslConnectionContextFactory();

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

    protected OkHttpClient getCallHttpClient(HttpApiMethodInvocation<?> methodInvocation, OkHttpClient defaultClient, SslConfig sslConfig) {
        Method method = methodInvocation.getMethod();
        OkHttpClient methodClient = httpClientCache.get(method);
        if (methodClient != null){
            return methodClient;
        }
        OkHttpClient callHttpClient = createCallHttpClient(methodInvocation, defaultClient, sslConfig);
        if (callHttpClient == null){
            return defaultClient;
        }
        httpClientCache.put(method,callHttpClient);
        return callHttpClient;
    }
    private OkHttpClient createCallHttpClient(HttpApiMethodInvocation<?> methodInvocation,  OkHttpClient defaultClient,SslConfig sslConfig){
        Method method = methodInvocation.getMethod();
        HttpCallConfig callConfigAnno = method.getAnnotation(HttpCallConfig.class);
        if (callConfigAnno == null && sslConfig == null){
            return null;
        }

        OkHttpClient.Builder newBuilder = defaultClient.newBuilder();
        if (callConfigAnno != null){
                    newBuilder.callTimeout(callConfigAnno.callTimeout(), TimeUnit.MILLISECONDS)
                              .readTimeout(callConfigAnno.readTimeout(), TimeUnit.MILLISECONDS)
                              .writeTimeout(callConfigAnno.writeTimeout(), TimeUnit.MILLISECONDS)
                              .connectTimeout(callConfigAnno.connectTimeout(), TimeUnit.MILLISECONDS);
        }

        if (sslConfig != null){
            SslConnectionContext sslConnectionContext = sslConnectionContextFactory.create(sslConfig);
            configSslForOkhttp(sslConnectionContext, newBuilder);
        }

        return newBuilder.build();
    }

    private static void configSslForOkhttp(SslConnectionContext sslConnectionContext, OkHttpClient.Builder newBuilder) {
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
