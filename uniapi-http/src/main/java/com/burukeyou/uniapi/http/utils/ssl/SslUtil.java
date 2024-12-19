package com.burukeyou.uniapi.http.utils.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.burukeyou.uniapi.http.core.ssl.SslConnectionContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

/**
 *  create Ssl connection context info
 *
 * @author caizhihao
 */
public class SslUtil {

    private static final String keyManagerAlgorithm = KeyManagerFactory.getDefaultAlgorithm(); // SunX509

    private static final String truststoreAlgorithm = TrustManagerFactory.getDefaultAlgorithm(); // PKIX

    public static SSLBuilder builder(){
        return new SSLBuilder();
    }

    public static class SSLBuilder {
        private Set<KeyManager> keyManagers;
        private  Set<TrustManager> trustManagers;
        private SecureRandom secureRandom;
        private String protocol = "TLS";

        private HostnameVerifier hostnameVerifier;

        private List<String> cipherSuites;

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
                URL url = ResourceUtils.getURL(trustStore);
                TrustManager[] tmp = getTrustManagersByUrl(url, keyStoreKey.getKeyStorePassword(), keyStoreKey.getKeyAlias(),keyStoreKey.getKeyStoreType(), keyStoreKey.getKeyStoreProvider());
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
                URL url = ResourceUtils.getURL(keyStore);
                KeyManager[] tmp = getKeyManagersByUrl(url, keyStoreKey.getKeyStorePassword(), keyStoreKey.getKeyPassword(), keyStoreKey.getKeyAlias(),keyStoreKey.getKeyStoreType(), keyStoreKey.getKeyStoreProvider());
                keyManagers.addAll(Arrays.asList(tmp));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this;
        }


        public SslConnectionContext build() {
            if (StringUtils.isBlank(protocol)){
                protocol = "TLS";
            }
            SslConnectionContext context = new SslConnectionContext();

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
            PrivateKey privateKey = (keyPath != null) ? PrivateKeyParserUtil.parseSmart(keyPath) : null;
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
