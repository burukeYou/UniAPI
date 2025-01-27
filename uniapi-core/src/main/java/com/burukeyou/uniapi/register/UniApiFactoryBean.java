package com.burukeyou.uniapi.register;


import com.burukeyou.uniapi.core.proxy.AnnotationInvokeProxy;
import com.burukeyou.uniapi.core.proxy.ApiProxyFactory;
import com.burukeyou.uniapi.support.ProxySupport;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.AbstractLazyCreationTargetSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * @author caizhihao
 */
public class UniApiFactoryBean extends BaseSpringAware implements FactoryBean<Object> {

    private Object lazyProxy;

    private Class<?> targetClass;

    @Autowired
    private List<ApiProxyFactory> proxyFactoryList;


    public UniApiFactoryBean() {
    }

    public UniApiFactoryBean(Class<?> targetClass) {
        this.targetClass = targetClass;
    }


    @Override
    public Object getObject() throws Exception {
        if (lazyProxy == null) {
            createLazyProxy();
        }
        return lazyProxy;
    }

    private void createLazyProxy() {
        ProxySupport proxySupport = new ProxySupport();
        proxySupport.setTargetClass(targetClass);
        proxySupport.setBeanFactory(beanFactory);
        proxySupport.setEnvironment(getEnvironment());

        Optional<ApiProxyFactory> factory = proxyFactoryList.stream().filter(e -> e.isProxy(proxySupport)).findFirst();
        if (!factory.isPresent()){
            this.lazyProxy = new Object();
            return;
        }

        ApiProxyFactory apiProxyFactory = factory.get();
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource(new ProxyLazyTargetSource());
        proxyFactory.addInterface(targetClass);
        proxyFactory.addAdvice(new DataApiMethodInterceptor(apiProxyFactory.getProxy(proxySupport)));
        this.lazyProxy = proxyFactory.getProxy(this.beanClassLoader);
    }

    class DataApiMethodInterceptor implements MethodInterceptor {

        private AnnotationInvokeProxy invokeProxy;

        public DataApiMethodInterceptor(AnnotationInvokeProxy proxy) {
            this.invokeProxy = proxy;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Object proxy = invocation.getThis();
            Method method = invocation.getMethod();
            Object[] args = invocation.getArguments();
            if (ReflectionUtils.isEqualsMethod(method)) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0]);
            } else if (ReflectionUtils.isHashCodeMethod(method)) {
                // Use hashCode of reference proxy.
                return System.identityHashCode(proxy);
            } else if (ReflectionUtils.isToStringMethod(method)) {
                return targetClass.toString();
            }

            if (invokeProxy == null){
                return null;
            }

            return invokeProxy.invoke(targetClass,invocation);
        }
    }

    private class ProxyLazyTargetSource extends AbstractLazyCreationTargetSource {

        @Override
        protected Object createObject() throws Exception {
            return getCallProxy();
        }

        @Override
        public synchronized Class<?> getTargetClass() {
            return targetClass;
        }
    }

    private Object getCallProxy() throws Exception {
        return new Object();
    }

    @Override
    public Class<?> getObjectType() {
        return targetClass;
    }
}
