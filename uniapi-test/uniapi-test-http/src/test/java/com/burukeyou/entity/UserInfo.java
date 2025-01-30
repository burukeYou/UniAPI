package com.burukeyou.entity;

import com.burukeyou.uniapi.http.annotation.JsonPathMapping;
import lombok.*;

@Getter
@Setter
public class UserInfo {

    @JsonPathMapping("$.son.detail")
    private Detail detail;

    private int[] nums;

    @JsonPathMapping("$.users[0].name")
    private String userName;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {
        private String level;
        private Integer count;
    }
}
