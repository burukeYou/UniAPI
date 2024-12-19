package com.burukeyou.uniapi.http.core.ssl;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * SSL configuration
 *
 * @author caizhihao
 */

@Getter
@Setter
public class SslConfig implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     *  whether to enable SSL support.
     */
    private boolean enabled = true;

    /**
     * the supported SSL ciphers suite.
     */
    private List<String> ciphers;

    /**
     * the enabled SSL protocols.  tls version
     */
    private List<String> enabledProtocols;

    /**
     * the alias that identifies the key in the key store.
     */
    private String keyAlias;

    /**
     * the password used to access the key in the key store.
     */
    private String keyPassword = "";

    /**
     * the path to the key store that holds the SSL certificate (typically a jks file).
     */
    private String keyStore;

    /**
     * the password used to access the key store
     */
    private String keyStorePassword = "";

    /**
     * the type of the key store
     */
    private String keyStoreType;

    /**
     * the provider for the key store.
     */
    private String keyStoreProvider;

    /**
     *  the location of the certificate or certificate content.
     */
    private String certificate;

    /**
     * the location of the private key for the certificate  or the private key content.
     */
    private String certificatePrivateKey;

    /**
     * the trust store that holds SSL certificates
     */
    private String trustStore;

    /**
     * the password used to access the trust store
     */
    private String trustStorePassword = "";

    /**
     * the alias that identifies the key in the trust store.
     */
    private String trustAlias;

    /**
     * the type of the trust store
     */
    private String trustStoreType;

    /**
     * the provider for the trust store
     */
    private String trustStoreProvider;

    /**
     * the location of the trust certificate authority chain or the  certificate content.
     */
    private String trustCertificate;

    /**
     * the location of the private key for the trust certificate or the private key content
     */
    private String trustCertificatePrivateKey;

    /**
     * the SSL protocol to use
     */
    private String protocol = "TLS";

    /**
     * whether to close hostname verify
     */
    private boolean closeHostnameVerify = false;

    /**
     *  whether to close certificate trust verify , if true will trust all certificates
     */
    private boolean closeCertificateTrustVerify = false;
}
