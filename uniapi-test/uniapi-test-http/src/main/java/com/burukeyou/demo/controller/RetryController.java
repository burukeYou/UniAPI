package com.burukeyou.demo.controller;

import com.burukeyou.demo.entity.BaseRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequestMapping("/retry-web")
@Controller
public class RetryController {

    private static Map<String,Integer> countMap = new ConcurrentHashMap<>();

    public synchronized static BaseRsp<String> getResult(String key){
        Integer count = countMap.get(key);
        if (count == null){
            count = 0;
        }
        countMap.put(key,count+1);
        if (count+1 < 5){
            return null;
        }
        return  BaseRsp.ok(key + "-9999");
    }


    @GetMapping("/rt01")
    @ResponseBody
    public BaseRsp<String> rt01(@RequestParam("key") String key){
        throw new IllegalArgumentException("server error for " + key);
    }


    @GetMapping("/rt02")
    @ResponseBody
    public BaseRsp<String> rt02(@RequestParam("key") String key){
        log.info("收到请求02:  key:{}", key);
        return  getResult(key);
    }


}
