package com.burukeyou.demo.entity.xml;


import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "user")
public class UserXmlReq {

    //@XmlElement(name="name")
    private String name;

    //@XmlElement(name="age")
    private Integer age;

    public UserXmlReq() {
    }

    public UserXmlReq(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
