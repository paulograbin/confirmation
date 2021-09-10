package com.paulograbin.confirmation.featureflag;

import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.user.User;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.BooleanUtils.isTrue;


@CrossOrigin("*")
@RestController
@RequestMapping(value = "/feature")
public class FeatureController {

    private static final Logger log = LoggerFactory.getLogger(FeatureController.class);

    private final Map<String, Boolean> featureMap = new HashMap<>();

    public FeatureController() {
        featureMap.put("RESET_PASSWORD_BUTTON", Boolean.TRUE);
        featureMap.put("ENABLE_RESET_PASSWORD", Boolean.TRUE);
    }

    @GetMapping(path = "/{featureKey}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> fetch(@PathVariable String featureKey) {
        Boolean aBoolean = featureMap.computeIfAbsent(featureKey, s -> {
            log.info("Key {} not found", featureKey);
            return false;
        });

        log.info("Looking feature enablement with key {}, status {}", featureKey, aBoolean);

        return ResponseEntity.ok(aBoolean);
    }

    @PostMapping(path = "/{featureKey}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> setValue(@CurrentUser User currentUser,  @PathVariable String featureKey) {
        log.info("Enabling feature {}", featureKey);

        if (!currentUser.getEmail().equals("plgrabin@gmail.com")) {
            ResponseEntity.badRequest();
        }

        Boolean aBoolean = featureMap.computeIfPresent(featureKey, (s, aBoolean1) -> !aBoolean1);

        return ResponseEntity.ok(isTrue(aBoolean));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, Boolean>> fetchAll() {
        log.info("Looking for all feature enablement");

        return ResponseEntity.ok(featureMap);
    }
}
