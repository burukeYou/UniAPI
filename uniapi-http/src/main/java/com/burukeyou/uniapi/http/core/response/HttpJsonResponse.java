package com.burukeyou.uniapi.http.core.response;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * Deserialize the body result based on the return value type of the proxy's method
 *
 * @author caizhihao
 */
@Getter
@Setter
public class HttpJsonResponse<T> extends AbstractHttpResponse<T> {

    /**
     *  Http Response body text value
     */
    private  String jsonRsp;

    /**
     *  Proxy Method
     */
    private  Method method;

    public HttpJsonResponse(String jsonRsp, Method method) {
        this.jsonRsp = jsonRsp;
        this.method = method;
        updateBodyResult();
    }

    @Override
    public String bodyResultString() {
        return jsonRsp;
    }

    public void setJsonRsp(String jsonRsp) {
        this.jsonRsp = jsonRsp;
        updateBodyResult();
    }

    private void updateBodyResult(){
        this.bodyResult = JSON.parseObject(this.jsonRsp,method.getGenericReturnType());
    }
}
