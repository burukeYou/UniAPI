package com.burukeyou.uniapi.http.support;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpRequestConfig implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * Whether to enable follow redirects
     */
    private Boolean followRedirect;

    /**
     * Whether to enable  follow redirects from HTTPS to HTTP and from HTTP to HTTPS
     */
    private Boolean followSslRedirect;

}
