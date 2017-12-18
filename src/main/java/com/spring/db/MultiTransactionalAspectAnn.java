package com.spring.db;

import com.spring.SpringContextUtil;
import com.spring.db.annotation.MultiTransactionalAnn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Stack;

/**
 *
 * @MultiTransactional(values = {"memberTransactionManager","activityTransactionManager"})
 */
@Aspect
@Slf4j
@Component
public class MultiTransactionalAspectAnn {

    @Pointcut("@annotation(com.spring.db.annotation.MultiTransactionalAnn)")
    public void transactionalAspectPointcut() {
        // Do nothing
    }


    @Around("transactionalAspectPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack = new Stack<>();
        Stack<TransactionStatus> transactionStatuStack = new Stack<>();

        try {
            String methodName = pjp.getSignature().getName();

            Class targetClass = pjp.getTarget().getClass();
            Class[] paramTypes = getParamTypes(pjp.getArgs());

            Method method = ReflectionUtils.findMethod(targetClass, methodName, paramTypes );
            MultiTransactionalAnn multiTransactional = method.getAnnotation(MultiTransactionalAnn.class);

            if (!openTransaction(dataSourceTransactionManagerStack,
                    transactionStatuStack, multiTransactional)) {
                return null;
            }

            Object ret = pjp.proceed();

            commit(dataSourceTransactionManagerStack, transactionStatuStack);

            return ret;
        } catch (Throwable e) {

            rollback(dataSourceTransactionManagerStack, transactionStatuStack);

            log.error(String.format(
                    "MultiTransactionalAspect, method:%s-%s occors error:", pjp
                            .getTarget().getClass().getSimpleName(), pjp
                            .getSignature().getName()), e);
            throw e;
        }
    }

    public Class[] getParamTypes(Object[] params) {
        return Arrays.stream(params).map(p -> p.getClass()).toArray(Class[]::new);
    }

    private boolean openTransaction(
            Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack,
            Stack<TransactionStatus> transactionStatuStack,
            MultiTransactionalAnn multiTransactional) {

        String[] transactionMangerNames = multiTransactional.values();
        if (ArrayUtils.isEmpty(multiTransactional.values())) {
            return false;
        }

        for (String beanName : transactionMangerNames) {
            DataSourceTransactionManager dataSourceTransactionManager = (DataSourceTransactionManager) SpringContextUtil.getBean(beanName);
            TransactionStatus transactionStatus = dataSourceTransactionManager
                    .getTransaction(new DefaultTransactionDefinition());
            transactionStatuStack.push(transactionStatus);
            dataSourceTransactionManagerStack
                    .push(dataSourceTransactionManager);
        }
        return true;
    }

    private void commit(
            Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack,
            Stack<TransactionStatus> transactionStatuStack) {

        while (!dataSourceTransactionManagerStack.isEmpty()) {
            dataSourceTransactionManagerStack.pop().commit(
                    transactionStatuStack.pop());
        }

    }

    private void rollback(
            Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack,
            Stack<TransactionStatus> transactionStatuStack) {

        while (!dataSourceTransactionManagerStack.isEmpty()) {
            dataSourceTransactionManagerStack.pop().rollback(
                    transactionStatuStack.pop());
        }

    }

}
