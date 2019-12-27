package com.paulograbin.confirmation.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@CrossOrigin("*")
@RestController
@RequestMapping(path = "/login")
public class LoginController {

//    @Resource
//    ProviderManager authenticationManager;

//    @PostMapping
//    public Authentication login(@RequestBody String username, @RequestBody String password) {
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        boolean isAuthenticated = isAuthenticated(authentication);
//        if (isAuthenticated) {
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        return authentication;
//    }
//
//    private boolean isAuthenticated(Authentication authentication) {
//        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
//    }

}
