package com.paulograbin.confirmation.security.jwt;

import com.paulograbin.confirmation.security.jwt.resource.JwtTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class JwtTokenUtil implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_CREATED = "iat";
    private static final long serialVersionUID = -3301605591108950415L;

    @Value("${jwt.signing.key.secret}")
    private String secret;

    @Value("${jwt.token.expiration.in.seconds}")
    private Long expiration;


    public JwtTokenResponse generateToken(Authentication authentication) {
        return null;
    }

    public Long getUserIdFromJWT(String token) {
        return null;
    }

    public boolean validateToken(String authToken) {
        return true;
    }

}
