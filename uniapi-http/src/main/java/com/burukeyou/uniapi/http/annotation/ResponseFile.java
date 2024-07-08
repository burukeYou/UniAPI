package com.burukeyou.uniapi.http.annotation;

import com.burukeyou.uniapi.http.support.UniHttpApiConstant;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 方法返回值为File对象时的配置，可以配置文件保存位置
 *
 * 1、当标记在方法参数上时， 此时注解@ResponseFile所有字段配置均无效，而方法参数值则是此次保存的文件位置，可以是具体的路径也可以是目录
 * 2、当标记在方法上时，会使用配置的saveDir的值作为保存目录（支持环境变量）
 *
 * @author caizhihao
 */
@Inherited
@Target({ElementType.METHOD,ElementType.TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseFile {

    /**
     *  保存目录
     */
    @AliasFor("value")
    String saveDir() default UniHttpApiConstant.DEFAULT_FILE_SAVE_DIR;

    /**
     * 保存目录
     */
    @AliasFor("saveDir")
    String value() default UniHttpApiConstant.DEFAULT_FILE_SAVE_DIR;

    /**
     * 是否启用唯一id路径，防止文件被覆盖，保证文件路径唯一，默认会在 saveDir 下再拼接上此uid作为最终路径
     */
    boolean uuid() default true;
}
