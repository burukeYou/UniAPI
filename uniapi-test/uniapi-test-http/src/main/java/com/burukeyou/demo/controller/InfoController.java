package com.burukeyou.demo.controller;

import com.burukeyou.demo.entity.BaseRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequestMapping("/info-web")
@Controller
public class InfoController {

    @GetMapping("/get01")
    @ResponseBody
    public BaseRsp<String> get01(@RequestParam("name") String name){
        log.info("收到请求1- name:{}",name);
        return BaseRsp.ok(name + "-1");
    }
}
