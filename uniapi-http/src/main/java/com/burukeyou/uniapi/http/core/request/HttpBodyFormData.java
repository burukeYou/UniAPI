package com.burukeyou.uniapi.http.core.request;

import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author caizhihao
 */
@Setter
@Getter
public class HttpBodyFormData extends HttpBody {

    private Map<String,String> formData;

    public HttpBodyFormData(Map<String,String> formData) {
        super(MediaTypeEnum.APPLICATION_FORM_URLENCODED.getType());
        this.formData = formData;
    }

    @Override
    public boolean emptyContent() {
        return formData == null || formData.isEmpty();
    }

    @Override
    public String toStringBody() {
        if (emptyContent()){
            return "";
        }
        return formData.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
    }

}
