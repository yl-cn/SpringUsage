package com.spring.validator.constraints;

import com.spring.validator.constraints.annotation.PatternEx;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class PatternExValidator implements ConstraintValidator<PatternEx, CharSequence>
{
    private static final Log log = LoggerFactory.make();

    private boolean isMatch = true;

    private java.util.regex.Pattern pattern;


    @Override
    public void initialize(PatternEx patternEx) {
        isMatch = patternEx.isMatch();

        PatternEx.Flag[] flags = patternEx.flags();
        int intFlag = 0;
        for ( PatternEx.Flag flag : flags ) {
            intFlag = intFlag | flag.getValue();
        }

        try {
            pattern = java.util.regex.Pattern.compile( patternEx.regexp(), intFlag );
        }
        catch ( PatternSyntaxException e ) {
            throw log.getInvalidRegularExpressionException( e );
        }
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if ( value == null ) {
            return true;
        }

        Matcher m = pattern.matcher( value );

        return isMatch ? m.matches() : !m.matches();
    }
}