package com.burukeyou.uniapi.http.support;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import com.burukeyou.uniapi.http.core.ssl.SslConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpApiConfigContext implements Serializable {

    private static final long serialVersionUID = -1L;

    private HttpCallConfig2 httpCallConfig2;

    private SslConfig sslConfig;

    public boolean isNotClientConfig(){
        Object[] arr = {httpCallConfig2,sslConfig};
        return  Arrays.stream(arr).allMatch(Objects::isNull);
    }
}
