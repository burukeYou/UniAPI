package com.burukeyou.uniapi.http.core.channel;


import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.exception.BaseDataApiException;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author caizhihao
 */
public abstract class AnnotationConfigCacheInvoker {

    private final Map<Method, HttpInterface> httpInterfaceCache = new ConcurrentHashMap<>();

    protected HttpInterface getHttpInterfaceInfo(Method method) {
        HttpInterface httpInterface = httpInterfaceCache.get(method);
        if (httpInterface == null){
            httpInterface = AnnotatedElementUtils.getMergedAnnotation(method, HttpInterface.class);
            if (httpInterface == null) {
                throw new BaseDataApiException("please mask @HttpInterface in this method " + method.getName() + "and config http path");
            }
            httpInterfaceCache.put(method,httpInterface);
        }
        return httpInterface;
    }

}
