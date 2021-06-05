package com.paulograbin.confirmation.featureflag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@CrossOrigin("*")
@RestController
@RequestMapping(value = "/feature")
public class FeatureController {

    private static final Logger log = LoggerFactory.getLogger(FeatureController.class);

    private final Map<String, Boolean> featureMap = new HashMap<>();

    public FeatureController() {
//        featureMap.put("RESET_PASSWORD_BUTTON", Boolean.TRUE);
        featureMap.put("RESET_PASSWORD_BUTTON", Boolean.TRUE);
        featureMap.put("ENABLE_RESET_PASSWORD", Boolean.TRUE);
        featureMap.put("TESTE_A", Boolean.FALSE);
        featureMap.put("TESTE_B", Boolean.TRUE);
        featureMap.put("TESTE_C", Boolean.TRUE);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> fetch(@PathVariable String id) {
        Boolean aBoolean = featureMap.computeIfAbsent(id, s -> {
            log.info("Key {} not found", id);
            return false;
        });

        log.info("Looking feature enablement with key {}, status {}", id, aBoolean);

        return ResponseEntity.ok(aBoolean);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, Boolean>> fetchAll() {
        log.info("Looking for all feature enablement");

        return ResponseEntity.ok(featureMap);
    }
}
