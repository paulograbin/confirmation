package com.paulograbin.confirmation;

import com.paulograbin.confirmation.domain.Chapter;
import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.service.ChapterService;
import com.paulograbin.confirmation.service.EventService;
import com.paulograbin.confirmation.service.RoleService;
import com.paulograbin.confirmation.service.UserService;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import com.paulograbin.confirmation.usecases.UpdateUserRequest;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.module.jsr310.Jsr310Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.apache.commons.lang3.Validate.isTrue;

@SpringBootApplication
@EnableCaching
public class DemoApplication implements CommandLineRunner {

    // TODO Figure out a way to obtain the token in the controller test calls
    // TODO Validations on post requests
    // TODO Test everything
    // TODO Exception when user is invited more than once to a event
    // TODO almoço

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);


    private static final String DEFAULT_ADDRESS_JOAO_CORREA = "Avenida João Corrêa, 815";
    private static final String ADMIN_USERNAME = "plgrabin";
    private static final String ADMIN_EMAIL = "plgrabin@gmail.com";

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Resource
    private UserService userService;

    @Resource
    private EventService eventService;

    @Resource
    private RoleService roleService;

    @Resource
    private ChapterService chapterService;

    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.registerModule(new Jsr310Module());

        return modelMapper;
    }

    @Override
    public void run(String... args) {
        setDefaultRoles();
//        setDefaultChapters();
        setDefaultAdmin();

//        setDefaultUsers();
//        setDefaultEvents();

        log.info("Application ready to roll.");
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


//        Chapter chapter200 = chapterService.fetchById(200L);
//        User master_2 = new User("master2", "Mestre 2", "Sobrenome @", "mestre@100.com", "aaa");
//        createUserIfDoesntExist(master_2, chapter200, true);
//
//        User not_master_2 = new User("notmaster2", "Not Mestre 2", "Not Sobrenome @", "not_mestre@100.com", "aaa");
//        createUserIfDoesntExist(not_master_2, chapter200, false);



//        createUserIfDoesntExist(new User("arthurdeazambuja@gmail.com", "Arthur de Azambuja", "", "arthurdeazambuja@gmail.com", "arthurdeazambuja@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("augustolemosborges123@gmail.com", "Augusto Lemos Borges", "", "augustolemosborges123@gmail.com", "augustolemosborges123@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("brunocachoeira50@gmail.com", "Bruno Bueno da Silva Walterman Cachoeira", "", "brunocachoeira50@gmail.com", "brunocachoeira50@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("carpesbruno29@gmail.com", "Bruno Carpes Cardoso", "", "carpesbruno29@gmail.com", "carpesbruno29@gmail.com"), chapter_gvs, true);
//        createUserIfDoesntExist(new User("brunopachecojost@hotmail.com", "Bruno Henrique Pachaco Jost", "", "brunopachecojost@hotmail.com", "brunopachecojost@hotmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("brunoleonardof.rocha@gmail.com", "Bruno Leonardo Fernandes da Rocha", "", "brunoleonardof.rocha@gmail.com", "brunoleonardof.rocha@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("bruno.rostoliveira@gmail.com", "Bruno Rost de Oliveira da Silva", "", "bruno.rostoliveira@gmail.com", "bruno.rostoliveira@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("serafinibruno67@gmail.com", "Bruno Serafini da Silva", "", "serafinibruno67@gmail.com", "serafinibruno67@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("bruwillrich@gmail.com", "Bruno Willrich", "", "bruwillrich@gmail.com", "bruwillrich@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("cassioclds@gmail.com", "Cássio Alexandre Dornelles Bolsoni", "", "cassioclds@gmail.com", "cassioclds@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("cris.fs10@gmail.com", "Cristiano Fernandes dos Santos", "", "cris.fs10@gmail.com", "cris.fs10@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("crizantearaujodequadrosneto@gmail.com", "Crizante Araujo de Quadros Neto", "", "crizantearaujodequadrosneto@gmail.com", "crizantearaujodequadrosneto@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("daniel.maciel305@gmail.com", "Daniel França Maciel", "", "daniel.maciel305@gmail.com", "daniel.maciel305@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("godyskiper@gmail.com", "Eduardo Godoi da Silveira", "", "godyskiper@gmail.com", "godyskiper@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("bampiemanuel@gmail.com", "Emanuel da Silva Bampi", "", "bampiemanuel@gmail.com", "bampiemanuel@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("lipeschn@gmail.com", "Felipe Schneider Bomfim", "", "lipeschn@gmail.com", "lipeschn@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("fernandoschulzdossantos@gmail.com", "Fernando Schulz dos Santos", "", "fernandoschulzdossantos@gmail.com", "fernandoschulzdossantos@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("fernando.wassum@hotmail.com", "Fernando Wassum", "", "fernando.wassum@hotmail.com", "fernando.wassum@hotmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("filipefidelis25@gmail.com", "Filipe Fidelis Wolffenbuttel", "", "filipefidelis25@gmail.com", "filipefidelis25@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("gabriels.bampi@gmail.com", "Gabriel da Silva Bampi", "", "gabriels.bampi@gmail.com", "gabriels.bampi@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("guilima76@hotmail.com", "Guilherme de Lima Rodrigues", "", "guilima76@hotmail.com", "guilima76@hotmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("guilherme.o.lealii@gmail.com", "Guilherme de Oliveira Leal", "", "guilherme.o.lealii@gmail.com", "guilherme.o.lealii@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("gustavo2002humann@gmail.com", "Gustavo Humann", "", "gustavo2002humann@gmail.com", "gustavo2002humann@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("henrique10bebber@yahoo.com", "Henrique Bebber da Rosa", "", "henrique10bebber@yahoo.com", "henrique10bebber@yahoo.com"), chapter_gvs, true);
//        createUserIfDoesntExist(new User("joaojj5056@hotmail.com", "João Lucas Ramos Belmonte", "", "joaojj5056@hotmail.com", "joaojj5056@hotmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("kauapower@hotmail.com", "Kauã Fogaça Cezar", "", "kauapower@hotmail.com", "kauapower@hotmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("xavier.leobecker@gmail.com", "Leonardo Becker Antunes Xavier", "", "xavier.leobecker@gmail.com", "xavier.leobecker@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("machadoiileonardo@gmail.com", "Leonardo Machado II", "", "machadoiileonardo@gmail.com", "machadoiileonardo@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("leonardo.oliveira@gmail.com", "Leonardo Muller de Oliveira", "", "leonardo.oliveira@gmail.com", "leonardo.oliveira@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("lokinalua@gmail.com", "Luan Guedes da Rocha", "", "lokinalua@gmail.com", "lokinalua@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("lucasmouramaidana@gmail.com", "Lucas de Moura Maidana", "", "lucasmouramaidana@gmail.com", "lucasmouramaidana@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("luishenriquecouto@hotmail.com", "Luis Henrique de Oliveira Couto", "", "luishenriquecouto@hotmail.com", "luishenriquecouto@hotmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("luis.otavioccm@hotmail.com", "Luis Otávio Cabral Carniel Marques", "", "luis.otavioccm@hotmail.com", "luis.otavioccm@hotmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("theteuferreira@gmail.com", "Matheus Ferreira da Silva", "", "theteuferreira@gmail.com", "theteuferreira@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("mgodoidasilveira@gmail.com", "Matheus Godoi da Silveira", "", "mgodoidasilveira@gmail.com", "mgodoidasilveira@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("matheuszini28@gmail.com", "Matheus Lucas Zini", "", "matheuszini28@gmail.com", "matheuszini28@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("nicolasdbohrer@gmail.com", "Nícolas Dias Bohrer", "", "nicolasdbohrer@gmail.com", "nicolasdbohrer@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("pabloborbaz@gmail.com", "Pablo de Borba", "", "pabloborbaz@gmail.com", "pabloborbaz@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("pedroaraujodasilvasauro@gmail.com", "Pedro Araujo da Silva", "", "pedroaraujodasilvasauro@gmail.com", "pedroaraujodasilvasauro@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("kuhn@terra.com.br", "Pedro Henrique Kuhn", "", "kuhn@terra.com.br", "kuhn@terra.com.br"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("pedrohenriquefontoura487@gmail.com", "Pedro Henrique Rosa da Fontoura", "", "pedrohenriquefontoura487@gmail.com", "pedrohenriquefontoura487@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("pedrocrocodilo@gmail.com", "Pedro Vargas de Moura", "", "pedrocrocodilo@gmail.com", "pedrocrocodilo@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("petersonmetzlima@gmail.com", "Peterson Metz Lima", "", "petersonmetzlima@gmail.com", "petersonmetzlima@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("gurisdonato@gmail.com", "Rafael Franco Donato", "", "gurisdonato@gmail.com", "gurisdonato@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("richardmilzarek24@gmail.com", "Richard Gustavo Milczarek de Abreu", "", "richardmilzarek24@gmail.com", "richardmilzarek24@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("sergioabcdef@outlook.com", "Sérgio Mário Arsand Neto", "", "sergioabcdef@outlook.com", "sergioabcdef@outlook.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("tiagofreitassilva4@gmail.com", "Tiago de Freitas da Silva", "", "tiagofreitassilva4@gmail.com", "tiagofreitassilva4@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("sartoritobias14@gmail.com", "Tobias Sartori da Silva", "", "sartoritobias14@gmail.com", "sartoritobias14@gmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("vitinho0303@hotmail.com", "Victor Hugo Andreon de Camargo", "", "vitinho0303@hotmail.com", "vitinho0303@hotmail.com"), chapter_gvs, false);
//        createUserIfDoesntExist(new User("willl.sauer0@gmail.com", "William Leichtweis Sauer", "", "willl.sauer0@gmail.com", "willl.sauer0@gmail.com"), chapter_gvs, false);
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

    private void setDefaultChapters() {
        log.info("Checking for default chapters...");

        if (chapterService.count() != 0) {
            return;
        }

//        chapterService.createChapter(new ChapterCreationRequest(100L, "Capitulo AAAAAAAAA"));
//        chapterService.createChapter(new ChapterCreationRequest(200L, "Capitulo BBBBBBBBB"));
        chapterService.createChapter(new ChapterCreationRequest(592L, "GVS"));
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
        defaultPasswordRequest.setPassword("123123123");
        userService.updateUser(defaultAdmin.getId(), defaultPasswordRequest);

        log.info("Setting admin authorizations....");
        userService.setAsMaster(defaultAdmin.getId());
        userService.grantRoles(defaultAdmin.getId(), new HashSet<>(Collections.singletonList(roleService.getByName(RoleName.ROLE_ADMIN))));
    }
}
