package by.test.sample.exception;

import by.test.sample.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocalException.class)
    public ResponseEntity<ApiErrorResponse> handleLocalException(Exception ex) {
        ApiErrorResponse error = new ApiErrorResponse(ex.getMessage(), LocalDateTime.now());
        if (ex instanceof UserNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({BindException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiErrorResponse> handleValidationException(Exception ex) {
        log.error(ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse("Request validation error", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
