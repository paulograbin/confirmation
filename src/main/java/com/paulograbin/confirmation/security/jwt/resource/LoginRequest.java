package com.paulograbin.confirmation.security.jwt.resource;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


public class LoginRequest {

    @NotBlank
    @Getter
    @Setter
    private String usernameOrEmail;

    @NotBlank
    @Getter
    @Setter
    private String password;

}
