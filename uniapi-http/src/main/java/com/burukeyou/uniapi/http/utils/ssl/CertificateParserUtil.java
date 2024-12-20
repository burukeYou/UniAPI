

package com.burukeyou.uniapi.http.utils.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.burukeyou.uniapi.http.utils.BizUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

/**
 * Parser for X.509 certificates .
 *
 */
public class CertificateParserUtil {

	private static final String HEADER = "-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+";

	private static final String BASE64_TEXT = "([a-z0-9+/=\\r\\n]+)";

	private static final String FOOTER = "-+END\\s+.*CERTIFICATE[^-]*-+";

	private static final Pattern PATTERN = Pattern.compile(HEADER + BASE64_TEXT + FOOTER, Pattern.CASE_INSENSITIVE);

	private CertificateParserUtil() {
	}

	/**
	 * @param content		file path or file content
	 */
	public static X509Certificate[] parseSmart(String content) {
		if (StringUtils.isBlank(content)){
			return new X509Certificate[0];
		}
		if (isFilePath(content)){
			return parse(content);
		}
		content = base64DecodeFilter(content);
		return readCertificatesForContent(content).toArray(new X509Certificate[0]);
	}

	private static String base64DecodeFilter(String content) {
		try {
			content = content.trim();
			return new String(Base64.getDecoder().decode(content.getBytes()));
		} catch (Exception e) {
			return content;
		}
	}

	/**
	 * Rude judgment on whether it is a file path or not
	 */
	private static boolean isFilePath(String content) {
		return  BizUtil.isFilePath(content);
	}


	/**
	 * Load certificates from the specified resource.
	 * @param path the certificate to parse
	 * @return the parsed certificates
	 */
	public static X509Certificate[] parse(String path) {
		CertificateFactory factory = getCertificateFactory();
		List<X509Certificate> certificates = new ArrayList<>();
		readCertificates(path, factory, certificates::add);
		return certificates.toArray(new X509Certificate[0]);
	}

	private static CertificateFactory getCertificateFactory() {
		try {
			return CertificateFactory.getInstance("X.509");
		}
		catch (CertificateException ex) {
			throw new IllegalStateException("Unable to get X.509 certificate factory", ex);
		}
	}


	private static void readCertificates(String resource, CertificateFactory factory,
			Consumer<X509Certificate> consumer) {
		try {
			String text = readText(resource);
			Matcher matcher = PATTERN.matcher(text);
			while (matcher.find()) {
				String encodedText = matcher.group(1);
				byte[] decodedBytes = decodeBase64(encodedText);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
				while (inputStream.available() > 0) {
					consumer.accept((X509Certificate) factory.generateCertificate(inputStream));
				}
			}
		}
		catch (CertificateException | IOException ex) {
			throw new IllegalStateException("Error reading certificate from '" + resource + "' : " + ex.getMessage(),
					ex);
		}
	}

	private static List<X509Certificate> readCertificatesForContent(String text) {
		try {
			CertificateFactory factory = getCertificateFactory();
			List<X509Certificate> certificates = new ArrayList<>();
			Matcher matcher = PATTERN.matcher(text);
			while (matcher.find()) {
				String encodedText = matcher.group(1);
				byte[] decodedBytes = decodeBase64(encodedText);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
				while (inputStream.available() > 0) {
					X509Certificate x509Certificate = (X509Certificate) factory.generateCertificate(inputStream);
					certificates.add(x509Certificate);
				}
			}
			return certificates;
		}
		catch (CertificateException e) {
			throw new IllegalStateException("Error reading certificate from '" + "' : " + e.getMessage(),e);
		}
	}

	private static String readText(String resource) throws IOException {
		URL url = ResourceUtils.getURL(resource);
		try (Reader reader = new InputStreamReader(url.openStream())) {
			return FileCopyUtils.copyToString(reader);
		}
	}

	private static byte[] decodeBase64(String content) {
		byte[] bytes = content.replaceAll("\r", "").replaceAll("\n", "").getBytes();
		return Base64Utils.decode(bytes);
	}

}
