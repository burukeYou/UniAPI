package com.burukeyou.uniapi.http.core.response;

import com.burukeyou.uniapi.http.core.conveter.response.ResponseConvertContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpEmptyBodyResponse extends AbstractHttpResponse<Void>{

    public HttpEmptyBodyResponse(ResponseConvertContext context) {
        super(context,null);
    }

    @Override
    public String bodyResultString() {
        return null;
    }
}
