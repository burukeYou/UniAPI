package com.burukeyou.uniapi.http.core.channel;

import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *   Multiple different attributes in multiple parameters or even one parameter of the method
 *   may be mixed with multiple Http request parameters, such as request header, request body,
 *   url path parameter, form-data paramete
 *
 * @author caizhihao
 */
public interface HttpMetadataFinder {

    HttpMetadata find(Method method, Object[] args);

    Map<String,Object> findQueryParam(Method method, Object[] args);

    Map<String,String> findPathParam(Method method, Object[] args);

    Map<String, String> findHeaders(Method method, Object[] args);

    HttpBody findHttpBody(Method method, Object[] args);

}
