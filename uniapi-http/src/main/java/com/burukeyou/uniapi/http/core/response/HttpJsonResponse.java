package com.burukeyou.uniapi.http.core.response;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author caizhihao
 */
@Data
public class HttpJsonResponse<T> extends AbstractHttpResponse<T> {

    private final String jsonRsp;
    private final Method method;

    public HttpJsonResponse(String jsonRsp, Method method) {
        this.jsonRsp = jsonRsp;
        this.method = method;
        this.result = JSON.parseObject(jsonRsp,method.getGenericReturnType());
    }

    @Override
    public String bodyResultString() {
        return jsonRsp;
    }
}
