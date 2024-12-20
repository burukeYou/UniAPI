package com.burukeyou.uniapi.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Http ssl Config
 *  All parameters here can be obtained from environment variables, i.e. configured using ${channel.ssl.xxx}
 *
 * @author caizhihao
 */

@Inherited
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SslCfg {

    /**
     *  whether to enable SSL support.
     */
    boolean enabled()  default true;

    /**
     * the supported SSL ciphers suite.
     */
    String[] ciphers() default {};

    /**
     * the enabled SSL protocols.  tls version
     */
    String[]  enabledProtocols() default {};

    /**
     * the alias that identifies the key in the key store.
     */
    String keyAlias()  default "";

    /**
     * the password used to access the key in the key store.
     */
    String keyPassword() default "";

    /**
     * the path to the key store that holds the SSL certificate (typically a jks file).
     */
    String keyStore() default "";

    /**
     * the password used to access the key store
     */
    String keyStorePassword()  default "";

    /**
     * the type of the key store
     */
    String keyStoreType() default "";

    /**
     * the provider for the key store.
     */
    String keyStoreProvider() default "";

    /**
     *  the location of the certificate or certificate content.
     */
    String certificate() default "";

    /**
     * the location of the private key for the certificate  or the private key content.
     */
    String certificatePrivateKey() default "";

    /**
     * the trust store that holds SSL certificates
     */
    String trustStore() default "";

    /**
     * the password used to access the trust store
     */
    String trustStorePassword()  default "";

    /**
     * the alias that identifies the key in the trust store.
     */
    String trustAlias() default "";

    /**
     * the type of the trust store
     */
    String trustStoreType() default "";

    /**
     * the provider for the trust store
     */
    String trustStoreProvider() default "";

    /**
     * the location of the trust certificate authority chain or the  certificate content.
     */
    String trustCertificate() default "";

    /**
     * the location of the private key for the trust certificate or the private key content
     */
    String trustCertificatePrivateKey() default "";

    /**
     * the SSL protocol to use
     */
    String protocol() default  "TLS";

    /**
     * whether to close hostname verify
     */
    boolean closeHostnameVerify()  default false;

    /**
     *  whether to close certificate trust verify , if true will trust all certificates
     */
    boolean closeCertificateTrustVerify() default false;

}
