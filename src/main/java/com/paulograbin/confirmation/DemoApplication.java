package com.paulograbin.confirmation;

import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.ParticipationRepository;
import com.paulograbin.confirmation.service.EventService;
import com.paulograbin.confirmation.service.ParticipationService;
import com.paulograbin.confirmation.service.RoleService;
import com.paulograbin.confirmation.service.UserService;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.module.jsr310.Jsr310Module;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TimeZone;

import static java.time.format.DateTimeFormatter.ofPattern;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    // TODO Figure out a way to obtain the token in the controller test calls
    // TODO Identify if user has permission to invite and confirm users
    // TODO Validations on post requests
    // TODO Test everything
    // TODO Exception when user is invited more than once to a event


    public static final String DEFAULT_ADDRESS_JOAO_CORREA = "Avenida João Corrêa, 815";

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC-3"));
    }

    @Resource
    private ParticipationRepository participationRepository;

    @Resource
    private UserService userService;

    @Resource
    private EventService eventService;

    @Resource
    private ParticipationService participationService;

    @Resource
    private RoleService roleService;

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }


    @Override
    public void run(String... args) throws Exception {
        if (participationRepository.count() == 0) {

            Role admin = new Role();
            admin.setName(RoleName.ROLE_ADMIN);
            Role master = new Role();
            master.setName(RoleName.ROLE_MC);
            Role user = new Role();
            user.setName(RoleName.ROLE_USER);

            admin = roleService.save(admin);
            master = roleService.save(master);
            user = roleService.save(user);

            User mc1 = new User("plgrabin", "Mestre", "Conselheiro", "plgrabin", "aaa");
            User mc2 = new User("asimov", "Isaac", "Asimov", "asimov", "aaa");
            User mc3 = new User("primeiroconselheiro", "Primeiro", "Conselheiro", "primeiroconselheiro", "aaa");

            mc1 = userService.createUser(mc1);
            userService.setAsMaster(mc1.getId());
            userService.grantRoles(mc1.getId(), Set.of(admin));
            mc2 = userService.createUser(mc2);
            mc3 = userService.createUser(mc3);

            Event e01 = new Event("Mais antigo", DEFAULT_ADDRESS_JOAO_CORREA, "Evento mais velho", mc1, LocalDateTime.of(2020, 01, 1, 14, 0, 0));
            Event e02 = new Event("Proximo", DEFAULT_ADDRESS_JOAO_CORREA, "Proximo", mc1, LocalDateTime.of(2020, 01, 20, 14, 0, 0));
            Event e03 = new Event("Futuro", DEFAULT_ADDRESS_JOAO_CORREA, "Evento do futuro", mc1, LocalDateTime.of(2020, 02, 1, 14, 0, 0));

            e01 = eventService.createEvent(e01, mc1);
            e02 = eventService.createEvent(e02, mc1);
            e03 = eventService.createEvent(e03, mc1);

            if (e01.getParticipants().isEmpty()) {
                throw new UnsupportedOperationException();
            }
        }

        checkDefaultAdminIsPresent();
    }

    private void checkDefaultAdminIsPresent() {
        User defaultAdmin = null;
        try {
            defaultAdmin = (User) userService.loadUserByUsername("plgrabin");
        } catch (UsernameNotFoundException e) {
            defaultAdmin = new User("plgrabin", "Mestre", "Conselheiro", "plgrabin", "aaa");
            defaultAdmin = userService.createUser(defaultAdmin);
        }

        userService.setAsMaster(defaultAdmin.getId());
        userService.grantRoles(defaultAdmin.getId(), Set.of(roleService.getByName(RoleName.ROLE_ADMIN)));
    }
}
