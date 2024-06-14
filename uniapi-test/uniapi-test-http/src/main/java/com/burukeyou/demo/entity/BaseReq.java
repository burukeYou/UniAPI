package com.burukeyou.demo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseReq<T> implements Serializable {

    private String seqNo;

    private String sign;
}
