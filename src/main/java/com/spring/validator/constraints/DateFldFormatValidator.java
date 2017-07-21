package com.spring.validator.constraints;

import com.spring.validator.constraints.annotation.DateFldFormat;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class DateFldFormatValidator implements ConstraintValidator<DateFldFormat, Object>
{

    private String datePattern;

    @Override
    public void initialize(DateFldFormat dateFormat) {
        datePattern = dateFormat.datePattern();
        if(StringUtils.isEmptyOrWhitespaceOnly(datePattern)) {
            datePattern = "yyyy-MM-dd HH:mm:ss";
        }
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context)
    {
        try {
            DateUtils.parseDateStrictly((String)value, datePattern);
        } catch (final Exception ignore) {
            // ignore
        }
        return true;
    }
}
