package by.test.sample.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(

        String message,

        LocalDateTime timestamp

) {
}
