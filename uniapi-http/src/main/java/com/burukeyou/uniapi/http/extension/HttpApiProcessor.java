package com.burukeyou.uniapi.http.extension;

import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.channel.HttpSender;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.response.HttpResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * HttpAPI lifecycle processor Extension point
 *
 * execution flow:
 *
 *                  Build HttpMetadata
 *                         |
 *                         V
 *                postBeforeHttpMetadata
 *                        |
 *                        V
 *                 postSendHttpRequest
 *                        |
 *                        V
 *             postAfterHttpResponseResult
 *                        |
 *                        V
 *             postAfterMethodReturnValue
 *
 *
 * @author caizhihao
 * @param <T>
 */
public interface HttpApiProcessor<T extends Annotation> {

    /**
     * Before sending the request
     *          you can revise the requested data for HttpMetadata
     * @param httpMetadata              request data
     * @param methodInvocation          the method of proxy execution
     * @return                          the new request data
     */
    default HttpMetadata postBeforeHttpMetadata(HttpMetadata httpMetadata, HttpApiMethodInvocation<T> methodInvocation){
        return httpMetadata;
    }

    /**
     * When sending HTTP requests
     *          Send an HTTP request using HttpMetadata
     * @param httpSender                 Request Sender
     * @param httpMetadata               request data
     */
    default HttpResponse<?> postSendHttpRequest(HttpSender httpSender, HttpMetadata httpMetadata){
        return httpSender.sendHttpRequest(httpMetadata);
    }

    /**
     * Post-processing of HTTP response body objects
     * @param result                         response body object。
     *                                       The specific object deserialized by the HTTP response body,
     *                                       with the specific type being the return value type of the proxy method
     * @param rsp                            Original  Http Response
     * @param method                         The method of agency
     * @param httpMetadata                   request data
     * @return                               the new response body object。
     */
    default Object postAfterHttpResponseResult(Object result, HttpResponse<?> rsp, Method method, HttpMetadata httpMetadata){
        return result;
    }

    /**
     * The post-processing method returns a value, similar to the post-processing of AOP
     * @param methodReturnValue             Method return value
     * @param rsp                           Original  Http Response
     * @param method                        The method of agency
     * @param httpMetadata                  request data
     * @return                              the new Method return value
     */
    default Object postAfterMethodReturnValue(Object methodReturnValue,HttpResponse<?> rsp,Method method,HttpMetadata httpMetadata){
        return methodReturnValue;
    }

}
