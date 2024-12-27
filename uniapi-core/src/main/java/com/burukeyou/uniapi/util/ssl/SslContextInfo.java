package com.burukeyou.uniapi.util.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author caizhihao
 */
@Setter
@Getter
public class SslContextInfo {

    /**
     * SSLContext
     */
    private SSLContext sslContext;

    /**
     *  sslContext keyManagers
     */
    private List<KeyManager> keyManagers;

    /**
     * sslContext trustManagers
     */
    private List<TrustManager> trustManagers;

    /**
     *  hostnameVerifier
     */
    private HostnameVerifier hostnameVerifier;

    /**
     *  ssl cipher suites
     */
    private List<String> cipherSuites;

    /**
     *  enable tls version protocols
     */
    private List<String> enableProtocols;


}
