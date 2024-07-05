package com.burukeyou.uniapi.core.proxy;

import com.burukeyou.uniapi.support.ProxySupport;

/**
 * @author caizhihao
 */
public interface ApiProxyFactory {

    /**
     * determine if it is necessary to use a uniAPI proxy for this class
     * @param proxySupport      proxy param
     */
    boolean isProxy(ProxySupport proxySupport);

    /**
     * Obtain specific UniAPI proxy objects
     * @param proxySupport          proxy param
     */
    AnnotationInvokeProxy getProxy(ProxySupport proxySupport);
}
