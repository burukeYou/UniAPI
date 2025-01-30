package com.burukeyou.api;

import com.burukeyou.demo.annotation.UserHttpApi;
import com.burukeyou.demo.entity.json.StuDTO;
import com.burukeyou.demo.entity.json.StuReq;
import com.burukeyou.demo.entity.xml.UserXmlDTO;
import com.burukeyou.entity.*;
import com.burukeyou.uniapi.http.annotation.HttpResponseCfg;
import com.burukeyou.uniapi.http.annotation.JsonPathMapping;
import com.burukeyou.uniapi.http.annotation.ModelBinding;
import com.burukeyou.uniapi.http.annotation.param.BodyJsonPar;
import com.burukeyou.uniapi.http.annotation.param.BodyXmlPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;
import com.burukeyou.uniapi.http.annotation.request.PostHttpInterface;
import com.burukeyou.uniapi.util.ClzUtil;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

@UserHttpApi
public interface FormatServiceApi {

    @GetHttpInterface("/user-web/format/get02")
    UserXmlDTO get02();

    @PostHttpInterface("/user-web/format/get03")
    void get03(@BodyXmlPar UserXmlDTO dto);

    @PostHttpInterface(path = "/user-web/del05")
    @HttpResponseCfg(afterJsonPathUnPack =  {"$.bbq","$.nums","$.configs[*].detail","$.id","$.info","$.users","$.son","$.son.detail"})
    @JsonPathMapping("$.son.detail")
    StuDTO get04();

    @PostHttpInterface(path = "/user-web/del05")
    @HttpResponseCfg(afterJsonPathUnPack =  {"$.bbq","$.nums","$.configs[*].detail","$.id","$.info","$.users","$.son","$.son.detail"})
    @ModelBinding
    StuDTO get05();

    @PostHttpInterface(path = "/xxxx")
    void get06(@BodyJsonPar StuReq req);

    @PostHttpInterface(path = "/user-web/del05")
    @HttpResponseCfg(afterJsonPathUnPack =  {"$.bbq","$.nums","$.configs[*].detail","$.id","$.info","$.users","$.son","$.son.detail"})
    @ModelBinding
    BaseResultA<UserInfo> get07();

    @PostHttpInterface(path = "/user-web/del05")
    @HttpResponseCfg(afterJsonPathUnPack =  {"$.bbq","$.nums","$.configs[*].detail","$.id","$.info","$.users","$.son","$.son.detail"})
    @ModelBinding
    BaseResultC<StuInfoA,UserInfo> get08();

    @PostHttpInterface(path = "/user-web/del05")
    @HttpResponseCfg(afterJsonPathUnPack =  {"$.bbq","$.nums","$.configs[*].detail","$.id","$.info","$.users","$.son","$.son.detail"})
    @ModelBinding
    BaseResultD<BaseResultB<UserInfo>> get09();

    @PostHttpInterface(path = "/user-web/del05")
    @HttpResponseCfg(afterJsonPathUnPack =  {"$.bbq","$.nums","$.configs[*].detail","$.id","$.info","$.users","$.son","$.son.detail"})
    @ModelBinding
    BaseResultC<StuInfoA,BaseResultB<UserInfo>> get10();

    @PostHttpInterface(path = "/user-web/del05")
    @HttpResponseCfg(afterJsonPathUnPack =  {"$.bbq","$.nums","$.configs[*].detail","$.id","$.info","$.users","$.son","$.son.detail"})
    @ModelBinding
    BaseResultC<StuInfoA,BaseResultD<BaseResultB<UserInfo>>> get11();


     static void main(String[] args) throws Exception {
         TypeVariable<Class<BaseResultA>>[] variables = BaseResultA.class.getTypeParameters();
         TypeVariable<Class<BaseResultB>>[] variables1 = BaseResultB.class.getTypeParameters();
         TypeVariable<Class<BaseResultC>>[] variables2 = BaseResultC.class.getTypeParameters();

         Class<?> aClass = ClzUtil.resolveClass(variables[0]);

         System.out.println();

         // 获取 getAA() 方法的 Method 对象
        Method method = FormatServiceApi.class.getMethod("get07");

        // 获取方法的泛型返回类型
        Type returnType = method.getGenericReturnType();

        // 检查是否是 ParameterizedType
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException("返回类型不是泛型类型");
        }

        ParameterizedType parameterizedType = (ParameterizedType) returnType;

        // 获取 Rsp 的实际类型参数（例如 User.class 和 Weather.class）
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        // 获取 Rsp 的原始类对象（即 Rsp.class）
        Class<?> rspClass = (Class<?>) parameterizedType.getRawType();

        // 获取 Rsp 的泛型类型参数声明（即 R 和 T 的 TypeVariable）
        TypeVariable<?>[] typeParameters = rspClass.getTypeParameters();

        // 将 TypeVariable 映射到其在声明中的索引
        Map<TypeVariable<?>, Integer> typeVarIndexMap = new HashMap<>();
        for (int i = 0; i < typeParameters.length; i++) {
            typeVarIndexMap.put(typeParameters[i], i);
        }

        // 遍历 Rsp 的字段，动态解析每个字段的实际类型
        for (Field field : rspClass.getDeclaredFields()) {
            Type fieldType = field.getGenericType();

            // 如果字段类型是 TypeVariable（例如 R 或 T）
            if (fieldType instanceof TypeVariable) {
                TypeVariable<?> typeVar = (TypeVariable<?>) fieldType;

                // 获取该 TypeVariable 在声明中的索引
                Integer index = typeVarIndexMap.get(typeVar);
                if (index != null && index < actualTypeArguments.length) {
                    Type actualType = actualTypeArguments[index];

                    // 确保实际类型是 Class 类型（例如 User.class 或 Weather.class）
                    if (actualType instanceof Class) {
                        System.out.printf("字段 %s 的实际类型: %s%n", field.getName(), ((Class<?>) actualType).getSimpleName());
                    }
                }
            }
        }

        System.out.println();
    }
}


