package com.burukeyou.uniapi.http.core.response;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

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
    private  String textValue;

    public HttpJsonResponse(String textValue, Method method) {
        this.textValue = textValue;
        this.method = method;
        updateBodyResult();
    }

    @Override
    public String bodyResultString() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
        updateBodyResult();
    }

    private void updateBodyResult(){
        Type resultType = getBodyResultType();
        if (resultType.equals(String.class)){
            this.bodyResult = (T)this.textValue;
        }else {
            this.bodyResult = JSON.parseObject(this.textValue,getBodyResultType());
        }
    }
}
