package com.burukeyou.uniapi.http.core.request;

import com.alibaba.fastjson.JSON;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author caizhihao
 */
@Setter
@Getter
public class HttpBodyFormData extends HttpBody {

    private Map<String,String> formData;

    public HttpBodyFormData(Map<String,String> formData) {
        super(MediaTypeEnum.APPLICATION_FORM_URLENCODED.getChartSetType());
        this.formData = formData;
    }

    @Override
    public boolean emptyContent() {
        return formData == null || formData.isEmpty();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(formData);
    }

}
