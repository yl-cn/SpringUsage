package com.spring.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.ast.VariableReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class SpelUtil {
    //boolean result = new SpelExpressionParser().parseExpression(expression).getValue(obj, boolean.class);
    //不支持obj为map

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    public static Set<String> getPropsOrFields(String expression) {
        SpelExpression parseExpression = (SpelExpression) PARSER.parseExpression(expression);
        SpelNode node = parseExpression.getAST();
        return getPropsOrFields(node);
    }

    public static Set<String> getVars(String expression) {
        SpelExpression parseExpression = (SpelExpression) PARSER.parseExpression(expression);
        SpelNode node = parseExpression.getAST();
        return getVars(node);
    }

    public static Set<String> getVars(SpelNode node) {
        Set<String> vars = new HashSet<>();
        if (node == null) {
            return vars;
        }

        if (node instanceof VariableReference) {
            // Remove the "#" to get the actual variable name
            vars.add(StringUtils.remove(node.toStringAST(), "#"));
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            SpelNode child = node.getChild(i);
            vars.addAll(getVars(child));
        }
        return vars;
    }

    private Set<String> getPropsOrFields(SpelNode node) {
        Set<String> vars = new HashSet<>();
        if (node == null) {
            return vars;
        }

        if (node instanceof PropertyOrFieldReference) {
            vars.add(node.toStringAST());
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            SpelNode child = node.getChild(i);
            vars.addAll(getPropsOrFields(child));
        }

        return vars;
    }
}
