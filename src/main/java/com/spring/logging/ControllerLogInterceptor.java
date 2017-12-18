package com.spring.logging;

import com.spring.logging.annotation.ControllerLogging;
import com.spring.util.GsonUtil;
import com.spring.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class ControllerLogInterceptor extends BaseInterceptor{



    /**
     * 定义日志切入点
     */
    //@Pointcut("@annotation(com.gznb.member.logging.annotation.ControllerLogging)")
    @Pointcut("execution(public * com.spring.service.*.*(..)) ")
    public void controllerAspect() {
        //
    }

    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //HttpSession session = request.getSession();

        //请求的IP
        String ip = HttpUtil.getIpAddress(request);
        try {
            String methodDescription = getMethodDescription(joinPoint, ControllerLogging.class);
            log.info("-----------------------{}--------------------------------------", methodDescription );
            log.info("方法描述: {}", methodDescription );
            log.info("请求方法: {}", (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
            log.info("请求IP:   {}", ip);

            Object[] args = joinPoint.getArgs();
            Map<String, Object> params = new HashMap<>();
            for (Object arg : args) {
                if (!(arg instanceof BindingResult) && !(arg instanceof ModelMap) && !(arg instanceof Model)) {

                    if (arg instanceof HttpServletRequest) {
                        Map<String, String> requestParamsMap = HttpUtil.getHttpRequestParams((HttpServletRequest)arg);

                        if(!CollectionUtils.isEmpty(requestParamsMap)) {
                            params.put("HttpServletRequest", requestParamsMap);
                        }
                    } else {
                        params.put(arg.getClass().getSimpleName(),arg);
                    }
                }
            }
            log.info("请求参数:\n {}", GsonUtil.toJson(params, true));

            log.info("-----------------------{}--------------------------------------", methodDescription);
        } catch (Exception e) {
            log.error("\n打印控制器信息错误:: 异常类: {} \n 异常方法:{} \n 异常信息:{} \n}",
                    joinPoint.getClass().getName(),
                    joinPoint.getSignature().getName(),
                    e.getMessage());
        }
    }

    /**
     * 后置通知
     *
     * @param joinPoint 切点
     */
    @After("controllerAspect()")
    public void doAfter(JoinPoint joinPoint) {
/*        try {

        } catch (Exception e) {
            log.error("异常信息:{}", e.getMessage());
        }*/
    }

    /**
     * 异常通知
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "controllerAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
/*        try {

        } catch (Exception ex) {
            log.error("异常信息:{}", ex.getMessage());
        }*/

        log.error("异常方法:{}异常代码:{}异常信息:{}参数:{}",
                joinPoint.getTarget().getClass().getName() + joinPoint.getSignature().getName(),
                e.getClass().getName(), e.getMessage());
    }

    @Override
    public <T extends Annotation> String getAnnotationValue(T annotation) {

        if(null != annotation && annotation instanceof ControllerLogging) {
            ControllerLogging controllerLogging = (ControllerLogging)annotation;
            return controllerLogging.moduleName();
        }
        return null;
    }

}