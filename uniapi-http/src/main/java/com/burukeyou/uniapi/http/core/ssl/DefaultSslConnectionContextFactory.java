package com.burukeyou.uniapi.http.core.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.TrustManager;

import com.burukeyou.uniapi.http.core.ssl.hostnameverify.UniHttpHostnameVerifier;
import com.burukeyou.uniapi.util.ssl.CertificateParam;
import com.burukeyou.uniapi.util.ssl.KeyStoreParam;
import com.burukeyou.uniapi.util.ssl.SslContextInfo;
import com.burukeyou.uniapi.util.ssl.SslUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author  caihzihao
 */
@Getter
@Setter
public class DefaultSslConnectionContextFactory implements SslConnectionContextFactory {

    private static final TrustManager trustAllTrustManager = new TrustAllX509ExtendedTrustManager();

    private static final HostnameVerifier trustAllHostnameVerifier = new TrustAllHostNameVerify();

    private static final HostnameVerifier defaultHostnameVerifier = new UniHttpHostnameVerifier();

    @Override
    public SslConnectionContext create(SslConfig sslConfig) {
        if(!Boolean.TRUE.equals(sslConfig.isEnabled())){
            return null;
        }

        SslUtil.SSLBuilder builder = SslUtil.builder()
                .setProtocol(sslConfig.getProtocol())
                .addCipherSuites(sslConfig.getCiphers())
                .addEnabledProtocols(sslConfig.getEnabledProtocols());

        if (Boolean.TRUE.equals(sslConfig.isCloseHostnameVerify())){
            builder.setHostnameVerifier(trustAllHostnameVerifier);
        }else {
            builder.setHostnameVerifier(defaultHostnameVerifier);
        }
        initTrustStore(sslConfig, builder);
        initKeyStore(sslConfig, builder);
        SslContextInfo contextInfo = builder.build();
        return convert(contextInfo);
    }

    private SslConnectionContext convert(SslContextInfo contextInfo) {
        SslConnectionContext context = new SslConnectionContext();
        context.setSslContext(contextInfo.getSslContext());
        context.setKeyManagers(contextInfo.getKeyManagers());
        context.setTrustManagers(contextInfo.getTrustManagers());
        context.setHostnameVerifier(contextInfo.getHostnameVerifier());
        context.setCipherSuites(contextInfo.getCipherSuites());
        context.setEnableProtocols(contextInfo.getEnableProtocols());
        return context;
    }

    private void initKeyStore(SslConfig sslConfig, SslUtil.SSLBuilder builder) {
        // certificate
        String certificate = sslConfig.getCertificate();
        if (StringUtils.isNotBlank(certificate)) {
            CertificateParam certificateKey = CertificateParam.builder()
                    .certificate(certificate)
                    .certificatePrivateKey(sslConfig.getCertificatePrivateKey())
                    .keyStoreType(sslConfig.getKeyStoreType())
                    .keyAlias(sslConfig.getKeyAlias())
                    .keyStoreProvider(sslConfig.getKeyStoreProvider())
                    .build();
            builder.addKeyStoreInfo(certificateKey);
        }

        // keyStore
        String keyStore = sslConfig.getKeyStore();
        if (StringUtils.isNotBlank(keyStore)) {
            KeyStoreParam keyStoreKey = KeyStoreParam.builder()
                    .keyStore(keyStore)
                    .keyStorePassword(sslConfig.getKeyStorePassword())
                    .keyAlias(sslConfig.getKeyAlias())
                    .keyPassword(sslConfig.getKeyPassword())
                    .keyStoreType(sslConfig.getKeyStoreType())
                    .keyStoreProvider(sslConfig.getKeyStoreProvider())
                    .build();
            builder.addKeyStoreInfo(keyStoreKey);
        }
    }

    private void initTrustStore(SslConfig sslConfig, SslUtil.SSLBuilder builder) {
        boolean trustAllCertificates = sslConfig.isCloseCertificateTrustVerify();
        if (Boolean.TRUE.equals(trustAllCertificates)){
            builder.clearAndSetTrustManagers(trustAllTrustManager);
            return;
        }

        // Certificate
        String trustCertificate = sslConfig.getTrustCertificate();
        if (StringUtils.isNotBlank(trustCertificate)) {
            CertificateParam certificateKey = CertificateParam.builder()
                    .certificate(trustCertificate)
                    .certificatePrivateKey(sslConfig.getTrustCertificatePrivateKey())
                    .keyAlias(sslConfig.getTrustAlias())
                    .keyStoreType(sslConfig.getTrustStoreType())
                    .keyStoreProvider(sslConfig.getTrustStoreProvider()).build();
            builder.addTrustStoreInfo(certificateKey);
        }

        // trustStore
        String trustStore = sslConfig.getTrustStore();
        if (StringUtils.isNotBlank(trustStore)) {
            KeyStoreParam keyStoreKey = KeyStoreParam.builder()
                    .keyStore(trustStore)
                    .keyStorePassword(sslConfig.getTrustStorePassword())
                    .keyAlias(sslConfig.getTrustAlias())
                    .keyStoreType(sslConfig.getTrustStoreType())
                    .keyStoreProvider(sslConfig.getTrustStoreProvider())
                    .build();
            builder.addTrustStoreInfo(keyStoreKey);
        }
    }
}
