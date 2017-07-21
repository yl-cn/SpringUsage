package com.spring.logging.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ControllerLogging {
    //模块名
    String moduleName() default "";
    //操作内容
    String option() default "";
}
