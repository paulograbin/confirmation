package com.paulograbin.confirmation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

@Configuration
public class JacksonConfig {



//    @Bean
//    @Primary
//    public ObjectMapper serializingObjectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        JavaTimeModule javaTimeModule = new JavaTimeModule();
////        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateSerializer());
////        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateDeserializer());
//        objectMapper.registerModule(javaTimeModule);
//
//        return objectMapper;
//    }
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
