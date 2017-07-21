package com.spring.validator.constraints.annotation;

import com.spring.validator.constraints.AllOrNoneValidator;

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
@Constraint(validatedBy = AllOrNoneValidator.class)
public @interface AllOrNone {
    String[] value();

    String message() default "{AllOrNone.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
