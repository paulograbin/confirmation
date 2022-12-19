package com.paulograbin.confirmation.security.jwt.resource;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    private String usernameOrEmail;
    private String password;

    @Override
    public String toString() {
        return "LoginRequest{" +
                "usernameOrEmail='" + usernameOrEmail + '\'' + '}';
    }
}
