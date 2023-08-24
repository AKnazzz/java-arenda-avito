package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class KingHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(final ConstraintViolationException e) {
        return new ResponseEntity<>(new ErrorResponse(String.valueOf(e.getClass()), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity<?> emailDuplicateException(final EmailDuplicateException e) {
        return new ResponseEntity<>(new ErrorResponse(String.valueOf(e.getClass()), e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> entityNotFoundException(final EntityNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(String.valueOf(e.getClass()), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleUnsupportedStatusException(UnsupportedStatusException ex) {
        return new ResponseEntity<>(new ExceptionDto("Unknown state: UNSUPPORTED_STATUS"), HttpStatus.BAD_REQUEST);
    }

    @Getter
    @RequiredArgsConstructor
    public static class ExceptionDto {
        private final String error;

        @JsonGetter(value = "error")
        public String getError() {
            return error;
        }
    }
}
