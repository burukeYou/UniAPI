package com.burukeyou.uniapi.http.core.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author caizhihao
 */
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
    public String toStringBody() {
        if (emptyContent()){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (MultipartFormDataItem tmp : multiPartData) {
            File file = tmp.getFile();
            sb.append("\t\t").append(tmp.getKey()).append(":").append(tmp.isFileFlag() && file != null ? file.getAbsolutePath() : tmp.getValue()).append("\n");
        }

        return sb.toString();
    }

}
