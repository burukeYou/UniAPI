package com.burukeyou.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.burukeyou.uniapi.http.annotation.ModelBinding;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResultC<R,T> {

    @JSONField(name = "nums")
    private int[] nums333;

    @ModelBinding
    private R data;

    @ModelBinding
    private T info;


}
