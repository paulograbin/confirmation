package com.paulograbin.confirmation.security.jwt.resource;

import lombok.Getter;
import lombok.Setter;


public class SignUpRequest {

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String email;

}
