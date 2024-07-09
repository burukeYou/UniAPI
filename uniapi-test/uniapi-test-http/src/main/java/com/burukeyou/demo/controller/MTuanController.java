package com.burukeyou.demo.controller;

import com.burukeyou.demo.entity.BaseRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequestMapping("/mtuan/weather")
@Controller
public class MTuanController {


    @PostMapping("/getToken")
    @ResponseBody
    public String getToken(@RequestHeader("appId") String appId,
                           @RequestHeader("publicKey") String publicKey, HttpServletResponse servletResponse){
        log.info("收到请求1- appId:{} key;{}",appId,publicKey);

        servletResponse.setHeader("sessionId","6666");

        return "Token-99999---" + appId + "---"+ publicKey;
    }


    @GetMapping("/getCityByName")
    @ResponseBody
    public BaseRsp<String> add(@RequestParam("city") String name){
        log.info("收到请求2- city:{}",name);
        return BaseRsp.ok(name + "会下大雨");
    }

}
