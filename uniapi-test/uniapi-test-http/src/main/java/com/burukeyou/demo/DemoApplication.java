package com.burukeyou.demo;

import com.burukeyou.uniapi.annotation.UniAPIScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *  httApi的扫描路径
 */
@UniAPIScan("com.burukeyou.demo.api")
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class,args);
    }
}
