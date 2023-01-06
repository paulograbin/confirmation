package com.paulograbin.confirmation.service.defaultdata;

import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.service.RoleService;
import com.paulograbin.confirmation.service.UserService;
import com.paulograbin.confirmation.service.mail.EmailService;
import com.paulograbin.confirmation.usecases.user.UpdateUserRequest;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.user.UserRepository;
import com.paulograbin.confirmation.userequest.UserRequestRepository;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;


@Profile("production")
@Service
public class ProductionDefaultData implements DefaultData {

    private static final Logger log = LoggerFactory.getLogger(ProductionDefaultData.class);

    private static final String ADMIN_USERNAME = "plgrabin";
    private static final String ADMIN_EMAIL = "plgrabin@gmail.com";

    @Value("${default.pass}")
    private String defaultPass;

    @Resource
    private UserService userService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserRequestRepository userRequestRepository;

    @Resource
    private ChapterRepository chapterRepository;

    @Resource
    private EmailService emailService;

    @Resource
    private RoleService roleService;


    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        log.info("Running production default data");

        setDefaultRoles();
        setDefaultAdmin();
//        setUserRequests();
    }

    private void setDefaultRoles() {
        log.info("Checking for default roles...");

        if (roleService.fetchRoleCount() == 0) {
            log.info("Adding default roles to the system");

            Role admin = new Role(RoleName.ROLE_ADMIN);
            Role master = new Role(RoleName.ROLE_MC);
            Role user = new Role(RoleName.ROLE_USER);

            roleService.save(admin);
            roleService.save(master);
            roleService.save(user);
        }
    }

    @Getter
    @Setter
    class NamePair {
        String firstName;
        String lastName;

        public NamePair(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    private void setUserRequests() {
        HashMap<String, NamePair> users = new HashMap<>();
//        users.put("xxx@xxx.com", new NamePair("xxx", "xxx"));

//        users.forEach((email, namePair) -> {
//            CreatePseudoUserRequest request = new CreatePseudoUserRequest();
//
//            request.requestingUser = 1;
//            request.email = email;
//            request.firstName = namePair.firstName;
//            request.lastName = namePair.lastName;
//            request.chapterId = 592;
//
//            new CreatePseudoUserUseCase(request, userRepository, userRequestRepository, chapterRepository, emailService).execute();
//        });
    }

    private void setDefaultAdmin() {
        log.info("Checking default admin....");

        User defaultAdmin = null;
        try {
            defaultAdmin = (User) userService.loadUserByUsername(ADMIN_USERNAME);
            log.info("Admin found");
        } catch (UsernameNotFoundException e) {
            log.info("Admin not found, creating it...");
            defaultAdmin = new User(ADMIN_USERNAME, "Paulo", "Gräbin", ADMIN_EMAIL, "aaa");
            defaultAdmin = userService.createUser(defaultAdmin);

            userService.assignUserToChapter(defaultAdmin.getId(), 3L);
        }

        log.info("Setting admin password....");
        UpdateUserRequest defaultPasswordRequest = new UpdateUserRequest();
        defaultPasswordRequest.setId(defaultAdmin.getId());
        defaultPasswordRequest.setFirstName(defaultAdmin.getFirstName());
        defaultPasswordRequest.setLastName(defaultAdmin.getLastName());

        defaultPasswordRequest.setPassword(defaultPass);
        userService.updateUser(defaultAdmin.getId(), defaultPasswordRequest);

        userService.assignUserToChapter(defaultAdmin.getId(), 3L);

        log.info("Setting admin authorizations....");
        userService.setAsMaster(defaultAdmin.getId());
        userService.grantRoles(defaultAdmin.getId(), new HashSet<>(Collections.singletonList(roleService.getByName(RoleName.ROLE_ADMIN))));
    }
}
