package com.burukeyou.uniapi.support;

import lombok.Data;

import java.io.Serializable;

@Data
public class Cookie implements Serializable {

    private String name;
    private String value;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
