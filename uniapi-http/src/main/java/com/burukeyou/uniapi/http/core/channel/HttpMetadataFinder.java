package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *   find HttpMetadata form the proxy method
 *   Multiple different attributes in multiple parameters or even one parameter of the method
 *   may be mixed with multiple Http request parameters, such as request header, request body,
 *   url path parameter, form-data paramete
 *
 * @author caizhihao
 */
public interface HttpMetadataFinder {

    /**
     * find HttpMetadata
     * @param method          proxy method
     * @param args            proxy method args
     */
    UniHttpRequest find(Method method, Object[] args);

    /**
     * find Url Query Param
     * @param method          proxy method
     * @param args            proxy method args
     */
    Map<String,Object> findQueryParam(Method method, Object[] args);

    /**
     *  find Url Path Param
     * @param method          proxy method
     * @param args            proxy method args
     */
    Map<String,String> findPathParam(Method method, Object[] args);

    /**
     * find Http request Header Param
     * @param method          proxy method
     * @param args            proxy method args
     */
    Map<String, String> findHeaders(Method method, Object[] args);

    /**
     * find Http request body Param
     * @param method          proxy method
     * @param args            proxy method args
     */
    HttpBody findHttpBody(Method method, Object[] args);

}
