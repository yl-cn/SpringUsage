package com.spring.http.annotion;

import java.lang.annotation.*;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ObjectDesc {
    String value() default "";
    String moduleName() default "";
    String option() default "";
    boolean redirect() default false;
    boolean needLog() default true; // 标志该 类 或者 方法 是否需要向数据库中记录日志。
}