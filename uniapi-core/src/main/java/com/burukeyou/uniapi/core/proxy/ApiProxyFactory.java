package com.burukeyou.uniapi.core.proxy;

import com.burukeyou.uniapi.support.ProxySupport;

/**
 * @author caizhihao
 */
public interface ApiProxyFactory {

    boolean isProxy(ProxySupport proxySupport);

    AnnotationInvokeProxy getProxy(ProxySupport proxySupport);
}
