package com.burukeyou.uniapi.http.support;

import lombok.Data;

import java.io.Serializable;

/**
 *  Cookie
 */
@Data
public class Cookie implements Serializable {

    private static final long serialVersionUID = -2897119826174864574L;

    /**
     *  Cookie pair Name
     */
    private String name;

    /**
     *  Cookie pair value
     */
    private String value;

    public Cookie() {
    }

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
