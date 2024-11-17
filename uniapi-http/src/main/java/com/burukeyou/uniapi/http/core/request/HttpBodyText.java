package com.burukeyou.uniapi.http.core.request;

import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Setter
@Getter
public class HttpBodyText extends HttpBody {

    private String text;

    protected HttpBodyText(String text) {
        super(MediaTypeEnum.TEXT_PLAIN.getType());
        this.text = text;
    }

    @Override
    public boolean emptyContent() {
        return StringUtils.isBlank(text);
    }

    @Override
    public String toStringBody() {
        return text;
    }
}
