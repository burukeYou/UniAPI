

# HttpAPI 
一个声明式的Http请求对接框架，能以极快的方式完成对一个接口的对接和使用，只要配置一下即可重复使用，
不需要开发者去关注如何发送一个请求，如何去传递Http请求参数，以及如何对请求结果进行处理和反序列化，这些框架都帮你一一实现 
就像配置 `Spring的Controller` 那样简单，不，甚至更简单。


## 2、快速开始
### 2.1、引入依赖
```xml
    <dependency>
      <groupId>io.github.burukeyou</groupId>
      <artifactId>uniapi-http</artifactId>
      <version>3.1</version>
    </dependency>
```

### 2.2、对接接口
在类上标记@HttpAPI注解，表示定义一个HttpAPI, 然后就可以去定义每个方法去对接每个接口。 
如下面两个方法的配置则对接了 GET http://localhost:8080/getUser和 POST http://localhost:8080/addUser 两个接口


```java
@HttpApi(url = "http://localhost:8080")
interface UserHttpAPI {
    
   @GetHttpInterface("/getUser")
   BaseRsp<String> getUser(@QueryPar("name") String param,@HeaderPar("userId") Integer id);
    
   @PostHttpInterface("/addUser")
   BaseRsp<Add4DTO> addUser(@BodyJsonPar Add4DTO req);
}
```

@QueryPar 表示将参数值放到Http请求的查询参数内

@HeaderPar    表示将参数值放到Http请求的请求头里

@BodyJsonPar 表示将参数值放到Http请求body内，并且content-type是application/json


1、getUser方法最终构建的Http请求报文为
```
GET http://localhost:8080/getUser?name=param
Header:
    userId: id
```
 
 
2、addUser最终构建的Http请求报文为 
```
        POST:  http://localhost:8080/addUser 
        Header: 
            Content-Type:   application/json
        Body:
            {"id":1,"name":"jay"}
```

        
然后使用 @Autowired UserHttpAPI 去引入该HttpAPI去使用即可，


### 2.3、声明定义的HttpAPI的包扫描路径

@UniAPIScan("com.xxx.demo.api")





