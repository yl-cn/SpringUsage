package com.spring.validator.constraints.annotation;

import com.spring.validator.constraints.CalculationConditionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.math.BigDecimal;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CalculationConditionValidator.class)
public @interface CalculationCondition {

    String expression() default "1==1";

    int scale() default 4;

    int round() default BigDecimal.ROUND_HALF_UP;

    String message() default "条件不匹配";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
