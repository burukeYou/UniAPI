package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.http.core.response.HttpResponse;

/**
 * Convert origin Response to Uni-HttpResponse
 *
 * @author  caizhihao
 */
public interface HttpResponseConverter {

    /**
     * convert origin response
     */
    HttpResponse<?> convert(ResponseConvertContext context);

    /**
     * set next Converter
     */
    void setNext(HttpResponseConverter nextConverter);
}
