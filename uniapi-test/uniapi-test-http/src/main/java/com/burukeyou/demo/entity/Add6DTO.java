package com.burukeyou.demo.entity;

import com.burukeyou.uniapi.annotation.param.BodyJsonParam;
import com.burukeyou.uniapi.annotation.param.HeaderParam;
import com.burukeyou.uniapi.annotation.param.UrlParam;
import lombok.Data;

import java.io.Serializable;

@Data
public class Add6DTO implements Serializable {

    private static final long serialVersionUID = 620995896770039484L;

    @UrlParam
    private Long id;

    @HeaderParam
    private String name;

    @BodyJsonParam
    private Add4DTO req;
}
