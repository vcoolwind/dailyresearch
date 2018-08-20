package com.blackstone.dailyresearch.helper.httpclient;

import javax.net.ssl.SSLContext;
import org.apache.http.ssl.SSLContexts;

/**
 * desc: HttpClient默认的SSLContext，仅仅是为了测试使用。
 *
 * @author 王彦锋
 * @date 2018/4/19 21:13
 */
public class DefaultSSLContextWrapper implements SSLContextWrapper {

    private static SSLContext sslcontext = createSSLContext();
    public static final DefaultSSLContextWrapper INSTANCE = new DefaultSSLContextWrapper();

    private DefaultSSLContextWrapper() {}

    @Override
    public SSLContext getSSLContext() {
        return sslcontext;
    }

    private static SSLContext createSSLContext() {
        return SSLContexts.createDefault();
    }

}
