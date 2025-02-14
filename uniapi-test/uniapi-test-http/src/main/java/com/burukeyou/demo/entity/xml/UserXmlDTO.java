package com.burukeyou.demo.entity.xml;


import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "user")
public class UserXmlDTO {

    //@XmlElement(name="name")
    private String name;

    //@XmlElement(name="age")
    private Integer age;

    public UserXmlDTO() {
    }

    public UserXmlDTO(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
