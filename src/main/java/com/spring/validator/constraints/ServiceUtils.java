package com.spring.validator.constraints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServiceUtils {
    private static ServiceUtils instance;

    @Autowired
    private DataValidator dataValidator;

    /* Post constructor */
    @PostConstruct
    public void fillInstance() {
        instance = this;
    }

    /*static methods */
    public static DataValidator getDataValidator(){
        return instance.dataValidator;
    }
}
