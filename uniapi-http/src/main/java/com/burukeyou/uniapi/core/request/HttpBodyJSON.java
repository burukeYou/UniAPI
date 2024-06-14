package com.burukeyou.uniapi.core.request;

import com.burukeyou.uniapi.support.MediaTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author caizhihao
 */
@Setter
@Getter
public class HttpBodyJSON extends HttpBody {

    private String bodyJson;

    public HttpBodyJSON(String bodyJson) {
        super(MediaTypeEnum.APPLICATION_JSON.getChartSetType());
        this.bodyJson = bodyJson;
    }

    @Override
    public boolean emptyContent() {
        return StringUtils.isBlank(bodyJson);
    }

    @Override
    public String toString() {
        return bodyJson;
    }

}
