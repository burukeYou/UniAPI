package com.burukeyou.demo.entity.json;


import com.alibaba.fastjson2.annotation.JSONField;
import com.burukeyou.uniapi.http.annotation.JsonPathMapping;
import com.burukeyou.uniapi.http.annotation.ModelBinding;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ModelBinding
public class StuDTO {

    @JsonPathMapping("$.users[1].name")
    @JSONField(name = "name222")
    @JsonProperty
    private String name;

    @JsonPathMapping("$.son.detail.count")
    private Integer sonId;

    @JsonPathMapping("$.son.detail.count")
    private String count;

    //private String id;

    @JsonPathMapping("$.nums")
    private int[] arr1;

    @JsonPathMapping("$.son.detail")
    private Detail detail;

    @ModelBinding
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
    public static class BBQ {
        @JsonPathMapping(path = "$.son.detail.level")
        private String level22;
        private Integer count;
    }
}
