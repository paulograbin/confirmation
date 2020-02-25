package com.paulograbin.confirmation.security.jwt.resource;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


public class LoginRequest {

    @NotBlank
    @Getter
    @Setter
    private String usernameOrEmail;

    @NotBlank
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
