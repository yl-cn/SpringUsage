package com.spring.service;

import com.spring.model.User;
import org.hibernate.validator.constraints.NotBlank;

public interface IUserService {

    User findByName(@NotBlank String name);
}
