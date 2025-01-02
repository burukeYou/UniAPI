package com.burukeyou.uniapi.http.extension.processor;

import java.io.IOException;
import java.lang.annotation.Annotation;

import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.channel.HttpSender;
import com.burukeyou.uniapi.http.core.exception.HttpResponseException;
import com.burukeyou.uniapi.http.core.exception.SendHttpRequestException;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;

/**
 * HttpAPI lifecycle processor Extension point
 *
 * execution flow:
 *
 * <pre>
 *
 *                postBeforeHttpRequest
 *                        |
 *                        V
 *                 postSendingHttpRequest
 *                        |
 *                        V
 *                 postAfterHttpResponse
 *                        |
 *                        V
 *              postAfterHttpResponseBodyString
 *                        |
 *                        V
 *             postAfterHttpResponseBodyResult
 *                        |
 *                        V
 *             postAfterMethodReturnValue
 *</pre>
 *
 * @author caizhihao
 * @param <T>
 */
public interface HttpApiProcessor<T extends Annotation> {

    /**
     * Before sending the request
     *          you can revise the requested data for UniHttpRequest
     *          if return null will stop to send the request
     * @param uniHttpRequest              request data
     * @param methodInvocation          the method of proxy execution
     * @return                          the new request data,if return null will stop to send the request
     */
    default UniHttpRequest postBeforeHttpRequest(UniHttpRequest uniHttpRequest, HttpApiMethodInvocation<T> methodInvocation){
        return uniHttpRequest;
    }

    /**
     * When sending HTTP requests， using UniHttpRequest to send an HTTP request
     * @param httpSender                   Request Sender
     * @param uniHttpRequest               request data
     */
    default UniHttpResponse postSendingHttpRequest(HttpSender httpSender, UniHttpRequest uniHttpRequest, HttpApiMethodInvocation<T> methodInvocation){
        return httpSender.sendHttpRequest(uniHttpRequest);
    }

    /**
     * This method will be called back when {@link #postSendingHttpRequest} is executed,
     * regardless of whether  {@link #postSendingHttpRequest} is executed successfully or abnormally
     * @param request                   request data
     * @param exception                 exception info, when request is executed successfully, this parameter is null
     * @param response                  Http response, when request is executed abnormally, this parameter is  null
     * @param methodInvocation          the method of proxy execution
     */
    default void postAfterHttpResponse(Throwable exception, UniHttpRequest request,UniHttpResponse response, HttpApiMethodInvocation<T> methodInvocation){
        if (exception instanceof IOException){
            throw new SendHttpRequestException("Http请求网络IO异常",exception);
        }else if (exception != null){
            throw new SendHttpRequestException("Http请求异常", exception);
        }
        if (!response.isSuccessful()) {
            throw new HttpResponseException("Http请求响应异常 接口【"+ response.getRequest().getUrlPath()+"】 响应状态码【" + response.getHttpCode() + "】结果:【" + response.getBodyToString() + "】");
        }
    }

    /**
     * Post-processing of HTTP response body string, when content-type is text
     * @param bodyString          http body string
     * @param rsp                 Original  Http Response
     * @param methodInvocation    The method of agency
     * @return                    the new response body string
     */
    default String postAfterHttpResponseBodyString(String bodyString, UniHttpResponse rsp, HttpApiMethodInvocation<T> methodInvocation){
        return bodyString;
    }


    /**
     * Post-processing of HTTP response body objects
     * @param bodyResult                     response body object。
     *                                       The specific object deserialized by the HTTP response body,
     *                                       with the specific type being the return value type of the proxy method
     * @param rsp                            Original  Http Response
     * @param methodInvocation               The method of agency
     * @return                               the new response body object。
     */
    default Object postAfterHttpResponseBodyResult(Object bodyResult, UniHttpResponse rsp, HttpApiMethodInvocation<T> methodInvocation){
        return bodyResult;
    }

    /**
     * The post-processing method returns a value, similar to the post-processing of AOP
     * @param methodReturnValue             Method return value
     * @param rsp                           Original  Http Response
     * @param methodInvocation              The method of agency
     * @return                              the new Method return value
     */
    default Object postAfterMethodReturnValue(Object methodReturnValue, UniHttpResponse rsp, HttpApiMethodInvocation<T> methodInvocation){
        return methodReturnValue;
    }

}
