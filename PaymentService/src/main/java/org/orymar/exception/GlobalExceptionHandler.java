package org.orymar.exception;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.PropertyValueException;
import org.orymar.exception.domain.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        ErrorResponse response =
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Error",
                        "Invalid input data",
                        LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse response =
                new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {
        String message = "Database constraint violation or null value";
        Throwable cause = ex.getCause();
        if (cause instanceof PropertyValueException pve) {
            message = "Missing required field: " + pve.getPropertyName();
        }

        ErrorResponse response =
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(), "Invalid Request", message, LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleInvalidJson(Exception ex) {
        ErrorResponse response =
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid Request",
                        "Malformed JSON or invalid field types",
                        LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorResponse response =
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Type Mismatch",
                        "Invalid request parameter type",
                        LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
