package com.spring.validator.constraints;

import com.mysql.jdbc.StringUtils;
import com.spring.validator.constraints.annotation.MerchantNo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class MerchantNoValidator implements ConstraintValidator<MerchantNo, Object> {
    @Autowired
    private DataValidator dataValidator;

    @Override
    public void initialize(MerchantNo merchantNo) {
        //SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.dataValidator = ServiceUtils.getDataValidator();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context)
    {
        boolean result = true;
        try {
            String merchantNo = (String)value;
            if(!StringUtils.isEmptyOrWhitespaceOnly(merchantNo)) {
                result = dataValidator.validateMerchantNo(merchantNo);
            }
        } catch (final Exception ignore) {
            // ignore
            result = false;
        }
        return result;
    }
}
