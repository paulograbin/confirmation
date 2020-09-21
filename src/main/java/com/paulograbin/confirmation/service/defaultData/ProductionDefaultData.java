package com.paulograbin.confirmation.service.defaultData;

import com.paulograbin.confirmation.DemoApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Profile("production")
@Service
public class ProductionDefaultData implements DefaultData, CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    @Override
    public void run(String... args) {
        log.info("Running production default data");
    }
}
