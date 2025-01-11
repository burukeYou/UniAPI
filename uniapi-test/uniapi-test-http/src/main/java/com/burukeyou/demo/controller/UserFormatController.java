package com.burukeyou.demo.controller;

import com.alibaba.fastjson2.JSON;
import com.burukeyou.demo.entity.xml.UserXmlDTO;
import com.burukeyou.demo.entity.xml.UserXmlReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/user-web/format")
@Controller
public class UserFormatController {

    // application/x-www-form-urlencoded
    @GetMapping("/get01")
    public ResponseEntity<String> get01() {
        // 创建一个Map来存储键值对
        Map<String, String> formData = new LinkedHashMap<>();
        formData.put("username", "Alice");
        formData.put("password", "123456");

        // 构造 application/x-www-form-urlencoded 格式的数据
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(entry.getKey()).append('=').append(entry.getValue());
        }

        // 构建响应体
        String body = sb.toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/get02", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public UserXmlDTO get02(){
       return new UserXmlDTO("san",999);
    }

    @PostMapping(value = "/get03",
            consumes =MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public Object get03(@RequestBody UserXmlReq dto){
        log.info("收到请求: {}", JSON.toJSONString(dto));
        dto.setAge(10000);
        dto.setName(dto.getName() + " SB");
        return dto;
    }

}
