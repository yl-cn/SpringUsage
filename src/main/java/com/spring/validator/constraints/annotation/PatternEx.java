package com.spring.validator.constraints.annotation;

import com.spring.validator.constraints.PatternExValidator;
import org.hibernate.validator.constraints.LuhnCheck;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = PatternExValidator.class
)
//@Pattern
public @interface PatternEx {
    String regexp();

    PatternEx.Flag[] flags() default {};

    String message() default "{javax.validation.constraints.Pattern.message}";

    boolean isMatch() default true;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

/*    @OverridesAttribute(
            constraint = Pattern.class,
            name = "regexp"
    )*/

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        PatternEx[] value();
    }

    public static enum Flag {
        UNIX_LINES(1),
        CASE_INSENSITIVE(2),
        COMMENTS(4),
        MULTILINE(8),
        DOTALL(32),
        UNICODE_CASE(64),
        CANON_EQ(128);

        private final int value;

        private Flag(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}
