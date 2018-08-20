package com.blackstone.dailyresearch.helper.httpclient;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.log4j.Logger;

/**
 * desc:快钱模拟证书
 *
 * @author 王彦锋
 * @date 2018/4/20 14:38
 */
public class Bill99SSLContextMockWrapper implements SSLContextWrapper {
    private static final Logger LOGGER = Logger.getLogger(Bill99SSLContextMockWrapper.class);
    private static SSLContext sslcontext = createSSLContext();
    public static final Bill99SSLContextMockWrapper INSTANCE = new Bill99SSLContextMockWrapper();

    private Bill99SSLContextMockWrapper() {}

    @Override
    public SSLContext getSSLContext() {
        return sslcontext;
    }

    private static SSLContext createSSLContext() {
        SSLContext sslcontext = null;
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            InputStream inputStream = Bill99SSLContextMockWrapper.class.getClassLoader().getResourceAsStream("bill99test.jks");
            ks.load(inputStream, "vpos123".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, "vpos123".toCharArray());
            sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(kmf.getKeyManagers(), new TrustManager[] {new MyX509TrustManager()}, null);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return sslcontext;
    }

    private static class MyX509TrustManager implements X509TrustManager {

        public MyX509TrustManager() {

        }

        /*
         * Delegate to the default trust manager.
         */
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        /*
         * Delegate to the default trust manager.
         */
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        /*
         * Merely pass this through.
         */
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[0];
        }
    }
}
