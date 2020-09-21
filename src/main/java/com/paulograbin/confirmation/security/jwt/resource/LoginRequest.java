package com.paulograbin.confirmation.security.jwt.resource;

import lombok.Getter;
import lombok.Setter;



public class LoginRequest {

    @Getter
    @Setter
    private String usernameOrEmail;

    @Getter
    @Setter
    private String password;

    @Override
    public String toString() {
        return "LoginRequest{" +
                "usernameOrEmail='" + usernameOrEmail + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
