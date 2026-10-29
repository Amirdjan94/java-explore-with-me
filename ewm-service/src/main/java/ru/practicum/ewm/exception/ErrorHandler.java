package ru.practicum.ewm.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFoundException(final ObjectNotFoundException e) {
        return new ErrorResponse("The required object was not found.", e.getMessage(), "NOT_FOUND");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        List<String> errorsList = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            Object rejectedValue = fieldError.getRejectedValue();

            String formattedMessage = String.format(
                    "Field: %s. Error: %s. Value: %s",
                    fieldName, errorMessage, rejectedValue
            );
            errorsList.add(formattedMessage);
        });
        return new ErrorResponse("Incorrectly made request.", errorsList.getFirst(), "BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlePSQLException(final DataIntegrityViolationException e) {
        return new ErrorResponse("Integrity constraint has been violated.", e.getLocalizedMessage(), "CONFLICT");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConditionsNotMetException(final ConditionsNotMetException e) {
        return new ErrorResponse("Incorrect argument in payload.", e.getMessage(), "BAD REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(final ForbiddenException e) {
        return new ErrorResponse("The required object was not found.", e.getMessage(), "FORBIDDEN");
    }

}
