package com.burukeyou.demo.entity;

import com.burukeyou.uniapi.annotation.param.BodyJsonParam;
import com.burukeyou.uniapi.annotation.param.HeaderParam;
import com.burukeyou.uniapi.annotation.param.UrlParam;
import lombok.Data;

@Data
public class QueryUserReq {

    @UrlParam
    private String userId;

    @HeaderParam
    private String token;

    @UrlParam("cityIdSSS")
    private Integer cityId;

    @BodyJsonParam
    private CategoryBody categoryBody;

    private String name1;
}
