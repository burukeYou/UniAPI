package com.burukeyou.uniapi.http.support;

import com.burukeyou.uniapi.http.core.ssl.SslConfig;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
public class HttpApiConfigContext implements Serializable {

    private static final long serialVersionUID = -1L;

    private HttpRequestConfig httpRequestConfig;

    private HttpCallConfig httpCallConfig;

    private SslConfig sslConfig;

    private HttpResponseConfig httpResponseConfig;

    public boolean isNotClientConfig(){
        Object[] arr = {httpCallConfig,sslConfig,httpRequestConfig};
        return  Arrays.stream(arr).allMatch(Objects::isNull);
    }

    public Boolean isAsyncRequest(){
        return Boolean.TRUE.equals(Optional.ofNullable(httpRequestConfig).map(HttpRequestConfig::getAsync).orElse(null));
    }

    public List<String> getJsonPathUnPackList(){
        return Optional.ofNullable(httpResponseConfig).map(HttpResponseConfig::getJsonPathUnPack).orElse(Collections.emptyList());
    }

    public List<String> getAfterJsonPathUnPackList(){
        return Optional.ofNullable(httpResponseConfig).map(HttpResponseConfig::getAfterJsonPathUnPack).orElse(Collections.emptyList());
    }
}
