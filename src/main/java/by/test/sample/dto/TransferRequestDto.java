package by.test.sample.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequestDto(

        @NotNull
        Long toUserId,

        @NotNull
        BigDecimal amount
) {
}
