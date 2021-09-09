package com.paulograbin.confirmation.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import static com.paulograbin.confirmation.DateUtils.parseDateToString;


@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @GetMapping
    public List<String> home(HttpServletRequest request) {
        return logRequestDetails(log, request);
    }

    public static List<String> logRequestDetails(Logger log, HttpServletRequest request) {
        log.info("******");
        List<String> a = new ArrayList<>();

        var top = String.format("%s %s %s", request.getMethod(), request.getRequestURI(), request.getProtocol());
        a.add(top);
        log.info("Format {}", top);

        var connection = String.format("Connection: %s", request.getHeader("connection"));
        a.add(connection);
        log.info(connection);

        var host = String.format("Host: %s", request.getHeader("connection"));
        a.add(host);
        log.info(host);

        var userAgent = String.format("User-agent: %s", request.getHeader("user-agent"));
        a.add(userAgent);
        log.info(userAgent);

        var date = parseDateToString(LocalDateTime.now());
        log.info("Date {}", date);
        a.add("Date: " + date);

        var remoteHost = String.format("Remote host %s", request.getRemoteHost());
        log.info(remoteHost);
        a.add(remoteHost);

        var contentLength = String.format("Content Length %s", request.getContentLength());
        log.info(contentLength);
        a.add(contentLength);

        var requestUrl = String.format("Request URL %s", request.getRequestURL());
        log.info(requestUrl);
        a.add(requestUrl);

        var serverPort = String.format("Server port %s", request.getServerPort());
        log.info(serverPort);
        a.add(serverPort);

        var serverName = String.format("Server name %s", request.getServerName());
        log.info(serverName);
        a.add(serverName);

        Enumeration<String> headerNames = request.getHeaderNames();
        Iterator<String> stringIterator = headerNames.asIterator();

        while (stringIterator.hasNext()) {
            var next = stringIterator.next();
            var headerValue = request.getHeader(next);

            var headerString = String.format("Header %s: %s", next, headerValue);
            if (!next.equalsIgnoreCase("authorization")) {
                log.info("Header {}: {}", next, headerValue);
                a.add(headerString);
            }
        }

        return a;
    }
}
