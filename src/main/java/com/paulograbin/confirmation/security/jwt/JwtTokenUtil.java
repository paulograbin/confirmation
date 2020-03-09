package com.paulograbin.confirmation.security.jwt;

import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.security.jwt.resource.JwtTokenResponse;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

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
        User principal = (User) authentication.getPrincipal();

        Date now = new Date();
        Date expirationDate = calculateExpirationDate(now);

        String jwtToken = Jwts.builder()
                .setSubject(Long.toString(principal.getId()))
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        return new JwtTokenResponse(jwtToken, expirationDate);
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);

            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }

        return false;
    }

    private Date calculateExpirationDate(Date createdDate) {
//        return new Date(createdDate.getTime() + expiration * 1000);

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH, 30);

        return instance.getTime();
    }
}
