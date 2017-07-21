package com.spring.validator.constraints;

import com.spring.validator.constraints.annotation.OperMatchPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class OperMatchPlaceValidator implements ConstraintValidator<OperMatchPlace, Object> {
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    @Autowired
    private DataValidator dataValidator;

    private String operNo;

    private String placeNo;

    @Override
    public void initialize(OperMatchPlace constraintAnnotation) {

        dataValidator = ServiceUtils.getDataValidator();

        operNo = constraintAnnotation.operNo();
        placeNo = constraintAnnotation.placeNo();

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean result = true;
        try {
            String operNoValue = PARSER.parseExpression(operNo).getValue(value).toString();

            String placeNoValue = PARSER.parseExpression(placeNo).getValue(value).toString();

            result = dataValidator.ifOperNoMatchWithPlaceNo(operNoValue, placeNoValue);
        }catch (Exception e){
            result = false;
        }
        return result;
    }
}