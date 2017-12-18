package com.spring.validator.constraints;

import com.spring.validator.constraints.annotation.NotNullString;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotNullStringValidator implements ConstraintValidator<NotNullString, Object>
{
    private boolean allowEmpty = false;

    @Override
    public void initialize(NotNullString notNullString) {
        allowEmpty = notNullString.allowEmpty();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context)
    {
        try {
            String fldValue = (String)value;
            if(allowEmpty && StringUtils.isBlank(fldValue) ) {
                return true;
            }

            if("null".equals(StringUtils.lowerCase(fldValue.trim()))) {
                return false;
            }

        } catch (final Exception ignore) {
            // ignore
            return false;
        }
        return true;
    }
}
