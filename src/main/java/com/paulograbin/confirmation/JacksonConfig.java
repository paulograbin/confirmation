package com.paulograbin.confirmation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

@Configuration
public class JacksonConfig {

//    void teste() {
//        // with 3.0 (or with 2.10 as alternative)
//        ObjectMapper mapper = JsonMapper.builder() // or different mapper for other format
//                .addModule(new ParameterNamesModule())
//                .addModule(new Jdk8Module())
//                .addModule(new JavaTimeModule())
//                // and possibly other configuration, modules, then:
//                .build();
//    }


    @Bean
    @Primary
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateDeserializer());
        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }
}

class LocalDateSerializer extends JsonSerializer {

    static final DateTimeFormatter FORMATTER = ofPattern("dd/MM/yyyy hh:mm:ss");

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        LocalDateTime v = (LocalDateTime) value;

        gen.writeString(v.format(FORMATTER));
    }
}

class LocalDateDeserializer extends JsonDeserializer<LocalDateTime> {

    static final DateTimeFormatter FORMATTER = ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return LocalDateTime.parse(p.getValueAsString(), FORMATTER);
    }
}
