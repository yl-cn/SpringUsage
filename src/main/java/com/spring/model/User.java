package com.spring.model;

import com.fasterxml.jackson.annotation.JsonView;

public class User {
    //没有password字段的视图
    public interface WithoutPasswordView {};
    //有password字段的视图
    public interface WithPasswordView extends WithoutPasswordView {};

    private String username;

    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @JsonView(WithoutPasswordView.class)
    public String getUsername() {
        return this.username;
    }

    @JsonView(WithPasswordView.class)
    public String getPassword() {
        return this.password;
    }
}
