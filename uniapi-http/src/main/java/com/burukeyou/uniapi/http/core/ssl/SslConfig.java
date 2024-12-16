package com.burukeyou.uniapi.http.core.ssl;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * SSL configuration.
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
     * the supported SSL ciphers.
     */
    private String[] ciphers;

    /**
     * the enabled SSL protocols.
     */
    private String[] enabledProtocols;

    /**
     * the alias that identifies the key in the key store.
     */
    private String keyAlias;

    /**
     * the password used to access the key in the key store.
     */
    private String keyPassword;

    /**
     * the path to the key store that holds the SSL certificate (typically a jks file).
     */
    private String keyStore;

    /**
     * the password used to access the key store
     */
    private String keyStorePassword;

    /**
     * the type of the key store
     */
    private String keyStoreType;

    /**
     * the provider for the key store.
     */
    private String keyStoreProvider;

    /**
     * the trust store that holds SSL certificates
     */
    private String trustStore;

    /**
     * the password used to access the trust store
     */
    private String trustStorePassword;

    /**
     * the type of the trust store
     */
    private String trustStoreType;

    /**
     * the provider for the trust store
     */
    private String trustStoreProvider;

    /**
     *  the location of the certificate in PEM format.
     */
    private String certificate;

    /**
     * the location of the private key for the certificate in PEM format.
     */
    private String certificatePrivateKey;

    /**
     * the location of the trust certificate authority chain in PEM format
     */
    private String trustCertificate;

    /**
     * the location of the private key for the trust certificate in PEM format.
     */
    private String trustCertificatePrivateKey;

    /**
     * the SSL protocol to use
     */
    private String protocol = "TLS";

    /**
     *  do not verify the host address of the access
     */
    private boolean closeHostnameVerifier = false;

    /**
     *  whether to trust all certificates
     */
    private boolean trustAllCertificates = false;
}
