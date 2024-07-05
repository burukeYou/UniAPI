package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;


/**
 *
 * 支持标记的参数类型举例:
 *       Map                        @CookiePar                 Map
 *       单个Cookie对象              @CookiePar                {@link com.burukeyou.uniapi.http.support.Cookie}
 *       Cookie集合                 @CookiePar                List<{@link com.burukeyou.uniapi.http.support.Cookie}>
 *       字符串(指定name)            @CookiePar("userId")      String            当成单个cookie键值对处理
 *       字符串(不指定name)          @CookiePar                String            当成完整的cookie字符串处理
 *
 *
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CookiePar {

    /**
     *   单个cookie键值对的key名
     */
    String value() default "";
}
