package com.burukeyou.uniapi.http.annotation;

import com.burukeyou.uniapi.http.support.UniHttpApiConstant;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**  Http 响应内容配置
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
     * 是否启用唯一id路径，防止文件被覆盖，保证文件路径唯一，默认会在 saveDir 下再拼接上此路径
     */
    boolean uuid() default true;
}
