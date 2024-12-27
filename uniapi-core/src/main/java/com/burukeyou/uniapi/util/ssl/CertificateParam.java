package com.burukeyou.uniapi.util.ssl;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class CertificateParam implements Serializable  {

    /**
     *  certificate path or certificate content
     */
    private String certificate;

    /**
     *  The private key of the certificate
     */
    private String certificatePrivateKey;

    /**
     *  the keyStore format type，  such jks、PKCS12
     */
    private String keyStoreType;

    /**
     *  entry item alias name
     */
    private String keyAlias;

    /**
     *  the provider for the key store
     */
    private String keyStoreProvider;

    public CertificateParam() {
    }

    public CertificateParam(String certificate) {
        this.certificate = certificate;
    }
}
