package com.spring.logging;

import com.spring.logging.annotation.ControllerLogging;
import com.spring.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class ControllerLogInterceptor {
    /**
     * 定义日志切入点
     */
    //@Pointcut("@annotation(com.gznb.member.logging.annotation.ControllerLogging)")
    @Pointcut("execution(public * com.spring.controller.*.*(..)) ")
    public void controllerAspect() {
    }

    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();

        //请求的IP
        String ip = request.getRemoteAddr();
        try {
            String methodDescription = getControllerMethodDescription(joinPoint);
            log.info("-----------------------{}--------------------------------------", methodDescription );
            log.info("方法描述: {}", methodDescription );
            log.info("请求方法: {}", (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));

            log.info("请求IP: {} ", ip);

            Object[] args = joinPoint.getArgs();
            Map<String, Object> params = new HashMap<>();
            for (Object arg : args) {
                if (!(arg instanceof BindingResult) && !(arg instanceof ModelMap) && !(arg instanceof Model)) {
                    if (arg instanceof HttpServletRequest) {
                        HttpServletRequest httpRequest = (HttpServletRequest) arg;
                        Enumeration<?> enume = httpRequest.getParameterNames();
                        if (null != enume) {
                            Map<String, String> hpMap = new HashMap<>();
                            while (enume.hasMoreElements()) {
                                Object element = enume.nextElement();
                                if (null != element) {
                                    String paramName = (String) element;
                                    String paramValue = httpRequest.getParameter(paramName);
                                    hpMap.put(paramName, paramValue);
                                }
                            }
                            params.put("HttpServletRequest", hpMap);
                        }
                    } else {
                        try {
                            params.put(arg.getClass().getSimpleName(), GsonUtil.toJson(arg, false));
                        } catch (Throwable e) {
                            log.warn("不能转换为Json字符串:" + arg.getClass().getName());
                        }
                    }
                }
            }
            log.info(GsonUtil.toJson(params, true));

            log.info("-----------------------{}--------------------------------------", methodDescription);
        } catch (Exception e) {
            log.error("异常信息:{}", e.getMessage());
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


    /**
     * 获取注解中对方法的描述信息
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    private static String getControllerMethodDescription(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    ControllerLogging controllerLogging = method.getAnnotation(ControllerLogging.class);
                    if(null == controllerLogging) {
                        description = methodName;
                    }
                    else {
                        description = controllerLogging.moduleName();
                    }

                    break;
                }
            }
        }
        return description;
    }

}