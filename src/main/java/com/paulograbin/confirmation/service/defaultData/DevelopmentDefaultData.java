package com.paulograbin.confirmation.service.defaultData;

import com.paulograbin.confirmation.DemoApplication;
import com.paulograbin.confirmation.domain.Chapter;
import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.ChapterRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.persistence.UserRequestRepository;
import com.paulograbin.confirmation.service.ChapterService;
import com.paulograbin.confirmation.service.EventService;
import com.paulograbin.confirmation.service.RoleService;
import com.paulograbin.confirmation.service.UserService;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import com.paulograbin.confirmation.usecases.pseudouser.creation.CreatePseudoUserRequest;
import com.paulograbin.confirmation.usecases.pseudouser.creation.CreatePseudoUserUseCase;
import com.paulograbin.confirmation.usecases.user.UpdateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;


@Service
@Profile("!production")
public class DevelopmentDefaultData implements DefaultData, CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);


    private static final String DEFAULT_ADDRESS_JOAO_CORREA = "Avenida João Corrêa, 815";
    private static final String ADMIN_USERNAME = "plgrabin";
    private static final String ADMIN_EMAIL = "plgrabin@gmail.com";

    @Resource
    private ChapterService chapterService;

    @Resource
    private UserService userService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ChapterRepository chapterRepository;

    @Resource
    private RoleService roleService;

    @Resource
    private EventService eventService;

    @Resource
    private UserRequestRepository userRequestRepository;


    @Override
    public void run(String... args) {
        log.info("Running development default data");

        setDefaultRoles();
        setDefaultChapters();
        setDefaultAdmin();

        setDefaultEvents();
        setDefaultUsers();

        setUserRequests();
    }

    private void setUserRequests() {
        int random = new Random().nextInt();

        CreatePseudoUserRequest request = new CreatePseudoUserRequest();
        request.setFirstName("Requesting User 1");
        request.setLastName("Will be created");
        request.setEmail(random + "augusto@augusto.com");
        request.setChapterId(592L);
        request.setRequestingUser(userService.fetchByUsername("plgrabin").getId());

        new CreatePseudoUserUseCase(request, userRepository, userRequestRepository, chapterRepository).execute();
    }

    private void setDefaultEvents() {
        log.info("Checking events...");

        if (eventService.fetchCount() > 0) {
            log.info("Skipping default events.");
            return;
        }

        Chapter gvs = chapterService.fetchById(592L);
        User mc1 = userService.fetchByUsername(ADMIN_USERNAME);

        Event e01 = new Event(gvs, "Mais antigo", DEFAULT_ADDRESS_JOAO_CORREA, "Evento mais velho", mc1,
                LocalDate.of(2019, 01, 1),
                LocalTime.of(14, 0, 0));
        e01 = eventService.createEvent(e01, mc1);

        Event e02 = new Event(gvs, "Proximo", DEFAULT_ADDRESS_JOAO_CORREA, "Proximo", mc1,
                LocalDate.of(2021, 06, 20),
                LocalTime.of(14, 0, 0));
        e02 = eventService.createEvent(e02, mc1);

        Event e03 = new Event(gvs, "Futuro", DEFAULT_ADDRESS_JOAO_CORREA, "Evento do futuro", mc1,
                LocalDate.of(2021, 06, 1),
                LocalTime.of(14, 0, 0));
        e03 = eventService.createEvent(e03, mc1);

//        List<Event> upComingEvents = eventService.fetchUpComingEventsFromChapter(gvs.getId());
//        isTrue(upComingEvents.size() == 2, "Two events for GVS");
//
//        List<Event> allEventsFromGVS = eventService.fetchAllEventsFromChapter(gvs.getId());
//        isTrue(allEventsFromGVS.size() == 3, "Three events for GVS");


//        isTrue(e01.getParticipants().size() == 1, "Only one participant");
//        isTrue(e01.getParticipants().get(0).getStatus() == ParticipationStatus.CONFIRMADO, "Participant is confirmed");
//        isTrue(e01.getParticipants().get(0).getUser().getUsername().equals(ADMIN_USERNAME), "Participant is confirmed");
    }

    private void setDefaultUsers() {
        log.info("Checking users...");

        Chapter chapter_gvs = chapterService.fetchById(592L);

        User mc2 = new User("asimov", "Isaac", "Asimov", "isaac@asimov.com", "aaa");
        createUserIfDoesntExist(mc2, chapter_gvs, false);

        Chapter chapter200 = chapterService.fetchById(592L);
        User master_2 = new User("master2", "Mestre 2", "Sobrenome @", "mestre@100.com", "aaa");
        createUserIfDoesntExist(master_2, chapter200, true);

        User not_master_2 = new User("notmaster2", "Not Mestre 2", "Not Sobrenome @", "not_mestre@100.com", "aaa");
        createUserIfDoesntExist(not_master_2, chapter200, false);

        createUserIfDoesntExist(new User("arthurdeazambuja@gmail.com", "Arthur de Azambuja", "", "arthurdeazambuja@gmail.com", "arthurdeazambuja@gmail.com"), chapter_gvs, false);
        createUserIfDoesntExist(new User("augustolemosborges123@gmail.com", "Augusto Lemos Borges", "", "augustolemosborges123@gmail.com", "augustolemosborges123@gmail.com"), chapter_gvs, false);
        createUserIfDoesntExist(new User("brunocachoeira50@gmail.com", "Bruno Bueno da Silva Walterman Cachoeira", "", "brunocachoeira50@gmail.com", "brunocachoeira50@gmail.com"), chapter_gvs, false);
        createUserIfDoesntExist(new User("carpesbruno29@gmail.com", "Bruno Carpes Cardoso", "", "carpesbruno29@gmail.com", "carpesbruno29@gmail.com"), chapter_gvs, true);
        createUserIfDoesntExist(new User("brunopachecojost@hotmail.com", "Bruno Henrique Pachaco Jost", "", "brunopachecojost@hotmail.com", "brunopachecojost@hotmail.com"), chapter_gvs, false);
        createUserIfDoesntExist(new User("brunoleonardof.rocha@gmail.com", "Bruno Leonardo Fernandes da Rocha", "", "brunoleonardof.rocha@gmail.com", "brunoleonardof.rocha@gmail.com"), chapter_gvs, false);
        createUserIfDoesntExist(new User("bruno.rostoliveira@gmail.com", "Bruno Rost de Oliveira da Silva", "", "bruno.rostoliveira@gmail.com", "bruno.rostoliveira@gmail.com"), chapter_gvs, false);
        createUserIfDoesntExist(new User("serafinibruno67@gmail.com", "Bruno Serafini da Silva", "", "serafinibruno67@gmail.com", "serafinibruno67@gmail.com"), chapter_gvs, false);
        createUserIfDoesntExist(new User("bruwillrich@gmail.com", "Bruno Willrich", "", "bruwillrich@gmail.com", "bruwillrich@gmail.com"), chapter_gvs, false);
        createUserIfDoesntExist(new User("cassioclds@gmail.com", "Cássio Alexandre Dornelles Bolsoni", "", "cassioclds@gmail.com", "cassioclds@gmail.com"), chapter_gvs, false);
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
            log.info("User {} already exists, no need to create it again", newUser.getUsername());
        }
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

            userService.assignUserToChapter(defaultAdmin.getId(), 592L);
        }

        log.info("Setting admin password....");
        UpdateUserRequest defaultPasswordRequest = new UpdateUserRequest();
        defaultPasswordRequest.setId(defaultAdmin.getId());
        defaultPasswordRequest.setFirstName(defaultAdmin.getFirstName());
        defaultPasswordRequest.setLastName(defaultAdmin.getLastName());
        defaultPasswordRequest.setPassword("123123");
        userService.updateUser(defaultAdmin.getId(), defaultPasswordRequest);

        userService.assignUserToChapter(defaultAdmin.getId(), 592L);

        log.info("Setting admin authorizations....");
        userService.setAsMaster(defaultAdmin.getId());
        userService.grantRoles(defaultAdmin.getId(), new HashSet<>(Collections.singletonList(roleService.getByName(RoleName.ROLE_ADMIN))));
    }

    private void setDefaultChapters() {
        log.info("Checking for default chapters...");

        if (chapterService.count() != 0) {
            return;
        }

        chapterService.createChapter(new ChapterCreationRequest(100L, "Capitulo AAAAAAAAA"));
        chapterService.createChapter(new ChapterCreationRequest(200L, "Capitulo BBBBBBBBB"));
        chapterService.createChapter(new ChapterCreationRequest(592L, "GVS"));
    }

}
