package com.burukeyou.demo.controller;

import com.alibaba.fastjson2.JSON;
import com.burukeyou.demo.entity.Add4DTO;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.demo.util.HttpResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Slf4j
@RequestMapping("/user-web")
@Controller
public class UserController {

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/add")
    @ResponseBody
    public BaseRsp<String> add(@RequestParam("name") String name){
        log.info("收到请求1- name:{}",name);
        return BaseRsp.ok(name + "-1");
    }

    @DeleteMapping("/add2")
    @ResponseBody
    public BaseRsp<String> add2(@RequestParam("name") String name,
                                @RequestParam("id") Integer id){
        log.info("收到请求2- name:{} id:{}",name,id);
        return BaseRsp.ok(name + "-2");
    }

    @GetMapping("/add3")
    @ResponseBody
    public BaseRsp<String> add3(@RequestParam("name") String name,
                                @RequestHeader("token") String token){
        log.info("收到请求3- name:{} token:{}",name,token);
        return BaseRsp.ok(name + "-2");
    }

    @PostMapping("/add4")
    @ResponseBody
    public BaseRsp<Add4DTO> add4(@RequestBody Add4DTO req){
        log.info("收到请求4- req:{}", JSON.toJSONString(req));
        req.setName("牛逼");
        return BaseRsp.ok(req);
    }

    @PostMapping("/add41")
    @ResponseBody
    public BaseRsp<String> add41(@RequestBody String req){
        log.info("收到请求4- req:{}", JSON.toJSONString(req));
        return BaseRsp.ok(req + "-3");
    }

    @GetMapping("/add5/{userId}")
    @ResponseBody
    public BaseRsp<Long> add5(@PathVariable("userId") Long userId){
        log.info("收到请求5- userId:{}",userId);
        return BaseRsp.ok(userId + 10);
    }

    // Content-Type:  application/json
    @PutMapping("/add6")
    @ResponseBody
    public BaseRsp<Add4DTO> add6(@RequestParam("id") Long id,
                                 @RequestHeader("name") String name,
                                 @RequestBody Add4DTO req){
        log.info("收到请求6- id:{} name:{} req:{}", id,name,JSON.toJSONString(req));
        req.setName("牛逼");
        return BaseRsp.ok(req);
    }

    // Content-Type:  application/octet-stream
    //  consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE
    @PostMapping(value = "/add7")
    public BaseRsp<String> add7(@RequestBody byte[] data) throws IOException {
        log.info("收到请求7- dataSize:{}",data.length);
        return BaseRsp.ok("1");
    }

    // Content-Type:  application/x-www-form-urlencoded
    @PostMapping("/add8")
    @ResponseBody
    public BaseRsp<String> add8(Add4DTO formData) throws IOException {
        log.info("收到请求8- data:{}",JSON.toJSONString(formData));
        return BaseRsp.ok("1");
    }

    //  Content-Type:  multipart/form-data
    @PostMapping("/add9")
    @ResponseBody
    public BaseRsp<String> add9(@RequestPart("userImg") MultipartFile file,
                                @RequestPart("logoImg") MultipartFile[] file2,
                                Add4DTO formData,
                                @RequestParam("reqNo") String reqNo) throws IOException {
        log.info("收到请求9- data:{} size1:{} size2:{}",JSON.toJSONString(formData),file.getBytes().length,file2.length);
        return BaseRsp.ok("1");
    }

    @PostMapping("/add10")
    public void add10(HttpServletResponse response) throws IOException {
        String filePath = "img/a.txt";
        File file = resourceLoader.getResource("classpath:" + filePath).getFile();
        HttpResponseUtil.write(file,response);
        log.info("下载完成");
    }


    @PostMapping("/update")
    @ResponseBody
    public BaseRsp<String> update(@RequestParam("name") String name,
                                  @RequestParam("age") Integer age,
                                  @RequestHeader("clientType") String clientType,
                                  @RequestHeader("userId") Long userId,
                                  @RequestParam("d") String d,
                                  @RequestParam("e") String e,HttpServletRequest request){
        log.info("收到请求1- name:{} age:{} clientType:{} userId:{}",name,age,clientType,userId);
        return BaseRsp.ok(name + "-1");
    }

    @PostMapping("/update1")
    @ResponseBody
    public BaseRsp<String> update1(@CookieValue(value = "userToken",required = false) String token,
                                   HttpServletRequest request,
                                   HttpServletResponse httpResponse){
        log.info("收到请求 token:{}",token);

        Cookie[] cookies = request.getCookies();

        Cookie cookie = new Cookie("id","2");
        cookie.setDomain("sb");

        Cookie cookie1 = new Cookie("name","jay");
        cookie1.setHttpOnly(true);

        Cookie cookie2 = new Cookie("age","30");
        cookie2.setPath("/user-web");
        cookie2.setMaxAge(30);

        httpResponse.addCookie(cookie);
        httpResponse.addCookie(cookie1);
        httpResponse.addCookie(cookie2);

        return BaseRsp.ok("-1");
    }


    @PostMapping("/update3")
    @ResponseBody
    public BaseRsp<String> update3(@RequestParam("ids") int[] arr){
        log.info("收到请求1- arr:{} ",arr);
        return BaseRsp.ok(  "-1");
    }

    @GetMapping(path = "/del",produces = "text/plain")
    @ResponseBody
    public String del01(@RequestParam("name") String name){
        log.info("收到请求del01- name:{}",name);
        return "牛哔哔哔哔哔哔del01";
    }

    @GetMapping(path = "/del2",produces = "application/xml")
    @ResponseBody
    public String del02(){
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<root>\n" +
                "  <element>Some data</element>\n" +
                "</root>";
    }


    @PostMapping(path = "/del03")
    @ResponseBody
    public void del01(){
        log.info("收到请求del03");
    }
}
