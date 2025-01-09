package com.burukeyou.uniapi.http.support;

import java.io.Serializable;
import java.util.List;

import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpResponseConfig implements Serializable {

    private static final long serialVersionUID = -6769135220896976117L;

    /**
     *  Unpacking of the original response body
     */
    private List<String> jsonPathUnPack;

    /**
     *  Unpacking of the response body string after the {@link HttpApiProcessor#postAfterHttpResponseBodyString}
     */
    private List<String> afterJsonPathUnPack;

    /**
     *  Extract the response body string by the jsonPath
     */
    private String extractJsonPath;
}
