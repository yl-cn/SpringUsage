package com.spring.logging;

import com.google.gson.Gson;
import com.spring.logging.annotation.ServiceLogging;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class ServiceLogInterceptor {

    ThreadLocal<Long> startTime =  new ThreadLocal<>();

    //@Pointcut("@annotation(com.spring.logging.annotation.ServiceLogging)")
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
        log.info("ServiceLogInterceptor.doAfterReturning()");

        log.info("耗时（毫秒） : {}", (System.currentTimeMillis()- startTime.get()));
    }


    @Around("serviceAspectPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        Object  result;
        try {

            result = joinPoint.proceed();

        } catch (Exception e) {
            log.error("\n异常方法: {} \n 异常代码:{} \n 异常信息:{} \n 参数:{}",
                    getMthodDescription(joinPoint),
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
        Gson gson = new Gson();
        if (params !=  null && params.length > 0) {
            for ( int i = 0; i < params.length; i++) {
                result.append(gson.toJson(params[i]) + ";");
            }
        }

        return result.toString();
    }

    /**
     * 获取注解中对方法的描述信息
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public static String getMthodDescription(ProceedingJoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();

        String packages = joinPoint.getThis().getClass().getName();

        // 如果是CGLIB动态生成的类
        if (packages.indexOf("$$EnhancerByCGLIB$$") > -1
                || packages.indexOf("EnhancerBySpringCGLIB") > -1) {
            packages = packages.substring(0, packages.indexOf("$$"));
        }

        //获得参数列表
        Object[] arguments = joinPoint.getArgs();

        if(arguments.length<=0){
            log.info("=== {} 方法没有参数", methodName);
        }else{
            for(int i=0;i<arguments.length;i++){
                log.info("==== 参数   {}  : {}", (i+1), arguments[i]);
            }
        }

        Class targetClass = Class.forName(targetName);
        Method[] method = targetClass.getMethods();
        String methodDesc = "";
        for (Method m : method) {
            if (m.getName().equals(methodName)) {
                Class[] tmpCs = m.getParameterTypes();
                if (tmpCs.length == arguments.length) {
                    ServiceLogging methodCache = m.getAnnotation(ServiceLogging.class);
                    if(null == methodCache) {
                        methodDesc = methodName;
                    }
                    else {
                        methodDesc = methodCache.moduleName();
                    }
                    break;
                }
            }
        }
        return packages + ":" + methodDesc;
    }
}
