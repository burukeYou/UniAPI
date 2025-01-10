package com.burukeyou.demo.entity;

import java.io.Serializable;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class Add4DTO implements Serializable {

    private static final long serialVersionUID = 620995896770039484L;

    private Long id;
    private String name;


    @Value("${bbq.user}")
    private Add4DTO dto;

    public Add4DTO() {
    }

    public Add4DTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
