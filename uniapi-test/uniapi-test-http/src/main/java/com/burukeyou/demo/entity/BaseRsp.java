package com.burukeyou.demo.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class BaseRsp<T> implements Serializable {

    private Integer code;

    private T data;

    private String ext;

    public BaseRsp() {
    }

    public BaseRsp(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public static <T> BaseRsp<T> ok(T data){
        return new BaseRsp<>(200,data);
    }
}
