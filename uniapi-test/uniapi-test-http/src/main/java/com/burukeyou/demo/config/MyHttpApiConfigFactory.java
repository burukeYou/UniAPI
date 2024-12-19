package com.burukeyou.demo.config;

import java.lang.annotation.Annotation;

import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.ssl.SslConfig;
import com.burukeyou.uniapi.http.extension.config.HttpApiConfigFactory;
import org.springframework.stereotype.Component;

@Component
public class MyHttpApiConfigFactory implements HttpApiConfigFactory {
    @Override

    public SslConfig getSslConfig(HttpApiMethodInvocation<Annotation> methodInvocation) {
        SslConfig sslConfig = new SslConfig();
        //sslConfig.setTrustCertificate("classpath:ssl2/ca_server.crt");
        //sslConfig.setTrustCertificatePrivateKey("classpath:ssl2/ca_server.key");
        //sslConfig.setTrustCertificate("classpath:ssl2/ca_server_no_san.crt");
        //sslConfig.setCloseCertificateTrustVerify(true);
        //sslConfig.setCertificate("classpath:ssl2/ca_client.pem");
        //sslConfig.setCertificate("classpath:ssl3/cert_chain.pem");
        //sslConfig.setCertificatePrivateKey("classpath:ssl2/ca_client.key");

        sslConfig.setTrustCertificate("classpath:ssl/server.crt");

        //sslConfig.setTrustStore("classpath:ssl2/ca_server.p12");
       // sslConfig.setTrustStore("classpath:ssl3/ca_server_multi_item_del01.p12");
        //sslConfig.setTrustStorePassword("123abcd");
        //sslConfig.setTrustStoreType("PKCS12");
        //sslConfig.setTrustAlias("sb01");

//        sslConfig.setKeyStore("classpath:ssl2/ca_client.pkcs12");
//        sslConfig.setKeyStorePassword("123abc");
//        sslConfig.setKeyPassword("123abc");


//        sslConfig.setKeyStore("classpath:ssl3/ca_server_multi_item.p12");
//        sslConfig.setKeyStorePassword("123abcd");
//        sslConfig.setKeyPassword("123abcd");
//        sslConfig.setKeyStoreType("PKCS12");
//        sslConfig.setKeyAlias("sb04");

        //sslConfig.setEnabledProtocols(Arrays.asList("TLSv1.2"));
        //sslConfig.setCiphers(Arrays.asList("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"));
        return sslConfig;
    }

}
