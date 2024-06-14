package com.burukeyou.demo.entity;

import lombok.Data;

@Data
public class CategoryBody {

    private String category;

    private String range;

    public CategoryBody() {
    }

    public CategoryBody(String category, String range) {
        this.category = category;
        this.range = range;
    }
}
