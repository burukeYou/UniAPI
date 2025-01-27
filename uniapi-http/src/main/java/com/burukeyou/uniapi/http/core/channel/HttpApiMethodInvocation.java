package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.support.arg.Param;
import com.burukeyou.uniapi.support.map.IMap;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author  caihzihao
 */
public interface HttpApiMethodInvocation<T extends Annotation> extends MethodInvocation {

    /**
     * Obtain HttpAPI annotations for the proxy
     */
    T getProxyApiAnnotation();

    /**
     * Proxy interface
     */
    HttpInterface getProxyInterface();

    /**
     * The specific class being represented
     */
    Class<?> getProxyClass();

    /**
     * get method invocation attachment map
     * @return          all attachment param
     */
    IMap<String,Object> getAttachment();

    /**
     * Get the list of method parameters
     */
    List<Param> getMethodParamList();

    /**
     * Get the type of HTTP response body string that needs to be deserialized Type,
     * This is usually the class type of the return value of the method, or the generic of HttpResponse, or the generic of Future
     *
     */
    Type getBodyResultType();

    /**
     * Get the absolute name of the method, including the class name
     */
    String getMethodAbsoluteName();


}
