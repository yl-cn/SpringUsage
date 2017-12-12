package com.spring.interceptor;

import com.spring.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

@Slf4j
public class LogTaskFactory {

    //private static IOperateLogService operateLogService     = SpringContextUtil.getBeanByClass(IOperateLogService.class);

    public static TimerTask bussinessLog(final Object logInfo) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    //operateLogService.insert(logInfo);
                } catch (Exception e) {
                    log.error("创建业务日志异常!", e);
                }
            }
        };
    }

}
