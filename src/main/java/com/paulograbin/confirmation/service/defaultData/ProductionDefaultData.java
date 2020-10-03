package com.paulograbin.confirmation.service.defaultData;

import com.paulograbin.confirmation.DemoApplication;
import com.paulograbin.confirmation.userequest.UserRequest;
import com.paulograbin.confirmation.userequest.UserRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Profile("production")
@Service
public class ProductionDefaultData implements DefaultData, CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    @Resource
    UserRequestRepository repository;

    @Override
    public void run(String... args) {
        log.info("Running production default data");

        updateUserRequestToIncludeId();
    }

    private void updateUserRequestToIncludeId() {
        log.info("Setting for those user requests that don't have");

        Iterable<UserRequest> all = repository.findAll();

        long id = 1;

        for (UserRequest userRequest : all) {
            log.info("Working on user request {}", userRequest.getEmail());

            if (userRequest.getId2() == null) {
                userRequest.setId2(id);
                id++;

                repository.save(userRequest);
            }
        }
    }
}
