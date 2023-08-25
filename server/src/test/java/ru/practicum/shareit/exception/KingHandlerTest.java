package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolationException;

public class KingHandlerTest {


    @Test
    public void testConstraintViolationException() {
        ConstraintViolationException exception = new ConstraintViolationException("Constraint violation", null);

        KingHandler kingHandler = new KingHandler();
        ResponseEntity<?> response = kingHandler.constraintViolationException(exception);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(ErrorResponse.class, response.getBody().getClass());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        Assertions.assertEquals(exception.getClass().toString(), errorResponse.getClassName());
        Assertions.assertEquals(exception.getMessage(), errorResponse.getMessage());
    }

    @Test
    public void testEmailDuplicateException() {
        EmailDuplicateException exception = new EmailDuplicateException("Email already exists");

        KingHandler kingHandler = new KingHandler();
        ResponseEntity<?> response = kingHandler.emailDuplicateException(exception);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals(ErrorResponse.class, response.getBody().getClass());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        Assertions.assertEquals(exception.getClass().toString(), errorResponse.getClassName());
        Assertions.assertEquals(exception.getMessage(), errorResponse.getMessage());
    }

    @Test
    public void testEntityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        KingHandler kingHandler = new KingHandler();
        ResponseEntity<?> response = kingHandler.entityNotFoundException(exception);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(ErrorResponse.class, response.getBody().getClass());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        Assertions.assertEquals(exception.getClass().toString(), errorResponse.getClassName());
        Assertions.assertEquals(exception.getMessage(), errorResponse.getMessage());
    }

    @Test
    public void testHandleUnsupportedStatusException() {
        UnsupportedStatusException exception = new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");

        KingHandler kingHandler = new KingHandler();
        ResponseEntity<KingHandler.ExceptionDto> response = kingHandler.handleUnsupportedStatusException(exception);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(KingHandler.ExceptionDto.class, response.getBody().getClass());
        KingHandler.ExceptionDto exceptionDto = response.getBody();
        Assertions.assertEquals(exception.getMessage(), exceptionDto.getError());
    }

    @Test
    public void testExceptionDto() {
        String error = "Unknown state: UNSUPPORTED_STATUS";
        KingHandler.ExceptionDto exceptionDto = new KingHandler.ExceptionDto(error);
        Assertions.assertEquals(error, exceptionDto.getError());
    }

}
