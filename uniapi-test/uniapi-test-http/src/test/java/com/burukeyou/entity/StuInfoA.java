package com.burukeyou.entity;

import com.burukeyou.uniapi.http.annotation.JsonPathMapping;
import lombok.*;

@Getter
@Setter
public class StuInfoA {

    @JsonPathMapping("$.nums")
    private int[] nums1;

    @JsonPathMapping("$.son.detail.count")
    private Integer count;

}
