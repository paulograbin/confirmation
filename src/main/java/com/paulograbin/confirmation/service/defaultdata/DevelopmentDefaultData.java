package com.paulograbin.confirmation.service.defaultdata;

import com.paulograbin.confirmation.chapter.ChapterService;
import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.service.RoleService;
import com.paulograbin.confirmation.service.UserService;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import com.paulograbin.confirmation.usecases.user.UpdateUserRequest;
import com.paulograbin.confirmation.user.User;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;


@Component
public class DevelopmentDefaultData {

    private static final Logger log = LoggerFactory.getLogger(DevelopmentApplicationReadyHelper.class);

    private static final String ADMIN_USERNAME = "plgrabin";
    private static final String ADMIN_EMAIL = "plgrabin@gmail.com";


    @Value("${default.pass}")
    private String defaultPass;

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private ChapterService chapterService;

    @Resource
    private EntityManager entityManager;


    @Transactional
    public void setDefaultRoles() {
        log.info("Checking for default roles...");

        if (roleService.fetchRoleCount() == 0) {
            log.info("Adding default roles to the system");

            Role admin = new Role(1L, RoleName.ROLE_ADMIN);
            Role master = new Role(2L, RoleName.ROLE_MC);
            Role user = new Role(3L, RoleName.ROLE_USER);

            entityManager.persist(admin);
            entityManager.persist(master);
            entityManager.persist(user);
        }
    }

    @Transactional
    public void setDefaultChapters() {
        log.info("Checking for default chapters...");

        if (chapterService.count() != 0) {
            return;
        }

        chapterService.createChapter(new ChapterCreationRequest(100L, "Capitulo AAAAAAAAA"));
        chapterService.createChapter(new ChapterCreationRequest(200L, "Capitulo BBBBBBBBB"));
        chapterService.createChapter(new ChapterCreationRequest(592L, "GVS"));
    }

    @Transactional
    public void setDefaultAdmin() {
        log.info("Checking default admin....");

        User defaultAdmin = null;
        try {
            defaultAdmin = (User) userService.loadUserByUsername(ADMIN_USERNAME);
            log.info("Admin found");
        } catch (UsernameNotFoundException e) {
            log.info("Admin not found, creating it...");
            defaultAdmin = new User(ADMIN_USERNAME, "Paulo", "Gräbin", ADMIN_EMAIL, "aaa");
            defaultAdmin = userService.createUser(defaultAdmin);

            userService.assignUserToChapter(defaultAdmin.getId(), 592L);
        }

        log.info("Setting admin password....");
        UpdateUserRequest defaultPasswordRequest = new UpdateUserRequest();
        defaultPasswordRequest.setId(defaultAdmin.getId());
        defaultPasswordRequest.setFirstName(defaultAdmin.getFirstName());
        defaultPasswordRequest.setLastName(defaultAdmin.getLastName());
        defaultPasswordRequest.setPassword(defaultPass);
        userService.updateUser(defaultAdmin.getId(), defaultPasswordRequest);

        userService.assignUserToChapter(defaultAdmin.getId(), 592L);

        log.info("Setting admin authorizations....");
        userService.setAsMaster(defaultAdmin.getId());
        userService.grantRoles(defaultAdmin.getId(), new HashSet<>(Collections.singletonList(roleService.getByName(RoleName.ROLE_ADMIN))));
    }
}
