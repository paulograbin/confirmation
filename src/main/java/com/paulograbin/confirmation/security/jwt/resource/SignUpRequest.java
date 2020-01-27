package com.paulograbin.confirmation.security.jwt.resource;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


public class SignUpRequest {

    @Getter
    @Setter
    @NotBlank
    private String firstName;

    @Getter
    @Setter
    @NotBlank
    private String lastName;

    @Getter
    @Setter
    @NotBlank
    @Size(min = 6)
    private String password;

    @Getter
    @Setter
    @NotBlank
    private String username;

    @Getter
    @Setter
    @NotBlank
    @Email
    private String email;

}
