package com.paulograbin.confirmation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

@Configuration
public class JacksonConfig {

    private static final Logger log = LoggerFactory.getLogger(JacksonConfig.class);
    
    @Bean
    @Primary
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }
}

class LocalDateSerializer extends JsonSerializer {

    private static final Logger log = LoggerFactory.getLogger(LocalDateSerializer.class);
    static final DateTimeFormatter FORMATTER = ofPattern("dd/MM/yyyy");
//    static final DateTimeFormatter FORMATTER = ofPattern("yyyy/MM/dd");

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        log.info("Serializing");
        LocalDate v = (LocalDate) value;

        gen.writeString(v.format(FORMATTER));
    }
}

class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    private static final Logger log = LoggerFactory.getLogger(LocalDateDeserializer.class);
//    static final DateTimeFormatter FORMATTER = ofPattern("yyyy/MM/dd");
    static final DateTimeFormatter FORMATTER = ofPattern("dd/MM/yyyy");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        log.info("DE-Serializing");
         return LocalDate.parse(p.getValueAsString(), FORMATTER);
    }
}
