package com.spring.logging;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

public abstract class BaseInterceptor {

    /**
     * 获取注解中对方法的描述信息
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    protected  <T extends Annotation> String getMethodDescription(JoinPoint joinPoint, Class<T> annotationClass) {

        String methodName = joinPoint.getSignature().getName();
        String description = methodName;
        Class targetClass = joinPoint.getTarget().getClass();

        try {
            Class[] argumentTypes = getParamTypes(joinPoint.getArgs());

            Method method = ReflectionUtils.findMethod(targetClass, methodName, argumentTypes);

            if(null != method) {
                T annotaion = method.getAnnotation(annotationClass);
                if(null != annotaion) {
                    String annotationValue = getAnnotationValue(annotaion);
                    description = StringUtils.isBlank(annotationValue) ? description : annotationValue;
                }
            }

        } catch (Exception e) {
            //ignore
        }

        return description;
    }

    protected <T extends Annotation> String  getFullMethodDescription(JoinPoint joinPoint, Class<T> annotionClass) {

        String methodName = joinPoint.getSignature().getName();
        String packages = joinPoint.getThis().getClass().getName();

        // 如果是CGLIB动态生成的类
        if (packages.indexOf("$$EnhancerByCGLIB$$") > -1
                || packages.indexOf("EnhancerBySpringCGLIB") > -1) {
            packages = packages.substring(0, packages.indexOf("$$"));
        }

        String methodDesc = getMethodDescription(joinPoint, annotionClass);

        return packages + ":" + methodName + (methodName.equals(methodDesc) ? "" : "--" + methodDesc);
    }

    public Class[] getParamTypes(Object[] params) {
        return Arrays.stream(params).map(Object::getClass).toArray(Class[]::new);
    }

    public <T extends Annotation> String getAnnotationValue(T annotation ) {
        return null == annotation ? null : "";
    }


}
