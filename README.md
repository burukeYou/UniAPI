

# 1、UniHttp 
一个声明式的Http请求对接框架，能以极快的方式完成对一个第三方Http接口的对接和使用，只要配置一下即可重复使用，
不需要开发者去关注如何发送一个请求，如何去传递Http请求参数，以及如何对请求结果进行处理和反序列化，这些框架都帮你一一实现 
就像配置 `Spring的Controller` 那样简单，只不过相当于是反向配置而已

该框架更注重于如何保持高内聚和可读性高的代码情况下与快速第三方渠道接口进行对接和集合，
而非像传统编程式的Http请求客户端（比如HttpClient、Okhttp）那样专注于如何去发送和响应一个Http请求，二者并不冲突只是注重点不一样，UniHttp 目前底层也是用的Okhttp去发送请求。
与其说的是对接的Http接口，不如说是对接的第三方渠道，UniHttp可支持自定义接口渠道方HttpAPI注解以及一些自定义的对接和交互行为 ，为此扩展了发送和响应和反序列化一个Http请求接口的各种生命周期钩子需要开发者去自定义实现。



# 2、快速开始
## 2.1、引入依赖（待发布到中央仓库）
```xml
    <dependency>
      <groupId>io.github.burukeyou</groupId>
      <artifactId>uniapi-http</artifactId>
      <version>0.0.1</version>
    </dependency>
```

## 2.2、对接接口
在类上标记@HttpAPI注解，用户表示定义一个Http接口的信息, 然后就可以去编写每个方法去对接每个接口。 
如下面两个方法的配置则对接了 GET http://localhost:8080/getUser和 POST http://localhost:8080/addUser 两个接口


```java
@HttpApi(url = "http://localhost:8080")
interface UserHttpApi {
    
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



## 2.3、声明定义的HttpAPI的包扫描路径

@UniAPIScan("com.xxx.demo.api") 会自动为改接口生成代理对象并且注入到Spring容器中，
之后只需要像使用Spring的其他bean一样，依赖注入使用即可

### 2.4 依赖注入使用即可

```java
@Service
class UserAppService {
    
    @Autowired
    private UserHttpApi userHttpApi;
    
    public void doSomething(){
        userHttpApi.getUser("jay",3);
    }
} 

```

# 3、特性

##  @HttpApi注解

## HttpApiProcessor 生命周期钩子

## @HttpInterface注解

##  各种@Par注解

## HttpResponse




# 4、企业级渠道对接实战
案例背景：假设现在需要对接一个某天气服务的所有接口，需要在请求cookie带上一个token字段和sessionId字段， 这两个字段的值需要每次接口调用前先手动调渠道方的一个特定的接口申请获取，token值在该接口返回值中返回，sessionId在该接口的响应头中返回
然后还需要在请求头上带上一个sign签名字段， 该sign签名字段生成规则需要用渠道方提供的公钥对所有请求体和请求参数进行加签生成。
然后还需要在每个接口的查询参数上都带上一个渠道方分配的客户端appId。

## 4.1、自定义该渠道方的HttpAPI注解
比如现在对接的是某团，所以叫MTuanHttpApi吧，然后需要在该注解上标记@HttpApi注解，并且需要配置processor字段，需要去自定义实现一个HttpApiProcessor这个具体实现后续讲。
有了这个注解后就可以自定义该注解与对接渠道方相关的各种字段配置，当然也可以不定义。 接下来重点讲讲HttpApiProcessor。

```java
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@HttpApi(processor = MTuanHttpApiProcessor.class)
public @interface MTuanHttpApi {

    /**
     * 渠道方域名地址
     */
    @AliasFor(annotation = HttpApi.class)
    String url() default "${channel.mtuan.url}";

    /**
     * 渠道方分配的appId
     */
    String appId() default "${channel.mtuan.appId}";
}

```
```java
@Slf4j
@Component
public class MTuanHttpApiProcessor implements HttpApiProcessor<MTuanHttpApi> {
    
}
```



## 4.2 对接接口
有了@MTuanHttpApi注解之后就可以开始对接接口了，比如假设有两个接口要对接。一个就是前面说的获取令牌的接口。 一个是获取天气情况的接口。
为什么getToken方法返回值是 `HttpResponse`, 这是UniHttp内置的原始Http响应对象，方便我们去拿到原始Http响应体的一些内容（比如响应状态码、响应cookie）。 
其中的泛型BaseRsp<TokenDTO>才是实际的Http响应体反序列化后的内容。 而getCityWeather方法没有使用HttpResponse包装,
BaseRsp<WeatherDTO>只是单纯Http响应体反序列化后的内容，这是两者的区别。 后面会介绍 `HttpResponse`，其实大部份接口是不关注HttpResponse的可以不用去配置。

```java
@MTuanHttpApi
public interface WeatherApi {
    
    /**
     * 根据城市名获取天气情况
     */
    @GetHttpInterface("/getCityByName")
    BaseRsp<WeatherDTO> getCityWeather(@QueryPar("city") String cityName);

    /**
     *  根据appId和公钥获取令牌
     */
    @PostHttpInterface("/getToken")
    HttpResponse<BaseRsp<TokenDTO>> getToken(@HeaderPar("appId") String appId, @HeaderPar("publicKey")String publicKey);

}

```


## 4.3、自定义HttpApiProcessor
在之前我们自定义了一个@MTuanHttpApi注解上指定了一个MTuanHttpApiProcessor，接下来我们去实现他的具体内容为了实现我们案例背景里描述的功能
HttpApiProcessor表示是一个发送和响应和反序列化一个Http请求接口的各种生命周期钩子，开发者可以在里面自定义编写与渠道方相关的各种对接逻辑。


```java
@Slf4j
@Component
public class MTuanHttpApiProcessor implements HttpApiProcessor<MTuanHttpApi> {

    /**
     *  渠道方分配的公钥
     */
    @Value("${channel.mtuan.publicKey}")
    private String publicKey;

    @Value("${channel.mtuan.appId}")
    private String appId;

    @Autowired
    private Environment environment;
    
    @Autowired
    private WeatherApi weatherApi;

    /** 实现-postBeforeHttpMetadata： 发送Http请求之前会回调该方法，可对Http请求体的内容进行二次处理
     *
     * @param httpMetadata              原来的请求体
     * @param methodInvocation          被代理的方法
     * @return                          新的请求体
     */
    @Override
    public HttpMetadata postBeforeHttpMetadata(HttpMetadata httpMetadata, HttpApiMethodInvocation<MTuanHttpApi> methodInvocation) {
        /**
         * 在查询参数中添加提供的appId字段
         */
        // 获取MTuanHttpApi注解
        MTuanHttpApi apiAnnotation = methodInvocation.getProxyApiAnnotation();

        // 获取MTuanHttpApi注解的appId，由于该appId是环境变量所以我们从environment中解析取出来
        String appIdVar = apiAnnotation.appId();
        appIdVar = environment.resolvePlaceholders(appIdVar);

        // 添加到查询参数中
        httpMetadata.putQueryParam("appId",appIdVar);

        /**
         *  生成签名sign字段
         */
        // 获取所有查询参数
        Map<String, Object> queryParam = httpMetadata.getHttpUrl().getQueryParam();

        // 获取请求体参数
        HttpBody body = httpMetadata.getBody();

        // 生成签名
        String signKey = createSignKey(queryParam,body);

        // 将签名添加到请求头中
        httpMetadata.putHeader("sign",signKey);

        return httpMetadata;
    }

    private String createSignKey(Map<String, Object> queryParam, HttpBody body) {
        // todo 伪代码
        // 1、将查询参数拼接成字符串
        String queryParamString = queryParam.entrySet()
                .stream().map(e -> e.getKey() + "="+e.getValue())
                .collect(Collectors.joining(";"));

        // 2、将请求体参数拼接成字符串
        String bodyString = "";
        if (body instanceof HttpBodyJSON){
            // application/json  类型的请求体
            bodyString = body.toStringBody();
        }else if (body instanceof HttpBodyFormData){
            // application/x-www-form-urlencoded 类型的请求体
            bodyString = body.toStringBody();
        }else if (body instanceof HttpBodyMultipart){
            // multipart/form-data 类型的请求体
            bodyString =  body.toStringBody();
        }

        // 使用公钥publicKey 加密拼接起来
        String sign = publicKey + queryParamString + bodyString;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(sign.getBytes());
            return new String(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  实现-postBeforeHttpMetadata： 发送Http请求时，可定义发送请求的行为 或者打印请求和响应日志。
     */
    @Override
    public HttpResponse<?> postSendHttpRequest(HttpSender httpSender, HttpMetadata httpMetadata) {
        // 1、动态获取token和sessionId
        HttpResponse<String> httpResponse = weatherApi.getToken(appId, publicKey);

        // 从响应体获取令牌token
        String token = httpResponse.getBodyResult();
        // 从响应头中获取sessionId
        String sessionId = httpResponse.getHeader("sessionId");

        // 把这两个值放到此次的请求cookie中
        httpMetadata.addCookie(new Cookie("token",token));
        httpMetadata.addCookie(new Cookie("sessionId",sessionId));
        
        log.info("开始发送Http请求 请求接口:{} 请求体:{}",httpMetadata.getHttpUrl().toUrl(),httpMetadata.toHttpProtocol());

        // 使用框架内置实现发送请求
        HttpResponse<?> rsp = HttpApiProcessor.super.postSendHttpRequest(httpSender, httpMetadata);

        log.info("开始发送Http请求 响应结果:{}",rsp.toHttpProtocol());
        
        return rsp;
    }

    /**
     *  实现-postAfterHttpResponseBodyResult： 反序列化后Http响应体的内容后回调，可对该结果进行二次处理返回
     * @param bodyResult                     Http响应体反序列化后的结果
     * @param rsp                            原始Http响应对象
     * @param method                         被代理的方法
     * @param httpMetadata                   Http请求体
     */
    @Override
    public Object postAfterHttpResponseBodyResult(Object bodyResult, HttpResponse<?> rsp, Method method, HttpMetadata httpMetadata) {
        if (bodyResult instanceof BaseRsp){
            BaseRsp baseRsp = (BaseRsp) bodyResult;
            // 设置
            baseRsp.setCode(999);
        }
        
        return bodyResult;
    }
}

```

上面我们分别重写了postBeforeHttpMetadata、postSendHttpRequest、postAfterHttpResponseBodyResult三个生命周期的钩子方法去完成我们的需求，实现他们可以方便的发送一个Http请求的
过程中去织入我们的对接行为






