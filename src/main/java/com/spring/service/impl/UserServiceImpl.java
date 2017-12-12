package com.spring.service.impl;

import com.spring.model.User;
import com.spring.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

@Validated
@Service("userService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private ApplicationContext applicationContext;

    //@Resource(name="userService")
    private IUserService self;

    @PostConstruct
    private void init() {
        self = applicationContext.getBean(IUserService.class);
    }

    @Override
    public User findByName(String name) {
        return new User(name, "password");
    }
}
