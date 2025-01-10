package com.burukeyou.demo.entity.json;

import com.alibaba.fastjson2.annotation.JSONField;
import com.burukeyou.uniapi.http.annotation.ModelBinding;
import com.burukeyou.uniapi.http.annotation.JsonPathMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ModelBinding
public class StuReq {

    @JsonPathMapping("$.users.name")
    private String name;

    @JsonPathMapping("$.son.detail.id")
    private Integer sonId;

    @JsonPathMapping("$.son.detail.count")
    private String count;

    private String id;

    @JsonPathMapping("$.info.nums")
    private int[] arr1;

    @JsonPathMapping("$.father.detail")
    private Detail detail;

    private BBQ bbq;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {
        @JSONField(name = "level")
        private String level22;
        private Integer count;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BBQ {
        @JsonPathMapping(path = "$.son.detail.level")
        private String level22;
        private Integer count;
    }



}
