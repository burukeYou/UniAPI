package com.burukeyou.demo.entity;

import com.burukeyou.uniapi.http.annotation.param.BodyJsonPar;
import com.burukeyou.uniapi.http.annotation.param.HeaderPar;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import lombok.Data;

@Data
public class QueryUserReq {

    @QueryPar
    private String userId;

    @HeaderPar
    private String token;

    @QueryPar("cityIdSSS")
    private Integer cityId;

    @BodyJsonPar
    private CategoryBody categoryBody;

    private String name1;
}
