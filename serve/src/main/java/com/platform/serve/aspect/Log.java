package com.platform.serve.aspect;

import java.lang.annotation.*;

/**
 * 自定义注解：日志输出
 *
 * @Author lds
 * @Date 2021/3/22 17:39
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Log {
    String value() default "";

    String node() default "";
}
