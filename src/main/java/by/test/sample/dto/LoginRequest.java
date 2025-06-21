package by.test.sample.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest (

        @NotBlank
        String identity,

        @NotBlank
        @Size(min = 8, max = 500)
        String password

) {
}
