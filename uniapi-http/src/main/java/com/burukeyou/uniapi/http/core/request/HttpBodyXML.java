package com.burukeyou.uniapi.http.core.request;

import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import com.burukeyou.uniapi.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpBodyXML extends HttpBody{

    private String xml;

    public HttpBodyXML(String xml) {
        super(MediaTypeEnum.APPLICATION_XML.getType());
        this.xml = xml;
    }

    @Override
    public boolean emptyContent() {
        return StrUtil.isBlank(xml);
    }

    @Override
    public String toStringBody() {
        return xml;
    }
}
