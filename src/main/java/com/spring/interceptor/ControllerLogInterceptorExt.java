package com.spring.interceptor;

import com.spring.http.annotion.ObjectDesc;
import com.spring.http.config.RedirectConfig;
import com.spring.http.util.HttpUtilExt;
import com.spring.model.OperateLog;
import com.spring.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class ControllerLogInterceptorExt {

    /**
     * 定义日志切入点
     */
    //@Pointcut("@annotation(com.gznb.member.logging.annotation.ControllerLogging)")
    @Pointcut("execution(public * com.spring.controller.*.*.*(..)) ")
    public void controllerAspect() {
        //
    }

    @Around("controllerAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        Exception exception = null;
        OperateLog operateLog = generateLog(joinPoint);

        Instant startTime = Instant.now();

        try {

            result = joinPoint.proceed();
        } catch (Exception e) {

            log.error("\n异常方法: {} \n 异常代码:{} \n 异常信息:{} \n 参数:{}",
                    joinPoint.getTarget().getClass().getName(),
                    e.getClass().getName(),
                    getExceptionMessage(e),
                    joinPoint.getArgs());

            //抛出异常，交后续处理
            exception = e;
        }
        Long duration = Duration.between(startTime, Instant.now()).toMillis();
        log.info("**************耗时(毫秒): {}", duration);
        saveOperationLog(operateLog, Date.from(startTime), duration, exception);
        return result;
    }

    public void saveOperationLog(OperateLog operateLog, Date startTime, Long duration, Exception exception) throws Exception {
        if (null != operateLog) {
            RandomStringGenerator generator = new RandomStringGenerator.Builder()
                    .withinRange('0', '9').build();
            String randomNumbers = generator.generate(20);
            operateLog.setLogId(randomNumbers);
            operateLog.setOperateTime(startTime);
            operateLog.setDuration(duration.intValue());

            if(null == exception) {
                operateLog.setResult("S");
                operateLog.setMessage("成功");
            }
            else {
                operateLog.setResult("F");
                operateLog.setMessage(getExceptionMessage(exception));
            }

            try {
                LogManager.me().executeLog(LogTaskFactory.bussinessLog(operateLog));
            } catch (Exception e) {
                log.error("日志保存错误：{}", e.getMessage());
            }

        }

        if(null != exception) {
            throw exception;
        }

    }

    public OperateLog generateLog(ProceedingJoinPoint joinPoint) {
        OperateLog operateLog = null;

        //请求的IP
        String ip = HttpUtilExt.getIpAddress();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        try {


            //获取拦截的方法名
            Signature sig = joinPoint.getSignature();
            if (!(sig instanceof MethodSignature)) {
                throw new IllegalArgumentException("该注解只能用于方法");
            }
            MethodSignature msig = (MethodSignature) sig;
            Object target = joinPoint.getTarget();
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());

            Map<String, Object> params = getArgs(joinPoint, currentMethod);

            //
            String classDescription = "";
            ObjectDesc classDesc = target.getClass().getAnnotation(ObjectDesc.class);
            if(null != classDesc) {
                classDescription = StringUtils.isBlank(classDesc.moduleName()) ? classDesc.value() : classDesc.moduleName();
            }
            if(StringUtils.isBlank(classDescription)) {
                classDescription = joinPoint.getTarget().getClass().getSimpleName();
            }

            String methodDescription = "";
            ObjectDesc methodDesc = currentMethod.getAnnotation(ObjectDesc.class);
            if(null != methodDesc) {
                if(methodDesc.redirect()) {
                    methodDescription = getRedirectDesc(joinPoint.getArgs());
                }

                if(StringUtils.isBlank(methodDescription)){
                    methodDescription = StringUtils.isBlank(methodDesc.moduleName()) ? methodDesc.value() : methodDesc.moduleName();
                }
            }
            else {
                //没有定义Desc则不记录日志
                return null;
            }

            if(StringUtils.isBlank(methodDescription)) {
                methodDescription = methodName;
            }

            log.info("-----------------------{} > {}--------------------------------------", classDescription, methodDescription);
            log.info("方法描述: {}", methodDescription);
            log.info("请求方法: {}", (className + "." + methodName + "()"));

            log.info("请求IP: {} ", ip);

            String jsonParams;

            if(canToJson(joinPoint.getArgs())) {
                jsonParams = GsonUtil.toJson(params, false);
                log.info(GsonUtil.toJson(params, true));

            }
            else {
                jsonParams = "参数不能转换为json格式";
            }

            log.info("-----------------------{} > {}--------------------------------------", classDescription, methodDescription);

            operateLog = new OperateLog();
            operateLog.setOperation(classDescription + " > " + methodDescription);
            operateLog.setIp(ip);
            operateLog.setOperateMethod(className + "." + methodName);
            operateLog.setOperateParam(jsonParams);

        } catch (Exception e) {
            log.error("\n打印控制器信息错误:: 异常类: {} \n 异常方法:{} \n 异常信息:{} \n}",
                    joinPoint.getClass().getName(),
                    methodName,
                    e.getMessage());
        }
        return operateLog;
    }


    public Map<String, Object> getArgs(ProceedingJoinPoint joinPoint, Method currentMethod) {
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = currentMethod.getParameters();

        Map<String, Object> params = new HashMap<>();
        params.put("HttpServletRequest", HttpUtilExt.getRequestParameters());

        Map<String, Object> methodParams = new HashMap<>();
        for (int i = 0; i< args.length; i++) {
            Object arg = args[i];
            if (!(arg instanceof BindingResult)
                    && !(arg instanceof ModelMap)
                    && !(arg instanceof Model)
                    && !(arg instanceof HttpServletRequest)) {

                methodParams.put(parameters[i].getName(), arg);

            }
        }
        params.put(currentMethod.getName(), methodParams);

        return params;
    }

    public String getExceptionMessage(Exception e) {
        String message = e.getMessage();
        boolean isConstraintViolation = (e instanceof ConstraintViolationException);
        if(isConstraintViolation) {
            StringBuilder sb = new StringBuilder();
            String delimiter = ";";
            for (ConstraintViolation<?> violation : ((ConstraintViolationException)e).getConstraintViolations())
            {
                sb.append(violation.getMessage()).append(delimiter);
            }
            sb.delete(sb.length() - delimiter.length(), sb.length());
            message = sb.toString();
        }

        return message;
    }

    public boolean canToJson(Object[] objs) {
        boolean result = true;

        if(ArrayUtils.isNotEmpty(objs)) {
            for(Object obj : objs) {
                if(obj instanceof DataBinder || obj instanceof WebDataBinder) {
                    return false;
                }
            }
        }

        return result;
    }

    public String getRedirectDesc(Object[] args) {
        String description = null;
        if(ArrayUtils.isNotEmpty(args)) {
            try {
                //重定向参数必须是第一个参数
                description = RedirectConfig.redirectMap.get(args[0]).getDescription();
            }catch (Exception e) {
                log.error("获取重定向描述失败");
            }

        }
        return description;
    }
}