package com.burukeyou.uniapi.http.core.request;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

/**
 * @author caizhihao
 */
@Getter
@Setter
public class HttpBodyMultipart extends HttpBody {

    private List<MultipartDataItem> multiPartData = new ArrayList<>();


    public HttpBodyMultipart() {
        super(MediaTypeEnum.MULTIPART_FORM_DATA.getType());
    }

    public HttpBodyMultipart(List<MultipartDataItem> multiPartData) {
        this();
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
            sb.append("\t\t").append(tmp.isFileFlag() ? "(File)" : "      ").append(tmp.getKey()).append(":   ").append(tmp.getValueString()).append("\n");
        }
        return sb.toString();
    }

    public void addTextItem(String name, String value){
        multiPartData.add(new MultipartDataItem(name,value,false));
    }

    public void addFileItem(String name, byte[] file){
        multiPartData.add(new MultipartDataItem(name,file,true));
    }

    public void addFileItem(String name, InputStream file){
        multiPartData.add(new MultipartDataItem(name,file,true));
    }

    public void addFileItem(String name, File file){
        multiPartData.add(new MultipartDataItem(name,file,true));
    }

}
