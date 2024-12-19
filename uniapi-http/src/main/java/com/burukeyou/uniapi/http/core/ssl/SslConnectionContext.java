package com.burukeyou.uniapi.http.core.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SslConnectionContext {

    private SSLContext sslContext;

    private List<KeyManager> keyManagers;

    private List<TrustManager> trustManagers;

    private HostnameVerifier hostnameVerifier;

    private List<String> cipherSuites;

    private List<String> enableProtocols;
}
