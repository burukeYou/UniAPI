package com.burukeyou.uniapi.util.ssl;

import com.burukeyou.uniapi.util.EncodeUtil;
import com.burukeyou.uniapi.util.FileBizUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 *  create Ssl connection context info
 *
 * @author caizhihao
 */
public class SslUtil {

    private SslUtil(){}

    private static final String keyManagerAlgorithm = KeyManagerFactory.getDefaultAlgorithm(); // SunX509

    private static final String truststoreAlgorithm = TrustManagerFactory.getDefaultAlgorithm(); // PKIX

    public static SSLBuilder builder(){
        return new SSLBuilder();
    }

    /**
     * SSL builder
     */
    public static class SSLBuilder {

        /**
         *  JSSE key managers.
         */
        private Set<KeyManager> keyManagers;

        /**
         *  JSSE trust managers
         */
        private  Set<TrustManager> trustManagers;

        /**
         * cryptographically strong random number generator (RNG)
         */
        private SecureRandom secureRandom;

        /**
         *  ssl context protocol
         */
        private String protocol = "TLS";

        /**
         *   hostname verify
         */
        private HostnameVerifier hostnameVerifier;

        /**
         *   SSL ciphers suite.
         */
        private List<String> cipherSuites;

        /**
         * the enabled SSL/TLS protocols version
         */
        private List<String> enabledProtocols;

        public SSLBuilder() {
            this.keyManagers = new HashSet<>();
            this.trustManagers = new HashSet<>();
            this.cipherSuites = new ArrayList<>();
            this.enabledProtocols = new ArrayList<>();
        }

        public SSLBuilder setProtocol(final String protocol) {
            this.protocol = protocol;
            return this;
        }

        public SSLBuilder setSecureRandom(SecureRandom secureRandom) {
            this.secureRandom = secureRandom;
            return this;
        }

        public SSLBuilder setHostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }


        public SSLBuilder addCipherSuites(List<String> cipherSuites) {
            if (cipherSuites == null || cipherSuites.isEmpty()) {
                return this;
            }
            this.cipherSuites.addAll(cipherSuites);
            return this;
        }

        public SSLBuilder addEnabledProtocols(List<String> enabledProtocols) {
            if (enabledProtocols == null || enabledProtocols.isEmpty()) {
                return this;
            }
            this.enabledProtocols.addAll(enabledProtocols);
            return this;
        }

        public SSLBuilder clearAndSetTrustManagers(TrustManager...trustManagers) {
            if (trustManagers == null || trustManagers.length == 0) {
                return this;
            }
            this.trustManagers = new HashSet<>(Arrays.asList(trustManagers));
            return this;
        }

        public SSLBuilder addTrustCertificate(String certificate){
            this.addTrustStoreInfo(new CertificateParam(certificate));
            return this;
        }

        public SSLBuilder addTrustStoreInfo(CertificateParam certificateKey) {
            String certificate = certificateKey.getCertificate();
            if (StringUtils.isBlank(certificate)){
                return this;
            }
            try {
                TrustManager[] tmp = getTrustManagersByPath(certificate, certificateKey.getCertificatePrivateKey(), certificateKey.getKeyStoreType(),certificateKey.getKeyAlias(), certificateKey.getKeyStoreProvider());
                trustManagers.addAll(Arrays.asList(tmp));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public SSLBuilder addTrustStoreInfo(KeyStoreParam keyStoreKey) {
            String trustStore = keyStoreKey.getKeyStore();
            if (StringUtils.isBlank(trustStore)){
                return this;
            }
            try {
                TrustManager[] tmp;
                if (FileBizUtil.isFilePathSimilar(trustStore)){
                    // load for file path
                    URL url = ResourceUtils.getURL(trustStore);
                    tmp = getTrustManagersByUrl(url, keyStoreKey.getKeyStorePassword(), keyStoreKey.getKeyAlias(),keyStoreKey.getKeyStoreType(), keyStoreKey.getKeyStoreProvider());
                }else {
                    // load for content
                    byte[] trustStoreByte = EncodeUtil.base64DecodeToByte(trustStore);
                    tmp = getTrustManagersByInputStream(new ByteArrayInputStream(trustStoreByte),keyStoreKey.getKeyStorePassword(), keyStoreKey.getKeyAlias(),keyStoreKey.getKeyStoreType(), keyStoreKey.getKeyStoreProvider());
                }
                trustManagers.addAll(Arrays.asList(tmp));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public SSLBuilder addKeyStoreInfo(CertificateParam certificateKey) {
            String certificate = certificateKey.getCertificate();
            if (StringUtils.isBlank(certificate)){
                return this;
            }

            try {
                KeyManager[] tmp = getKeyManagersByPath(certificate, certificateKey.getCertificatePrivateKey(), certificateKey.getKeyStoreType(), certificateKey.getKeyAlias(), certificateKey.getKeyStoreProvider());
                keyManagers.addAll(Arrays.asList(tmp));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public SSLBuilder addKeyStoreInfo(KeyStoreParam keyStoreKey) {
            String keyStore = keyStoreKey.getKeyStore();
            if (StringUtils.isBlank(keyStore)){
                return this;
            }
            try {
                KeyManager[] tmp;
                if (FileBizUtil.isFilePathSimilar(keyStore)){
                    // load for file
                     URL url = ResourceUtils.getURL(keyStore);
                     tmp = getKeyManagersByUrl(url, keyStoreKey.getKeyStorePassword(), keyStoreKey.getKeyPassword(), keyStoreKey.getKeyAlias(),keyStoreKey.getKeyStoreType(), keyStoreKey.getKeyStoreProvider());
                }else {
                    // load for content
                    tmp = getKeyManagersByInputStream(new ByteArrayInputStream(EncodeUtil.base64DecodeToByte(keyStore)), keyStoreKey.getKeyStorePassword(), keyStoreKey.getKeyPassword(), keyStoreKey.getKeyAlias(),keyStoreKey.getKeyStoreType(), keyStoreKey.getKeyStoreProvider());
                }
                keyManagers.addAll(Arrays.asList(tmp));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this;
        }


        public SslContextInfo build() {
            if (StringUtils.isBlank(protocol)){
                protocol = "TLS";
            }
            SslContextInfo context = new SslContextInfo();

            try {
                SSLContext sslcontext = SSLContext.getInstance(protocol);
                initSSLContext(sslcontext, keyManagers, trustManagers, secureRandom);
                context.setSslContext(sslcontext);
                context.setKeyManagers(new ArrayList<>(keyManagers));
                context.setTrustManagers(new ArrayList<>(trustManagers));
                context.setHostnameVerifier(hostnameVerifier);
                context.setCipherSuites(cipherSuites);
                context.setEnableProtocols(enabledProtocols);
                return context;
            } catch (Exception e) {
                throw new RuntimeException("building SslConnectionContext fail ",e);
            }
        }
    }

    private static TrustManager[] getTrustManagersByPath(String certPath, String keyPath, String storeType, String keyAlias, String storeProvider) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore trustStore = createKeyStore(certPath, keyPath, storeType, keyAlias,storeProvider);
        return getTrustManagers(trustStore);
    }


    private static TrustManager[] getTrustManagers(final KeyStore truststore) throws NoSuchAlgorithmException, KeyStoreException {
        final TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(truststoreAlgorithm);
        tmfactory.init(truststore);
        return tmfactory.getTrustManagers();
    }

    private static KeyStore createKeyStore(String certPath, String keyPath, String storeType, String keyAlias,String storeProvider) {
        try {
            KeyStore keyStore = getKeyStoreInstance(storeType, storeProvider);
            keyStore.load(null);
            X509Certificate[] certificates = CertificateParserUtil.parseSmart(certPath);
            if (StringUtils.isNotBlank(certPath) && certificates.length == 0) {
                throw new IllegalArgumentException("Error loading certificates from " + certPath);
            }
            PrivateKey privateKey =  PrivateKeyParserUtil.parseSmart(keyPath);
            if (StringUtils.isNotBlank(keyPath) && privateKey == null) {
                throw new IllegalArgumentException("Error loading private key from " + keyPath);
            }
            try {
                String alias = StringUtils.isNotBlank(keyAlias)? keyAlias : "1";
                if (privateKey != null) {
                    keyStore.setKeyEntry(alias, privateKey, "".toCharArray(), certificates);
                } else {
                    for (int index = 0; index < certificates.length; index++) {
                        keyStore.setCertificateEntry(alias + "-" + index, certificates[index]);
                    }
                }
            } catch (KeyStoreException ex) {
                throw new IllegalStateException("Error adding certificates to KeyStore: " + ex.getMessage(), ex);
            }
            return keyStore;
        }
        catch (GeneralSecurityException | IOException ex) {
            throw new IllegalStateException("Error creating KeyStore: " + ex.getMessage(), ex);
        }
    }

    private static String base64DecodeFilter(String content) {
        if (StringUtils.isBlank(content) || content.startsWith("/") || content.startsWith("classpath") || content.startsWith("file") || content.startsWith("http")){
            return content;
        }
        try {
            content = content.trim();
            return new String(Base64.getDecoder().decode(content.getBytes()));
        } catch (Exception e) {
           return content;
        }
    }

    private static KeyStore getKeyStoreInstance(String storeType, String storeProvider) throws KeyStoreException, NoSuchProviderException {
        if (StringUtils.isBlank(storeType)){
            storeType = KeyStore.getDefaultType(); // jdk8 is jks , jdk11 is  pkcs12
        }
        return StringUtils.isBlank(storeProvider) ? KeyStore.getInstance(storeType) : KeyStore.getInstance(storeType, storeProvider);
    }

    private static TrustManager[] getTrustManagersByUrl(URL url, String storePassword, String keyAlias,String storeType, String storeProvider) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException {
        final InputStream inputStream = url.openStream();
        return getTrustManagersByInputStream(inputStream,storePassword,keyAlias,storeType,storeProvider);
    }

    private static TrustManager[] getTrustManagersByInputStream(InputStream inputStream,
                                                                String storePassword,
                                                                String keyAlias,
                                                                String storeType,
                                                                String storeProvider) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        // loading trust store
        final KeyStore trustStore = getKeyStoreInstance(storeType,storeProvider);
        try {
            trustStore.load(inputStream, storePassword.toCharArray());
        } finally {
            inputStream.close();
        }

        if (StringUtils.isBlank(keyAlias)){
            // loading all trustedCertEntry
            return getTrustManagers(trustStore);
        }

        if (!trustStore.isCertificateEntry(keyAlias)) {
            throw new IOException(" Alias name [" + keyAlias+"] does not identify a trustedCert Entry ");
        }

        Certificate certificate = trustStore.getCertificate(keyAlias);
        KeyStore keyStore = getKeyStoreInstance(storeType, storeProvider);
        keyStore.load(null);
        keyStore.setCertificateEntry(keyAlias, certificate);
        return getTrustManagers(keyStore);
    }


    /**
     *
     */

    private static KeyManager[] getKeyManagersByPath(String certPath, String keyPath, String storeType, String keyAlias,String storeProvider) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = createKeyStore(certPath, keyPath, storeType, keyAlias,storeProvider);
        return getKeyManagers(keyStore,"");
    }

    private static KeyManager[] getKeyManagers(KeyStore keystore, String keyPassword) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        final KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(keyManagerAlgorithm);
        kmfactory.init(keystore, keyPassword.toCharArray());
        return kmfactory.getKeyManagers();
    }

    private static KeyManager[] getKeyManagersByUrl(URL url,
                                                    String storePassword,
                                                    String keyPassword,
                                                    String keyAlias,
                                                    String storeType,
                                                    String storeProvider) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        final InputStream inputStream = url.openStream();
        return getKeyManagersByInputStream(inputStream,storePassword,keyPassword,keyAlias,storeType,storeProvider);
    }

    private static  KeyManager[] getKeyManagersByInputStream(InputStream inputStream, String storePassword, String keyPassword, String keyAlias,String storeType, String storeProvider)  {
        try {
            KeyStore keyStore = getKeyStoreInstance(storeType, storeProvider);
            try {
                keyStore.load(inputStream, storePassword.toCharArray());
            } finally {
                inputStream.close();
            }

            if (StringUtils.isBlank(keyAlias)) {
                // will get the first  private key entry
                return getKeyManagers(keyStore, keyPassword);
            }

            if (!keyStore.isKeyEntry(keyAlias)) {
                throw new IOException(" Alias name [" + keyAlias+"] does not identify a key entry");
            }

            // get PrivateKeyEntry CertificateChain (privateKey + CertificateChain)
            Certificate[] certificateChain = keyStore.getCertificateChain(keyAlias);
            Key k = keyStore.getKey(keyAlias, keyPassword.toCharArray());
            if (k == null) {
                throw new IOException(" Alias name [" + keyAlias+"] can not get private key from key store");
            }

            // clear info
            //keyStore.load(null,  null);
            KeyStore usekeyStore = getKeyStoreInstance(storeType, storeProvider);
            usekeyStore.load(null);
            usekeyStore.setKeyEntry(keyAlias, k, keyPassword.toCharArray(), certificateChain);
            return getKeyManagers(usekeyStore, keyPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void initSSLContext(SSLContext sslcontext, Collection<KeyManager> keyManagers, Collection<TrustManager> trustManagers, SecureRandom secureRandom) throws KeyManagementException {
        sslcontext.init(
                !keyManagers.isEmpty() ? keyManagers.toArray(new KeyManager[keyManagers.size()]) : null,
                !trustManagers.isEmpty() ? trustManagers.toArray(new TrustManager[trustManagers.size()]) : null,
                secureRandom
        );
    }

}
