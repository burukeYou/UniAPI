package com.burukeyou.uniapi.http.core.param;

import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.support.arg.Param;

/**
 * @author  caizhihao
 */
public interface HttpRequestBodyConverter {

    /**
     * convert param to HttpBody
     */
    HttpBody convert(Param param);

    /**
     * set next Converter
     */
    void setNext(HttpRequestBodyConverter nextConverter);


}
