package by.test.sample.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record PhoneDto(

        @NotEmpty
        List<@Pattern(regexp = "\\d{11}") String> phones
) {
}
