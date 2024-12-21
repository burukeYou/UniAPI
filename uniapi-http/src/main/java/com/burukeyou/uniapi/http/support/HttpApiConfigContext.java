package com.burukeyou.uniapi.http.support;

import com.burukeyou.uniapi.http.core.ssl.SslConfig;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
public class HttpApiConfigContext implements Serializable {

    private static final long serialVersionUID = -1L;

    private HttpCallConfig httpCallMeta;

    private SslConfig sslConfig;

    public boolean isNotClientConfig(){
        Object[] arr = {httpCallMeta,sslConfig};
        return  Arrays.stream(arr).allMatch(Objects::isNull);
    }
}
