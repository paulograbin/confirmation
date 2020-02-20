package com.paulograbin.confirmation;

import com.paulograbin.confirmation.persistence.ParticipationRepository;
import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.service.EventService;
import com.paulograbin.confirmation.service.ParticipationService;
import com.paulograbin.confirmation.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static java.time.format.DateTimeFormatter.ofPattern;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    // TOOD:
    // Figure out a way to obtain the token in the controller test calls
    // Identify if user has permission to invite and confirm users
    // Get events to which I'm invited
    // Get events to which I'm invited but haven't confirmed yet
    // Validations on post requests
    // Test everything
    // Exception when user is invited more than once to a event
    // Make event and user unique in participation model


    public static final String DEFAULT_ADDRESS_JOAO_CORREA = "Avenida João Corrêa, 815";

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC-3"));
    }

    @Bean
    HttpTraceRepository configIt() {
        return new LoggerHttpTrace();
    }

//    @Bean
//    @Primary
//    public ObjectMapper serializingObjectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        JavaTimeModule javaTimeModule = new JavaTimeModule();
//
////        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateSerializer());
////        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
//        objectMapper.registerModule(javaTimeModule);
//        return objectMapper;
//    }

//    class LocalDateSerializer extends JsonSerializer<LocalDateTime> {
//
//        public final DateTimeFormatter FORMATTER = ofPattern("MM/dd/yyyy hh:mm:ss");
//
//        @Override
//        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//            gen.writeString(value.format(FORMATTER));
//        }
//    }

//    class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
//        public final DateTimeFormatter FORMATTER = ofPattern("dd::MM::yyyy");
//
//        @Override
//        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//            return LocalDate.parse(p.getValueAsString(), FORMATTER);
//        }
//    }

    @Resource
    private ParticipationRepository participationRepository;

    @Resource
    private UserService userService;

    @Resource
    private EventService eventService;

    @Resource
    private ParticipationService participationService;

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
            mc1.setMaster(true);
            mc1.setRoles(Set.of(admin, master));
            User mc2 = new User("asimov", "Isaac", "Asimov", "asimov", "aaa");
            User mc3 = new User("primeiroconselheiro", "Primeiro", "Conselheiro", "primeiroconselheiro", "aaa");

            mc1 = userService.createUser(mc1);
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
    }
}
