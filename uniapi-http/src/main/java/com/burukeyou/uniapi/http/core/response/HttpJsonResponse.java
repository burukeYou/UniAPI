package com.burukeyou.uniapi.http.core.response;

import com.burukeyou.uniapi.http.core.conveter.response.ResponseConvertContext;
import lombok.Getter;
import lombok.Setter;

/**
 * Deserialize the body result based on the return value type of the proxy's method
 *
 * @author caizhihao
 */
@Getter
@Setter
public class HttpJsonResponse<T> extends AbstractHttpResponse<T> {

    private  String jsonValue;

    public HttpJsonResponse(String value, ResponseConvertContext context) {
        super(context,null);
        this.jsonValue = value;
    }

    @Override
    public String bodyResultString() {
        return jsonValue;
    }
}
