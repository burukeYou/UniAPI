package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.http.core.response.HttpResponse;
import okhttp3.Response;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Convert origin Response to Uni-HttpResponse
 *
 * @author  caizhihao
 */
public interface HttpResponseConverter {

    /**
     * convert origin response
     */
    HttpResponse<?> convert(Response response, MethodInvocation methodInvocation);

    /**
     * set next Converter
     */
    void setNext(HttpResponseConverter nextConverter);
}
