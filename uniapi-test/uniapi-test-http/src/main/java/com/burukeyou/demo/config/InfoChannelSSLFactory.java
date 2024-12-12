package com.burukeyou.demo.config;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import com.burukeyou.uniapi.http.extension.OkHttpClientFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Slf4j
@Component
public class InfoChannelSSLFactory implements OkHttpClientFactory {
    private  final OkHttpClient client;

    public InfoChannelSSLFactory() throws Exception {
        String certPath = "classpath:ssl2/ca.crt";
        File file = ResourceUtils.getFile(certPath);

        Certificate certificate = CertificateFactory.getInstance("X.509")
                .generateCertificate(Files.newInputStream(file.toPath()));

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry(Integer.toString(1), certificate);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        SSLSocketFactory socketFactory = sslContext.getSocketFactory();

        // 设置SSL
        this.client = new OkHttpClient.Builder()
                .hostnameVerifier(getHostnameVerifier())
                .sslSocketFactory(socketFactory, (X509TrustManager) tmf.getTrustManagers()[0])
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(20,10, TimeUnit.MINUTES))
                .build();
        log.info("UserChannelOkHttpClientFactory client:{}",client);
    }

    @Override
    public OkHttpClient getHttpClient() {
        return client;
    }

    public HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }
}
