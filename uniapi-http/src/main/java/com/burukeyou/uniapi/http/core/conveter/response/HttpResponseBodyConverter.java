package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.http.core.response.HttpResponse;
import okhttp3.Response;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author  caizhihao
 */
public interface  HttpResponseBodyConverter {

    /**
     * convert origin response
     */
    HttpResponse<?> convert(Response response, MethodInvocation methodInvocation);

    /**
     * set next Converter
     */
    void setNext(HttpResponseBodyConverter nextConverter);
}
