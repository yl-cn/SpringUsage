package com.spring.validator.constraints;

import com.spring.validator.constraints.annotation.DataIn;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;


public class DataInValidator implements ConstraintValidator<DataIn, Object> {

    private String in;

    @Override
    public void initialize(DataIn dataIn) {
        in = dataIn.in();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        boolean result = true;

        try {
            if (null != value && StringUtils.isNotBlank(this.in)) {

                Long count = Arrays.stream(this.in.split(","))
                        .map(s -> ConvertUtils.convert(s, value.getClass()))
                        .filter(a -> a.equals(value)).count();

                result = (count>0);
            }
        } catch (final Exception ignore) {
            // ignore
            result = false;
        }
        return result;
    }
}
