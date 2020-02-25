package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;


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
