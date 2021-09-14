package com.paulograbin.confirmation.user;

import com.paulograbin.confirmation.DemoApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class AuthenticationHandler {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    private static final Map<String, Integer> MAP = new ConcurrentHashMap<>();

    static {
        MAP.put("success", 0);
        MAP.put("failure", 0);
    }


    @EventListener(AuthenticationSuccessEvent.class)
    public void handleAuthentication(AuthenticationSuccessEvent event) {
        log.debug("authenticationSuccess - event: {}", event);

        MAP.put("success", MAP.compute("success", (key, value) -> ++value));

//        template.broadcast("users", SseEmitter.event()
//                .name("success")
//                .data(Map.of("current", event.getAuthentication().getName(),
//                        "total", MAP)));
    }

    @EventListener
    public void authenticationFailed(AbstractAuthenticationFailureEvent event) {
        log.debug("authenticationFailed - event: {}", event);

        MAP.put("failure", MAP.compute("failure", (key, value) -> ++value));

//        template.broadcast("users", SseEmitter.event()
//                .name("failure")
//                .data(Map.of("current", event.getAuthentication().getName(),
//                        "total", MAP)));
    }

}
