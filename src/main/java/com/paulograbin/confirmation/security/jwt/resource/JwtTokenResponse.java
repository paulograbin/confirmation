package com.paulograbin.confirmation.security.jwt.resource;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public class JwtTokenResponse implements Serializable {

    private static final long serialVersionUID = 8317676219297719109L;

    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    private String tokenType = "Bearer";

    public JwtTokenResponse(String token) {
        this.token = token;
    }
}
