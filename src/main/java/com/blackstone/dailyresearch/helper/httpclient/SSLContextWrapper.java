package com.blackstone.dailyresearch.helper.httpclient;

import javax.net.ssl.SSLContext;

/**
 * desc:信任证书接口，私有证书需要实现该接口
 *
 * @author 王彦锋
 * @date 2018/4/19 21:35
 */
public interface SSLContextWrapper {

    /**
     * 获取对应的SSLContext
     * 
     * @return
     * @author 王彦锋
     * @date 2018/4/19 21:36
     *
     */
    SSLContext getSSLContext();
}
