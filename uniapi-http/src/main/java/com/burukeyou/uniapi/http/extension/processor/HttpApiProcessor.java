package com.burukeyou.uniapi.http.extension.processor;

import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.channel.HttpSender;
import com.burukeyou.uniapi.http.core.exception.HttpResponseException;
import com.burukeyou.uniapi.http.core.exception.SendHttpRequestException;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

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
 *               postBeforeSendHttpRequest
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
 *             postAfterHttpResponseBodyStringDeserialize
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
     * Before sending the request, get new request data
     * <pre>
     *     you can revise the request data for UniHttpRequest
     *     if return null will stop to send the request
     * </pre>
     * @param uniHttpRequest              request data
     * @param methodInvocation          the method of proxy execution
     * @return                          the new request data,if return null will stop to send the request
     */
    default UniHttpRequest postBeforeHttpRequest(UniHttpRequest uniHttpRequest, HttpApiMethodInvocation<T> methodInvocation){
        return uniHttpRequest;
    }

    /**
     * This method is called back before the Http request is actually sent
     * @param uniHttpRequest                 request data
     * @param httpSender                     Request Sender
     * @param methodInvocation               the method of proxy execution
     */
    default void postBeforeSendHttpRequest(UniHttpRequest uniHttpRequest, HttpSender httpSender,HttpApiMethodInvocation<T> methodInvocation){
    }

    /**
     * When sending SYNC HTTP requests will call back， using UniHttpRequest to send an HTTP request
     * @param httpSender                   Request Sender
     * @param uniHttpRequest               request data
     */
    default UniHttpResponse postSendingHttpRequest(HttpSender httpSender, UniHttpRequest uniHttpRequest, HttpApiMethodInvocation<T> methodInvocation){
        return httpSender.sendHttpRequest(uniHttpRequest);
    }

    /**
     * This method will be called back when http request has been sent ,regardless of whether it executed successfully or abnormally
     * @param request                   request data
     * @param exception                 exception info, when request is executed successfully, this parameter is null
     * @param response                  Http response, when request is executed abnormally, this parameter is  null
     * @param methodInvocation          the method of proxy execution
     */
    default void postAfterHttpResponse(Throwable exception, UniHttpRequest request,UniHttpResponse response, HttpApiMethodInvocation<T> methodInvocation) throws Throwable{
        String baseLog = String.format(" 接口:%s 耗时:%s(ms) ", request.getUrlPath(), request.getCurrentCostTime());
        if (exception instanceof IOException){
            throw new SendHttpRequestException("Http请求网络IO异常" + baseLog,exception);
        }else if (exception != null){
            throw new SendHttpRequestException("Http请求异常" + baseLog, exception);
        }
        if (!response.isSuccessful()) {
            throw new HttpResponseException("Http请求响应异常"+ baseLog+" 响应状态码【" + response.getHttpCode() + "】结果:【" + response.getBodyToString() + "】");
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
     * deserialize http response body string to object，If you don't implement this method, the framework will automatically deserialize internally
     * @param bodyString             http response body string , Consistent with {@link #postAfterHttpResponseBodyString}
     * @param bodyResultType         http response body string deserialized type, usually a method return value type, or a generic for HttpResponse
     * @param rsp                    Original  Http Response
     * @param methodInvocation       The method of agency
     * @return                       bodyResult object, Consistent with {@link #postAfterHttpResponseBodyResult}
     */
    default Object postAfterHttpResponseBodyStringDeserialize(String bodyString,Type bodyResultType,UniHttpResponse rsp, HttpApiMethodInvocation<T> methodInvocation){
        throw new UnsupportedOperationException();
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
