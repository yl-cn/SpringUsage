package com.spring.interceptor;

import com.spring.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ControllerInterceptor implements MethodInterceptor {

    @Override
    public String invoke(MethodInvocation invocation) {
        String result = "";
        String paramsStr = "";
        Object value = null;
        Method md = invocation.getMethod();
        try {
            Object[] args = invocation.getArguments();
            paramsStr = this.logRequest(args);
            value = invocation.proceed();
        } catch (Throwable e) {
            if (e instanceof Exception) {
                log.error("", md
                        .getDeclaringClass().getSimpleName()
                        + "."
                        + md.getName()
                        + " || "
                        + paramsStr
                        + " || "
                        + printStackTraceToString(e));
            } else {
                log.error("some_alarm_id", md
                        .getDeclaringClass().getSimpleName()
                        + "."
                        + md.getName()
                        + " || "
                        + paramsStr
                        + " || "
                        + printStackTraceToString(e));
            }
        }
        if (value != null) {
            result = value.toString();
        } else {
            result = "error";
        }
        this.logRequestResponse(md, paramsStr, result);
        return result;
    }
    private String logRequest(Object[] args) {
        if (args == null) {
            return "";
        }
        // 请求参数日志信息
        Map<String, Object> params = new HashMap<>();
        int i = 1;
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
                        params.put("arg" + i, GsonUtil.toJson(arg, false));
                        i++;
                    } catch (Throwable e) {
                        log.warn("CANNOT trasform to json string:"
                                + arg.getClass().getName());
                    }
                }
            }
        }
        String paramsStr = GsonUtil.toJson(params, false);
        return paramsStr;
    }
    private void logRequestResponse(Method md, String paramsStr, String re) {
        Map<String, String> logMap = new HashMap<>();
        logMap.put("controller.method", md.getDeclaringClass().getSimpleName() + "." + md.getName());
        logMap.put("logReq", paramsStr);
        logMap.put("logRes", re);
        log.info(logMap.toString());
    }
    private String printStackTraceToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString().replace("\n", " ").replace("\r", " ").replace("\t", " ");
    }
}