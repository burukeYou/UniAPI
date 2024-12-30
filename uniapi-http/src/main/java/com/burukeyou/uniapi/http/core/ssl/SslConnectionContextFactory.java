package com.burukeyou.uniapi.http.core.ssl;

import javax.net.ssl.X509TrustManager;

/**
 * @author caizhihao
 */
public interface SslConnectionContextFactory {

    X509TrustManager trustAllTrustManager = new TrustAllX509ExtendedTrustManager();


    SslConnectionContext create(SslConfig sslConfig);

}
