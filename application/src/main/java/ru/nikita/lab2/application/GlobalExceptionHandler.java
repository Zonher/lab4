package ru.nikita.lab2.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.security.authorization.AuthorizationDeniedException;
import ru.nikita.lab2.application.dto.ErrorResponse;
import ru.nikita.lab2.service.exception.*;


@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleService(ServiceException ex) {
        HttpStatus status =
                switch (ex) {
                    case NoUserFoundException ignored -> HttpStatus.NOT_FOUND;
                    case NoAccountFoundException ignored -> HttpStatus.NOT_FOUND;
                    case UserAlreadyExistsException ignored -> HttpStatus.CONFLICT;
                    case AccountNotEmptyException ignored -> HttpStatus.CONFLICT;
                    default -> HttpStatus.BAD_REQUEST;
                };
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), ex.getMessage()));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ConcurrencyFailureException.class})
    public ResponseEntity<Object> handleConflict(RuntimeException ex) {
        LOG.warn("Database conflict", ex);
        return ResponseEntity.status(409)
                .body(new ErrorResponse(409, "Data conflict; check the current resource state"));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String message = HttpStatus.valueOf(status.value()).getReasonPhrase();
        if (ex instanceof MethodArgumentNotValidException validation) {
            message =
                    validation.getBindingResult().getFieldErrors().stream()
                            .map(error -> error.getField() + ": " + error.getDefaultMessage())
                            .sorted()
                            .findFirst()
                            .orElse(message);
        }
        return super.handleExceptionInternal(
                ex, new ErrorResponse(status.value(), message), headers, status, request);
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AuthorizationDeniedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        HttpStatus.FORBIDDEN.value(),
                        "Forbidden"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception ex) {
        LOG.error("Unexpected request failure", ex);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(500, "Internal server error"));
    }
}
