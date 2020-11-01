package com.paulograbin.confirmation.service.defaultData;

import com.paulograbin.confirmation.DemoApplication;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.service.RoleService;
import com.paulograbin.confirmation.service.UserService;
import com.paulograbin.confirmation.service.mail.EmailService;
import com.paulograbin.confirmation.usecases.user.UpdateUserRequest;
import com.paulograbin.confirmation.userequest.UserRequestRepository;
import com.paulograbin.confirmation.userequest.usecases.creation.CreatePseudoUserRequest;
import com.paulograbin.confirmation.userequest.usecases.creation.CreatePseudoUserUseCase;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;


@Profile("production")
@Service
public class ProductionDefaultData implements DefaultData, CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

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


    @Override
    public void run(String... args) {
        log.info("Running production default data");

        setDefaultAdmin();
        setUserRequests();
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
//        users.put("paulo.grabin@sap.com", new NamePair("Paulo", "Grabin"));

//        users.put("brunocachoeira50@gmail.com", new NamePair("Bruno", "Bueno da Silva Walterman Cachoeira"));
//        users.put("cris.fs10@gmail.com", new NamePair("Cristiano", "Fernandes dos Santos"));
//        users.put("daniel.maciel305@gmail.com", new NamePair("Daniel", "França Maciel"));
//        users.put("bampiemanuel@gmail.com", new NamePair("Emanuel", "da Silva Bampi"));
//        users.put("fernandoschulzdossantos@gmail.com", new NamePair("Fernando", "Schulz dos Santos"));
//        users.put("fernando.wassum@hotmail.com", new NamePair("Fernando", "Wassum"));
//        users.put("gabriels.bampi@gmail.com", new NamePair("Gabriel", "da Silva Bampi"));
//        users.put("guilima76@hotmail.com", new NamePair("Guilherme", "de Lima Rodrigues"));
//        users.put("guilherme.o.lealii@gmail.com", new NamePair("Guilherme", "de Oliveira Leal"));
//        users.put("gustavo2002humann@gmail.com", new NamePair("Gustavo", "Humann"));
//        users.put("xavier.leobecker@gmail.com", new NamePair("Leonardo", "Becker Antunes Xavier"));
//        users.put("machadoiileonardo@gmail.com", new NamePair("Leonardo", "Machado II"));
//        users.put("lucasmouramaidana@gmail.com", new NamePair("Lucas", " de Moura Maidana"));
//        users.put("matheuszini28@gmail.com", new NamePair("Matheus", "Lucas Zini"));
//        users.put("pedroaraujodasilvasauro@gmail.com", new NamePair("Pedro", "Araujo da Silva"));
//        users.put("pedrocrocodilo@gmail.com", new NamePair("Pedro", "Vargas de Moura"));
//        users.put("gurisdonato@gmail.com", new NamePair("Rafael", "Franco Donato"));
//        users.put("tiagofreitassilva4@gmail.com", new NamePair("Tiago", "de Freitas da Silva"));
//        users.put("sartoritobias14@gmail.com", new NamePair("Tobias", "Sartori da Silva"));
//        users.put("willl.sauer0@gmail.com", new NamePair("William", "Leichtweis Sauer"));
//        users.put("augustolemosborges123@gmail.com", new NamePair("Augusto", "Borges"));
//        users.put("brunopachecojost@hotmail.com", new NamePair("Bruno", "Jost"));
//        users.put("brunoleonardof.rocha@gmail.com", new NamePair("Bruno", "Rocha"));
//        users.put("crizantearaujodequadrosneto@gmail.com", new NamePair("Crizante", "Neto"));

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
