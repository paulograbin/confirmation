package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;


class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    UserService userService;

    @Test
    void name() {

    }
}
