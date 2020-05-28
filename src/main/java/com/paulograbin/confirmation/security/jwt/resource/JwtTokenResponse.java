package com.paulograbin.confirmation.security.jwt.resource;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


public class JwtTokenResponse implements Serializable {

    private static final long serialVersionUID = 8317676219297719109L;

    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    private Date expirationDate;

    @Getter
    @Setter
    private String tokenType = "Bearer";


    public JwtTokenResponse() {
    }

    public JwtTokenResponse(String token, Date expirationDate) {
        this.token = token;
        this.expirationDate = expirationDate;
    }

//    public JwtTokenResponse(String token, Date expirationDate, boolean master) {
//        this.token = token;
//        this.expirationDate = expirationDate;
//        this.master = master;
//    }

    public JwtTokenResponse(String token) {
        this.token = token;
    }
}
