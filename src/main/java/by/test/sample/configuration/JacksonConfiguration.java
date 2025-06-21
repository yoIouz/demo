package by.test.sample.configuration;

import by.test.sample.utils.CustomLocalDateDeserializer;
import by.test.sample.utils.CustomLocalDateSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class JacksonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new CustomLocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDate.class, new CustomLocalDateDeserializer());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(javaTimeModule);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
