package com.blackstone.dailyresearch.helper;

import com.blackstone.dailyresearch.helper.httpclient.SSLContextWrapper;
import com.blackstone.dailyresearch.helper.httpclient.TrustAnySSLContextWrapper;
import com.blackstone.dailyresearch.util.CloseUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * desc:HttpClient 4.5.X封装工具类。
 *
 * @author 王彦锋
 * @date 2018/4/19 21:04
 */
public class HttpClientHelper {

    private static final Logger LOGGER = Logger.getLogger(HttpClientHelper.class);
    public static final String UTF8 = "UTF-8";
    public static final String GBK = "GBK";
    public static final String APPLICATION_JSON = "application/json";
    public static final String TEXT_XML = "text/xml";

    /**
     * 链接总数
     */
    private static int MAX_TOTAL = 1000;

    /**
     * 每个主机最大的并发数
     */
    private static int DEFAULT_MAX_PERROUTE = 100;

    /**
     * 从连接池中获取到连接的最长时间（默认5秒钟）
     */
    private static int REQUEST_CONN_TIMEOUT = 5000;

    /**
     * 创建连接的最长时间，链接超时
     */
    private static int CONN_TIMEOUT = 60 * 1000;

    /**
     * 数据传输的最长时间，读超时
     */
    private static int SOCKET_TIMEOUT = 10 * 60 * 1000;

    private static RequestConfig defaultRequestConfig = null;

    private static Map<String, HttpClientBuilder> httpClientBuilderMap
        = new ConcurrentHashMap<String, HttpClientBuilder>();

    static {
        init();
    }

    private static void init() {
        try {
            // 添加一个默认的
            getWithAddHttpClientBuilder(TrustAnySSLContextWrapper.INSTANCE);
            // 设置请求参数
            RequestConfig.Builder defaultConfigBuilder = RequestConfig.custom();
            defaultConfigBuilder.setConnectionRequestTimeout(REQUEST_CONN_TIMEOUT);
            defaultConfigBuilder.setSocketTimeout(SOCKET_TIMEOUT);
            defaultConfigBuilder.setConnectTimeout(CONN_TIMEOUT);
            defaultRequestConfig = defaultConfigBuilder.build();
        } catch (Throwable e) {
            LOGGER.error("init error", e);
        }
    }

    /**
     * get方式获取内容，使用默认的信任所有证书策略
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public static String getData(String url,String charset) throws IOException {
        return getData(url, charset,null);
    }

    /**
     * get方式获取内容,使用指定的证书策略
     * 
     * @param url
     * @param contextWrapper
     * @return
     * @throws Exception
     */
    public static String getData(String url,String charset, SSLContextWrapper contextWrapper) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        // 设置请求参数
        httpGet.setConfig(defaultRequestConfig);
        return httpExecute(httpGet, charset, contextWrapper);
    }

    /**
     * 使用post+form方式提交数据，并获取响应。使用默认信任策略，默认编码GBK。
     * 该方法不推荐使用，建议使用明确的指定字符集，避免不必要的字符集导致乱码的麻烦。
     * 
     * @param url
     * @param formMap
     * @return
     * @throws Exception
     */
    @Deprecated
    public static String postData(String url, Map<String, String> formMap) throws IOException {
        return postData(url, formMap, "GBK");
    }

    /**
     * 使用post+form方式提交数据，并获取响应。使用默认信任策略。
     * 
     * @param url
     * @param formMap
     * @param charset 提交指定的charSet，响应时优先从响应报文读取字符集，没有的话，也是用该字符集。
     * @return
     * @throws Exception
     */
    public static String postData(String url, Map<String, String> formMap, String charset) throws IOException {
        return postData(url, formMap, charset, null, null);
    }

    /**
     * 使用post+form方式提交数据，并获取响应数据流。使用默认信任策略。
     *
     * @param url
     * @param formMap
     * @param charset 提交指定的charSet，响应时优先从响应报文读取字符集，没有的话，也是用该字符集。
     * @param callbackOutStream 回调的OutputStream
     * @throws Exception
     */
    public static void postData(String url, Map<String, String> formMap, String charset, OutputStream callbackOutStream) throws IOException {
        LOGGER.info("post will send :" + url + "\r\n" + formMap.toString());
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(defaultRequestConfig);
        HttpEntity entity = buildFormEntity(formMap, charset);

        if (entity != null) {
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(entity);
        }
        // 创建httpClient对象
        CloseableHttpResponse response = null;
        try {
            showRequest(httpPost, charset);
            // 执行请求
            response = getWithAddHttpClientBuilder(null).build().execute(httpPost);
            int retCode = response.getStatusLine().getStatusCode();
            if (retCode == HttpStatus.SC_OK) {
                response.getEntity().writeTo(callbackOutStream);
            } else {
                showResponse(response, UTF8);
                throw new IOException("invalid response status code with:" + retCode);
            }
        } finally {
            CloseUtils.close(response);
        }
    }


    /**
     * 使用post+form方式提交数据，并获取响应。使用指定证书策略。
     * 
     * @param url
     * @param paraMap
     * @param charset 提交指定的charSet，响应时优先从响应报文读取字符集，没有的话，也是用该字符集。
     * @param contextWrapper 指定的证书，为null时使用默认证书。
     * @param headers 添加的header
     * @return
     * @throws Exception
     */
    public static String postData(String url, Map<String, String> paraMap, String charset,
                                  SSLContextWrapper contextWrapper, Map<String, String> headers) throws IOException {
        return doPost(url, paraMap, charset, contextWrapper, headers);
    }

    /**
     * 以json方式提交文本数据
     * 
     * @param url
     * @param content
     * @param charset
     * @return
     * @throws Exception
     */
    public static String postJsonData(String url, String content, String charset) throws IOException {
        return postData(url, content, null, charset, null, null);
    }

    /**
     * 以指定的MimeType提交数据
     * 
     * @param url 目标url
     * @param content 数据内容
     * @param mimeType 指定的mimeType
     * @param charset 指定的字符集
     * @param contextWrapper 指定的证书
     * @param headers 指定的hearders
     * @return
     * @throws Exception
     */
    public static String postData(String url, String content, String charset, String mimeType,
                                  SSLContextWrapper contextWrapper, Map<String, String> headers) throws IOException {
        return doPost(url, content, charset, mimeType, contextWrapper, headers);
    }

    private static String doPost(String url, String content, String charset, String mimeType,
                                 SSLContextWrapper contextWrapper, Map<String, String> headers) throws IOException {
        LOGGER.info("post will send :" + url + "\r\n" + content);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(defaultRequestConfig);
        HttpEntity entity = buildStringEntity(content, mimeType, charset);
        if (entity != null) {
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(entity);
        }
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return httpExecute(httpPost, charset, contextWrapper);
    }

    private static String doPost(String url, Map<String, String> formMap, String charset,
                                 SSLContextWrapper contextWrapper, Map<String, String> headers) throws IOException {
        LOGGER.info("post will send :" + url + "\r\n" + formMap.toString());
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(defaultRequestConfig);
        HttpEntity entity = buildFormEntity(formMap, charset);

        if (entity != null) {
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(entity);
        }
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return httpExecute(httpPost, charset, contextWrapper);
    }

    private static String httpExecute(HttpUriRequest request, String charset, SSLContextWrapper contextWrapper)
        throws IOException {
        // 创建httpClient对象
        CloseableHttpResponse response = null;
        try {
            showRequest(request, charset);
            // 执行请求
            response = getWithAddHttpClientBuilder(contextWrapper).build().execute(request);
            return getContentFromResponse(response, charset);
        } finally {
            CloseUtils.close(response);
        }
    }

    /**
     * 获取httpclient构造类，没有就添加一个新的。
     * 
     * @param contextWrapper
     * @return
     */
    private static HttpClientBuilder getWithAddHttpClientBuilder(SSLContextWrapper contextWrapper) {
        if (contextWrapper != null) {
            String key = contextWrapper.getClass().getName();
            if (httpClientBuilderMap.containsKey(key)) {
                return httpClientBuilderMap.get(key);
            } else {
                HttpClientBuilder httpClientBuilder = null;
                // 获取注册建造者
                RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
                // 注册http和https请求
                Registry<ConnectionSocketFactory> socketFactoryRegistry
                    = registryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE)
                        .register("https", new SSLConnectionSocketFactory(contextWrapper.getSSLContext())).build();

                PoolingHttpClientConnectionManager poolConnManager
                    = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
                poolConnManager.setMaxTotal(MAX_TOTAL);
                poolConnManager.setDefaultMaxPerRoute(DEFAULT_MAX_PERROUTE);
                httpClientBuilder = HttpClientBuilder.create();
                httpClientBuilder.setConnectionManager(poolConnManager);
                httpClientBuilderMap.put(key, httpClientBuilder);
                return httpClientBuilder;
            }
        } else {
            // 默认返回全部信任的连接池
            return httpClientBuilderMap.get(TrustAnySSLContextWrapper.class.getName());
        }

    }

    private static void showResponse(HttpResponse response, String charset) throws IOException {
        StringBuilder log = new StringBuilder("Content of Response:");

        if (response != null) {
            log.append("\r\n[StatusCode]:" + response.getStatusLine().getStatusCode());
            log.append("\r\n[ResponseHeaders]:");
            for (Header header : response.getAllHeaders()) {
                log.append("\r\n\t" + header.getName() + " = " + header.getValue());
            }
            log.append("\r\n[ResponseContent]:");
            log.append("\r\n" + EntityUtils.toString(response.getEntity(), charset));
        }
        LOGGER.info(log.toString());
    }

    private static void showRequest(HttpUriRequest request, String charset) {
        StringBuilder log = new StringBuilder("info of request:");
        if (request != null) {
            log.append("\r\n[URI]:" + request.getURI());
            log.append("\r\n[METHOD]:" + request.getMethod());
            if (request.getAllHeaders() != null && request.getAllHeaders().length > 0) {
                log.append("\r\n[HEADERS]:");
                for (Header header : request.getAllHeaders()) {
                    log.append("\r\n\t" + header.getName() + " = " + header.getValue());
                }
            }

            if (request instanceof HttpPost) {
                HttpPost post = (HttpPost)request;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    post.getEntity().writeTo(outputStream);
                    String postBody = outputStream.toString(charset);
                    log.append("\r\n[POSTBODY]:" + postBody);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }
        LOGGER.info(log.toString());
    }

    private static String getContentFromResponse(CloseableHttpResponse response, String charset) throws IOException {
        String theCharSet = StringUtils.isBlank(charset) ? UTF8 : charset;
        // 判断返回状态码是否为200
        int retCode = response.getStatusLine().getStatusCode();
        if (retCode == HttpStatus.SC_OK) {
            // EntityUtils会自动根据响应获取编码，如果无法获取，则按utf-8处理。
            String respContent = EntityUtils.toString(response.getEntity(), theCharSet);
            LOGGER.info("Response:\r\n" + respContent);
            return respContent;
        } else {
            showResponse(response, theCharSet);
            throw new IOException("invalid response status code with:" + retCode);
        }
    }

    private static UrlEncodedFormEntity buildFormEntity(Map<String, String> paramMap, String charset) {
        if (paramMap != null) {
            // 构造form请求实体
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            // 构建一个form表单式的实体
            return new UrlEncodedFormEntity(parameters, Charset.forName(charset));
        } else {
            LOGGER.warn("obj is null");
        }
        return null;

    }

    private static StringEntity buildStringEntity(String content, String mimeType, String charset) {
        if (content != null) {
            // 构造json请求实体
            ContentType contentType = null;
            // 默认是json
            String theMimeType = StringUtils.isBlank(mimeType) ? APPLICATION_JSON : mimeType;
            String theCharSet = StringUtils.isBlank(charset) ? UTF8 : charset;
            contentType = ContentType.create(theMimeType, theCharSet);
            return new StringEntity(content, contentType);
        } else {
            LOGGER.warn("obj is null");
        }
        return null;

    }

    public static void download2File(String geturl, File goalfile, SSLContextWrapper contextWrapper) throws IOException {
        HttpGet httpGet = new HttpGet(geturl);
        // 设置请求参数
        httpGet.setConfig(defaultRequestConfig);
        CloseableHttpResponse response = null;
        try {
            showRequest(httpGet, UTF8);
            // 执行请求
            response = getWithAddHttpClientBuilder(contextWrapper).build().execute(httpGet);
            int retCode = response.getStatusLine().getStatusCode();
            if (retCode == HttpStatus.SC_OK) {
                FileOutputStream out = new FileOutputStream(goalfile);
                response.getEntity().writeTo(out);
                CloseUtils.close(out);
            } else {
                showResponse(response, UTF8);
                throw new IOException("invalid response status code with:" + retCode);
            }
        } finally {
            CloseUtils.close(response);
        }
    }

}
