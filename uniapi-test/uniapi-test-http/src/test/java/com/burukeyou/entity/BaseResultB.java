package com.burukeyou.entity;

import com.burukeyou.uniapi.http.annotation.JsonPathMapping;
import com.burukeyou.uniapi.http.annotation.ModelBinding;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResultB<R> {

    private String name11;

    @JsonPathMapping("$.info.orderNo")
    private String name2;

    @ModelBinding
    private R data;


}
