package com.paulograbin.confirmation;

import com.paulograbin.confirmation.domain.*;
import com.paulograbin.confirmation.persistence.ChapterRepository;
import com.paulograbin.confirmation.persistence.ParticipationRepository;
import com.paulograbin.confirmation.service.EventService;
import com.paulograbin.confirmation.service.ParticipationService;
import com.paulograbin.confirmation.service.RoleService;
import com.paulograbin.confirmation.service.UserService;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.module.jsr310.Jsr310Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static org.springframework.util.Assert.isTrue;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    // TODO Figure out a way to obtain the token in the controller test calls
    // TODO Validations on post requests
    // TODO Test everything
    // TODO Exception when user is invited more than once to a event
    // TODO almoço

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);


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

    @Resource
    private ChapterRepository chapterRepository;

    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.registerModule(new Jsr310Module());

        return modelMapper;
    }

//    @Bean
//    public void teste() {
//    // with 3.0 (or with 2.10 as alternative)
//    ObjectMapper mapper = JsonMapper.builder() // or different mapper for other format
//            .addModule(new ParameterNamesModule())
//            .addModule(new Jdk8Module())
//            .addModule(new JavaTimeModule())
//            // and possibly other configuration, modules, then:
//            .build();
//    }


    @Override
    public void run(String... args) throws Exception {
        if (roleService.fetchRoleCount() == 0) {
            Role admin = new Role(RoleName.ROLE_ADMIN);
            Role master = new Role(RoleName.ROLE_MC);
            Role user = new Role(RoleName.ROLE_USER);

            admin = roleService.save(admin);
            master = roleService.save(master);
            user = roleService.save(user);
        }

        if (participationService.fetchCount() == 0) {
            Chapter gvs = new Chapter();
            gvs.setId(592L);
            gvs.setName("Guardiões do Vale dos Sinos");
            gvs = chapterRepository.save(gvs);

            Chapter p1 = new Chapter();
            p1.setId(9L);
            p1.setName("Sir Hughes de Payens");
            p1 = chapterRepository.save(p1);

            User mc1 = new User("plgrabin", "Mestre", "Conselheiro", "plgrabin", "aaa");
            mc1 = userService.createUser(mc1);
            userService.setAsMaster(mc1.getId());
            userService.grantRoles(mc1.getId(), Set.of(roleService.getAdmin()));
            userService.addChapter(mc1.getId(), gvs.getId());

            User mc2 = new User("asimov", "Isaac", "Asimov", "isaac@asimov.com", "aaa");
            mc2 = userService.createUser(mc2);
            userService.addChapter(mc2.getId(), gvs.getId());


            Event e01 = new Event(gvs, "Mais antigo", DEFAULT_ADDRESS_JOAO_CORREA, "Evento mais velho", mc1,
                    LocalDate.of(2019, 01, 1),
                    LocalTime.of(14, 0, 0));
            e01 = eventService.createEvent(e01, mc1);

            Event e02 = new Event(gvs, "Proximo", DEFAULT_ADDRESS_JOAO_CORREA, "Proximo", mc1,
                    LocalDate.of(2020, 03, 20),
                    LocalTime.of(14, 0, 0));
            e02 = eventService.createEvent(e02, mc1);

            Event e03 = new Event(gvs, "Futuro", DEFAULT_ADDRESS_JOAO_CORREA, "Evento do futuro", mc1,
                    LocalDate.of(2020, 03, 1),
                    LocalTime.of(14, 0, 0));
            e03 = eventService.createEvent(e03, mc1);

            List<Event> upComingEvents = eventService.fetchUpComingEventsFromChapter(gvs.getId());
            isTrue(upComingEvents.size() == 2, "Two events for GVS");

            List<Event> allEventsFromGVS = eventService.fetchAllEventsFromChapter(gvs.getId());
            isTrue(allEventsFromGVS.size() == 3, "Three events for GVS");


            isTrue(e01.getParticipants().size() == 1, "Only one participant");
            isTrue(e01.getParticipants().get(0).getStatus() == ParticipationStatus.CONFIRMED, "Participant is confirmed");
            isTrue(e01.getParticipants().get(0).getUser().getUsername().equals("plgrabin"), "Participant is confirmed");
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
