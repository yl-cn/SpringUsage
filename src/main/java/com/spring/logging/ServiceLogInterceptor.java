package com.spring.logging;

import com.spring.logging.annotation.ServiceLogging;
import com.spring.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ServiceLogInterceptor extends BaseInterceptor{

    ThreadLocal<Long> startTime =  new ThreadLocal<>();

    //@Pointcut("@annotation(com.gznb.member.logging.annotation.ServiceLogging)")
    @Pointcut("execution(public * com.spring.service.*.*.*(..)) ")
    public void serviceAspectPointcut() {
        // Do nothing
    }

    @Before("serviceAspectPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
    }

    @AfterReturning("serviceAspectPointcut()")
    public void doAfterReturning(JoinPoint joinPoint){
        log.info("{} 耗时（毫秒） : {}", getFullMethodDescription(joinPoint, ServiceLogging.class),
                (System.currentTimeMillis()- startTime.get()));
    }


    @Around("serviceAspectPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        Object  result;
        try {

            result = joinPoint.proceed();

        } catch (Exception e) {
            log.error("\n异常方法: {} \n异常代码:{} \n异常信息:{} \n参数:{}",
                    getFullMethodDescription(joinPoint, ServiceLogging.class),
                    e.getClass().getName(),
                    e.getMessage(),
                    paramsToStr(joinPoint.getArgs()));

            //抛出异常，交后续处理
            throw e;
        }

        return result;
    }

    /**
     * 将对象参数转成字符串
     * @param params  对象参数
     * @return
     */
    public String paramsToStr(Object[] params) {
        StringBuilder result = new StringBuilder();

        if (!ArrayUtils.isEmpty(params)) {
            Arrays.stream(params).forEach(p -> result.append("\n" + GsonUtil.toJson(p,true)));
        }

        return result.toString();
    }

    @Override
    public <T extends Annotation> String getAnnotationValue(T annotation) {

        if(null != annotation && annotation instanceof ServiceLogging) {
            ServiceLogging serviceLogging = (ServiceLogging)annotation;
            return serviceLogging.moduleName();
        }
        return null;
    }

}
