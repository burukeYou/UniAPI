package com.burukeyou.uniapi.core.response;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author caizhihao
 */
@Data
public class HttpJsonResponse extends AbstractHttpResponse {

    private final String jsonRsp;
    private final Method method;

    public HttpJsonResponse(String jsonRsp, Method method) {
        this.jsonRsp = jsonRsp;
        this.method = method;
    }

    @Override
    public Object getReturnObj() {
        if (ifReturnOriginalResponse()){
            return this;
        }

        return JSON.parseObject(jsonRsp,method.getGenericReturnType());
    }


}
