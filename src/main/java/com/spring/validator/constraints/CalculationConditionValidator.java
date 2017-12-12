package com.spring.validator.constraints;

import com.spring.validator.constraints.annotation.CalculationCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CalculationConditionValidator implements ConstraintValidator<CalculationCondition, Object>
{
    private String expression;

    private int scale;

    private int round;

    @Override
    public void initialize(final CalculationCondition constraintAnnotation) {

        expression = constraintAnnotation.expression();
        scale = constraintAnnotation.scale();
        round = constraintAnnotation.round();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context)
    {
        boolean result;
        try {
            ParserContext parserContext = ParserContext.create();
            MVEL.analysisCompile(expression, parserContext);
            Map<String, Class> fields = parserContext.getInputs();

            Map<String, BigDecimal> fieldMaps =  toBigDecimalMap(value, fields);

            result = (boolean)MVEL.eval(expression, fieldMaps);

        } catch (final Exception ignore) {
            // ignore
            log.error("计算条件验证异常：{}",ignore.getMessage());
            result = false;
        }
        return result;
    }

    public Map<String, BigDecimal> toBigDecimalMap(Object entity, Map<String, Class> fields) throws Exception{
        Map<String, BigDecimal> result = new HashMap<>();

        if(null != entity && !CollectionUtils.isEmpty(fields)) {

            for(Map.Entry entry : fields.entrySet()) {
                String key = (String)entry.getKey();
                Field field = ReflectionUtils.findField(entity.getClass(), key);
                field.setAccessible(true);

                Object obj = field.get(entity);
                BigDecimal value = (BigDecimal)ConvertUtils.convert(obj, BigDecimal.class);
                result.put(key, value.setScale(scale, round));

            }

        }

        return result;
    }


}
