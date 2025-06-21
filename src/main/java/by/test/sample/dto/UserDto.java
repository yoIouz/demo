package by.test.sample.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto (

        @Nullable
        String name,

        @Nullable
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
        LocalDate dateOfBirth,

        @Nullable
        @Size(min = 8, max = 500)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,

        @Nullable
        List<@Pattern(regexp = "\\d{11}") String> phones,

        @Nullable
        List<@Email String> emails
) implements Serializable {
}
