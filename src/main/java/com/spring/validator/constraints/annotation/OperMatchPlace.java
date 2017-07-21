package com.spring.validator.constraints.annotation;

import com.spring.validator.constraints.OperMatchPlaceValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = OperMatchPlaceValidator.class)
public @interface OperMatchPlace {

    String placeNo() default "placeNo";

    String operNo() default "operNo";

    String message() default "代办员与网点不匹配";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
