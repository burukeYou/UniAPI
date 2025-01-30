package com.burukeyou.entity;

import com.burukeyou.uniapi.http.annotation.JsonPathMapping;
import com.burukeyou.uniapi.http.annotation.ModelBinding;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BaseResultA<R> {

    @JsonPathMapping("$.configs[0].detail.id")
    private Integer status;

    @JsonPathMapping("$.info.orderNo")
    private String msg;

    @ModelBinding
    private List<R> dataList;

    @ModelBinding
    private R data;

}
