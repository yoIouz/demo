package by.test.sample.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record EmailDto(

        @NotEmpty
        List<@NotBlank @Email String> emails
) {
}
