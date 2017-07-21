package com.spring.logging.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ServiceLogging {
    //模块名
    String moduleName() default "";
    //操作内容
    String option() default "";
}