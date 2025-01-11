package com.burukeyou.demo.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
public class Add9DTO implements Serializable {

    private static final long serialVersionUID = 620995896770039484L;

    private Long id;

    @JSONField(name = "user_name")
    private String name;

    private File userImg;

    //private List<File> logoImg;

    private File[] logoImg;

    public Add9DTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
