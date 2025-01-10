package com.burukeyou.demo.entity;

import lombok.Data;

@Data
public class U2DTO {

    private Integer id;

    private String name;

    public U2DTO() {
    }

    public U2DTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
