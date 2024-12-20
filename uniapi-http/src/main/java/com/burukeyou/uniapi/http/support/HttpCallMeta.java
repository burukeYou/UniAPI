package com.burukeyou.uniapi.http.support;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpCallMeta implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * The call timeout spans the entire call: resolving DNS, connecting, writing the request body,
     * server processing, and reading the response body. If the call requires redirects or retries all must complete within one timeout period.
     * A value of 0 means no timeout, otherwise values must be between 1 and Integer.MAX_VALUE when converted to milliseconds
     *
     */
    private long callTimeout = 0L;

    /**
     * connect timeout is applied when connecting a TCP socket to the target host.
     * A value of 0 means no timeout, otherwise values must be between 1 and Integer.MAX_VALUE when converted to milliseconds.
     */
    private long connectTimeout = 0L;

    /**
     * The write timeout is applied for individual write IO operations
     * A value of 0 means no timeout, otherwise values must be between 1 and Integer.MAX_VALUE when converted to milliseconds
     */
    private long writeTimeout = 0L;

    /**
     * The read timeout is applied to both the TCP socket and for individual read IO operations including on Source of the Response
     * A value of 0 means no timeout, otherwise values must be between 1 and Integer.MAX_VALUE when converted to milliseconds
     */
    private long readTimeout = 0L;
}
