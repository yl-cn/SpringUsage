package com.spring.interceptor;

import com.spring.data.MessageCode;
import com.spring.data.WebServiceResult;
import com.spring.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统异常统一处理，包装自定义全局错误消息
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 全局错误处理
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String defaultErrorHandler(HttpServletRequest request, Exception ex) {
        ex.printStackTrace();

        return WebServiceResult.builder()
                .code(MessageCode.RESPONSE_MESSAGE_SERVER_ERROR)
                .message("Server Failed").build().toJson();
    }

    /**
     * 业务处理异常响应
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public String serviceExceptionHandler(HttpServletRequest request, ServiceException ex) {

        return WebServiceResult.builder()
                .code(MessageCode.RESPONSE_MESSAGE_SERVICE_ERROR)
                .message(ex.getMessage()).build().toJson();
    }

    /**
     * 参数验证异常响应
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public String constraintViolationExceptionHandler(HttpServletRequest request, ConstraintViolationException ex) {
        Map<String, Object> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations())
        {
            log.error("参数验证失败： 传入参数（{}）值 ：{} ", violation.getPropertyPath(), violation.getMessage());
            LinkedList<String> list = Arrays.stream(violation.getPropertyPath().toString().split("\\."))
                    .collect(Collectors.toCollection(LinkedList::new));
            errors.put(list.getLast(),violation.getMessage());
        }

        return WebServiceResult.builder()
                .code(MessageCode.RESPONSE_MESSAGE_VALIDATE_ERROR)
                .message("参数验证失败").data(errors).build().toJson();
    }

    /**
     * 参数验证异常响应
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public String bindExceptionHandler(HttpServletRequest request, BindException ex) {
        BindingResult result = ex.getBindingResult();

        Map<String, Object> errors = new HashMap<>();

        if (result.hasErrors()) {
            List<FieldError> lsFieldError = result.getFieldErrors();
            for (FieldError fieldError:lsFieldError) {
                log.error("参数验证失败： 传入参数（{}）值 ：{} ", fieldError.getField(), fieldError.getRejectedValue());

                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        }

        return WebServiceResult.builder()
                .code(MessageCode.RESPONSE_MESSAGE_VALIDATE_ERROR)
                .message("参数验证失败").data(errors).build().toJson();
    }
}
