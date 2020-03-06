package com.paulograbin.confirmation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

@Configuration
public class JacksonConfig {

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

    static final DateTimeFormatter FORMATTER = ofPattern("dd/MM/yyyy");

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        LocalDate v = (LocalDate) value;

        gen.writeString(v.format(FORMATTER));
    }
}

class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    static final DateTimeFormatter FORMATTER = ofPattern("dd/MM/yyyy");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return LocalDate.parse(p.getValueAsString(), FORMATTER);
    }
}
