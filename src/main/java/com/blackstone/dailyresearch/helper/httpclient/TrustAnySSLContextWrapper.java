package com.blackstone.dailyresearch.helper.httpclient;

import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.log4j.Logger;

/**
 * desc: 信任任何证书的SSLContext
 *
 * @author 王彦锋
 * @date 2018/4/19 21:13
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class TrustAnySSLContextWrapper implements SSLContextWrapper {

    private static final Logger LOGGER = Logger.getLogger(TrustAnySSLContextWrapper.class);

    private static SSLContext sslcontext = createSSLContext();
    public static final TrustAnySSLContextWrapper INSTANCE = new TrustAnySSLContextWrapper();

    private TrustAnySSLContextWrapper() {}

    @Override
    public SSLContext getSSLContext() {
        return sslcontext;
    }

    private static SSLContext createSSLContext() {
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, new TrustManager[] {new TrustAnyTrustManager()}, new java.security.SecureRandom());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return sslcontext;
    }

    private static class TrustAnyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }
}
