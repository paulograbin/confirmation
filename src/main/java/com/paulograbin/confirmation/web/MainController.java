package com.paulograbin.confirmation.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @GetMapping
    public List<String> home(HttpServletRequest request) {
        return EchoController.logRequestDetails(log, request);
    }
}
