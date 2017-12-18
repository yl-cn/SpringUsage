package com.spring.util;

import lombok.experimental.UtilityClass;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class MvelUtil {

    public static Set<String> getVars(String expression) {
        ParserContext parserContext = ParserContext.create();
        MVEL.analysisCompile(expression, parserContext);
        Map<String, Class> variables = parserContext.getVariables();

        return variables.entrySet().stream()
                .map(entry->entry.getKey())
                .collect(Collectors.toCollection(HashSet::new));
    }

    public static Set<String> getPropsOrFields(String expression) {
        ParserContext parserContext = ParserContext.create();
        MVEL.analysisCompile(expression, parserContext);
        Map<String, Class> fields = parserContext.getInputs();

        return fields.entrySet().stream()
                .map(entry->entry.getKey())
                .collect(Collectors.toCollection(HashSet::new));
    }
}
