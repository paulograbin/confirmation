package com.paulograbin.confirmation;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterService;
import com.paulograbin.confirmation.service.UserService;
import com.paulograbin.confirmation.user.User;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.module.jsr310.Jsr310Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

@SpringBootApplication
@EnableCaching
public class DemoApplication {

    // TODO Figure out a way to obtain the token in the controller test calls
    // TODO Validations on post requests
    // TODO almoço

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Resource
    private UserService userService;

    @Resource
    private ChapterService chapterService;

    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.registerModule(new Jsr310Module());

        return modelMapper;
    }

    private void setDefaultUsers() {
        log.info("Checking users...");

        Chapter chapter_gvs = chapterService.fetchById(592L);

        User mc2 = new User("asimov", "Isaac", "Asimov", "isaac@asimov.com", "aaa");
        createUserIfDoesntExist(mc2, chapter_gvs, false);


//        Chapter chapter200 = chapterService.fetchById(200L);
//        User master_2 = new User("master2", "Mestre 2", "Sobrenome @", "mestre@100.com", "aaa");
//        createUserIfDoesntExist(master_2, chapter200, true);
//
//        User not_master_2 = new User("notmaster2", "Not Mestre 2", "Not Sobrenome @", "not_mestre@100.com", "aaa");
//        createUserIfDoesntExist(not_master_2, chapter200, false);
    }

    private void createUserIfDoesntExist(User newUser, Chapter gvs, boolean setAsMaster) {
        try {
            userService.createUser(newUser);
            userService.assignUserToChapter(newUser.getId(), gvs.getId());

            if (setAsMaster) {
                log.info("Setting {} as master", newUser.getUsername());
                userService.setAsMaster(newUser.getId());
            }
        } catch (RuntimeException e) {
            log.info("User {} already exists, no need to create it again", newUser.getId());
        }
    }
}
