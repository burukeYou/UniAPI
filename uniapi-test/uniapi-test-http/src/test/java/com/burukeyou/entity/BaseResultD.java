package com.burukeyou.entity;

import com.burukeyou.uniapi.http.annotation.JsonPathMapping;
import com.burukeyou.uniapi.http.annotation.ModelBinding;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResultD<R> {

    @JsonPathMapping("$.configs[0].detail.id")
    private Integer status;

    @JsonPathMapping("$.info.orderNo")
    private String msg;

    @ModelBinding
    private R data;

}
