package com.burukeyou.uniapi.http.core.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

/**
 * @author  caihzihao
 */
public class DefaultSslContextFactory implements SslContextFactory {

    private TrustManager trustAllTrustManager = new TrustAllX509ExtendedTrustManager();
    private HostnameVerifier trustAllHostnameVerifier = new TrustAllHostNameVerify();

    private SslConfig sslConfig;

    private final Set<KeyManager> keymanagers = new HashSet<>();
    private final Set<TrustManager> trustmanagers = new HashSet<>();
    private SecureRandom secureRandom;


    public DefaultSslContextFactory(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
    }

    @Override
    public SSLContext createSslContext()  {
        try {
            return createSslContext(sslConfig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SSLContext createSslContext(SslConfig sslConfig) throws Exception {
        String sslProtocol = sslConfig.getProtocol();
        final SSLContext sslcontext = SSLContext.getInstance(sslProtocol);

        String trustCertificate = sslConfig.getTrustCertificate();
        if (StringUtils.isNotBlank(trustCertificate)) {
            URL url = ResourceUtils.getURL(trustCertificate);
            TrustManager[] tmp = getTrustManagersByUrl(url, sslConfig.getTrustStorePassword());
            trustmanagers.addAll(Arrays.asList(tmp));
        }



        return sslcontext;
    }

    @Override
    public HostnameVerifier createHostnameVerifier() {
        if (Boolean.TRUE.equals(sslConfig.isCloseHostnameVerifier())){
            return trustAllHostnameVerifier;
        }
        // todo
        return null;
    }

    private KeyStore newKeyStore(String storeType){
        try {
            return KeyStore.getInstance(StringUtils.isNotBlank(storeType) ? storeType : KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }


    public TrustManager[] getTrustManagersByInputStream(final InputStream inputStream,
                                                        final String storePassword) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            trustStore.load(inputStream, storePassword.toCharArray());
        } finally {
            inputStream.close();
        }
        return getTrustManagers(trustStore);
    }

    public TrustManager[] getTrustManagersByFile(final File file, final String storePassword) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        if (file == null || !file.exists() || file.isDirectory()){
            String filePath = file == null ? "" : "in" + file.getAbsolutePath();
            throw new NullPointerException("truststore file must exist " + filePath);
        }
        return getTrustManagersByInputStream(Files.newInputStream(file.toPath()),storePassword);
    }

    public TrustManager[] getTrustManagersByUrl(final URL url, final String storePassword) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        Objects.requireNonNull(url, "Truststore URL");
        final InputStream inputStream = url.openStream();
        return getTrustManagersByInputStream(inputStream,storePassword);
    }

    private TrustManager[] getTrustManagers(final KeyStore truststore) throws NoSuchAlgorithmException, KeyStoreException {
        final TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(truststore);
        return tmfactory.getTrustManagers();
    }


    private KeyManager[] getKeyManagers(KeyStore keystore, String keyPassword) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        final KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, keyPassword.toCharArray());
        return kmfactory.getKeyManagers();
    }


    public KeyManager[] getKeyManagersByInputStream(InputStream inputStream, String storePassword, String keyPassword)  {
        try {
            final KeyStore identityStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try {
                identityStore.load(inputStream, storePassword.toCharArray());
            } finally {
                inputStream.close();
            }
            return getKeyManagers(identityStore, keyPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public KeyManager[] getKeyManagersByFile(File file, String storePassword, String keyPassword) throws IOException {
        if (file == null || !file.exists() || file.isDirectory()){
            String filePath = file == null ? "" : "in" + file.getAbsolutePath();
            throw new NullPointerException("truststore file must exist " + filePath);
        }
        return getKeyManagersByInputStream(Files.newInputStream(file.toPath()),storePassword,keyPassword);
    }
}
