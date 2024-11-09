package com.burukeyou.uniapi.http.core.request;

import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * @author caizhihao
 */
@Getter
@Setter
public class HttpBodyMultipart extends HttpBody {

    private List<MultipartDataItem> multiPartData;

    public HttpBodyMultipart(List<MultipartDataItem> multiPartData) {
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
        for (MultipartDataItem tmp : multiPartData) {
            File file = tmp.getFileValue();
            sb.append("\t\t").append(tmp.getKey()).append(":   ").append(tmp.isFileFlag() && file != null ? file.getAbsolutePath() : tmp.getTextValue()).append("\n");
        }

        return sb.toString();
    }

}
