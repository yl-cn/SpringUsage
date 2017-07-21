package com.spring.validator.constraints;

import com.spring.util.IdCardValidator;
import com.spring.validator.constraints.annotation.IdCard;
import com.mysql.jdbc.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IdCardFldValidator implements ConstraintValidator<IdCard, Object>
{

    private boolean allowEmpty = false;

    @Override
    public void initialize(final IdCard constraintAnnotation)
    {
        allowEmpty = constraintAnnotation.allowEmpty();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context)
    {
        try {
            String idCardStr = (String) value;

            if(allowEmpty && StringUtils.isEmptyOrWhitespaceOnly(idCardStr)) {
                    return true;
            }

            return IdCardValidator.validateIdCardStrictly(idCardStr);
        } catch (final Exception ignore) {
            // ignore
        }
        return true;
    }
}
