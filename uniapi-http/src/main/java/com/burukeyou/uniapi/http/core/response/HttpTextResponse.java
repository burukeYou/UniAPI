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
public class HttpTextResponse extends AbstractHttpResponse<String> {

    /**
     *  Http Response body text value
     */
    private String textValue;

    public HttpTextResponse(String textValue, ResponseConvertContext context) {
        super(context,textValue);
        this.textValue = textValue;
    }

    @Override
    public String bodyResultString() {
        return textValue;
    }
}
