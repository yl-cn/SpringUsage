package com.spring.db;

import com.spring.SpringContextUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
public class MultiTransactional {
    private String[] transactionManagerNames;

    public String[] values() {
        transactionManagerNames = SpringContextUtil.getBeanNamesForType(PlatformTransactionManager.class);
        return this.transactionManagerNames;
    }
}
