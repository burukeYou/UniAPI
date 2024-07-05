package com.burukeyou.demo.entity;

import com.burukeyou.uniapi.http.annotation.param.BodyJsonPar;
import com.burukeyou.uniapi.http.annotation.param.CookiePar;
import com.burukeyou.uniapi.http.annotation.param.HeaderPar;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import lombok.Data;

import java.io.Serializable;

@Data
public class Add6DTO implements Serializable {

    private static final long serialVersionUID = 620995896770039484L;

    @QueryPar
    private Long id;

    @HeaderPar
    private String name;

    @BodyJsonPar
    private Add4DTO req;

    @CookiePar
    private String cook;
}
