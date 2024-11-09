package com.burukeyou.demo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Add4DTO implements Serializable {

    private static final long serialVersionUID = 620995896770039484L;

    private Long id;
    private String name;

    public Add4DTO() {
    }

    public Add4DTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
