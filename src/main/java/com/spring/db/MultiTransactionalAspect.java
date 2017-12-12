package com.spring.db;

import com.spring.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Stack;

@Aspect
@Slf4j
@Component
public class MultiTransactionalAspect {

    @Autowired
    private MultiTransactional multiTransactional;

    @Pointcut("execution(public * com.spring.service.*.*.*(..)) ")
    public void transactionalAspectPointcut() {
        // Do nothing
    }


    @Around("transactionalAspectPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack = new Stack<>();
        Stack<TransactionStatus> transactionStatuStack = new Stack<>();

        try {

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

    private boolean openTransaction(
            Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack,
            Stack<TransactionStatus> transactionStatuStack,
            MultiTransactional multiTransactional) {

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

