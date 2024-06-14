package com.burukeyou.uniapi.core.request;

import com.alibaba.fastjson.JSON;
import com.burukeyou.uniapi.support.MediaTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@Setter
public class HttpBodyMultipartFormData extends HttpBody {

    private List<MultipartFormDataItem> multiPartData;

    public HttpBodyMultipartFormData(List<MultipartFormDataItem> multiPartData) {
        super(MediaTypeEnum.MULTIPART_FORM_DATA.getType());
        this.multiPartData = multiPartData;
    }

    @Override
    public boolean emptyContent() {
        return CollectionUtils.isEmpty(multiPartData);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(multiPartData);
    }

}
