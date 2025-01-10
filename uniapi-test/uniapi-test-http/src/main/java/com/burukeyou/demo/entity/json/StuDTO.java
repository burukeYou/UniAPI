package com.burukeyou.demo.entity.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson2.annotation.JSONField;
import com.burukeyou.uniapi.http.annotation.JsonPathMapping;
import com.burukeyou.uniapi.http.annotation.ModelBinding;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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


    public static void main(String[] args) {
        PropertyFilter filter = new PropertyFilter() {

            @Override
            public boolean apply(Object object, String name, Object value) {
                System.out.println(name);
                return true;
            }
        };
        SerializeConfig config = new SerializeConfig();
        config.addFilter(StuDTO.class, filter);

        StuDTO stuDTO = new StuDTO();
        stuDTO.setName("1");
        stuDTO.setSonId(3);
        stuDTO.setCount("a");
        stuDTO.setArr1(new int[]{1,2,34});
        stuDTO.setDetail(new Detail("aa",99));

        String jsonString = JSON.toJSONString(stuDTO, config);
        System.out.println();
    }

}
