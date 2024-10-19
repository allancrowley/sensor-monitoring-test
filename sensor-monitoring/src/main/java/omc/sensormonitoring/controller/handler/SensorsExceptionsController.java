package omc.sensormonitoring.controller.handler;

import java.util.stream.Collectors;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import static omc.sensormonitoring.controller.handler.ErrorMessages.*;

/**
 * A controller advice class for handling exceptions related to sensor monitoring.
 * This class provides centralized exception handling across all controllers
 * within the application. It intercepts exceptions thrown by methods
 * and returns a consistent response format.
 */
@ControllerAdvice
@Slf4j
public class SensorsExceptionsController {

    /**
     * Constructs a standardized response entity with a given message.
     *
     * @param message The message to be included in the response body.
     * @return A ResponseEntity containing the message and a BAD_REQUEST status.
     */
    private ResponseEntity<String> returnResponse(String message) {
        log.error(message);

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handles IllegalStateException and IllegalArgumentException thrown
     * by the application. Returns a response entity with the exception message.
     *
     * @param e The exception that was thrown.
     * @return A ResponseEntity with the error message and a BAD_REQUEST status.
     */
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    ResponseEntity<String> badRequestHandler(RuntimeException e) {

        return returnResponse(e.getMessage());
    }


    /**
     * Handles NoResourceFoundException thrown by the application.
     * Returns a response entity with a predefined no resource found message.
     *
     * @param e The exception that was thrown.
     * @return A ResponseEntity with the no resource found message and a BAD_REQUEST status.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<String> noResourceHandler(NoResourceFoundException e) {

        return returnResponse(NO_RESOURCE_FOUND_MESSAGE);
    }


    /**
     * Handles MethodArgumentNotValidException thrown during method argument validation.
     * Returns a response entity with the validation error messages.
     *
     * @param e The exception that was thrown.
     * @return A ResponseEntity containing validation error messages and a BAD_REQUEST status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<String> methodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        String message = e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(";"));

        return returnResponse(message);
    }


    /**
     * Handles HandlerMethodValidationException thrown during method argument validation.
     * Returns a response entity with the validation error messages.
     *
     * @param e The exception that was thrown.
     * @return A ResponseEntity containing validation error messages and a BAD_REQUEST status.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<String> methodValidationHandler(HandlerMethodValidationException e) {
        String message = e.getAllErrors().stream().map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(";"));

        return returnResponse(message);
    }


    /**
     * Handles MethodArgumentTypeMismatchException thrown when a method argument's type does not match.
     * Returns a response entity with a predefined type mismatch message.
     *
     * @param e The exception that was thrown.
     * @return A ResponseEntity with the type mismatch message and a BAD_REQUEST status.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<String> methodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {

        return returnResponse(TYPE_MISMATCH_MESSAGE);
    }


    /**
     * Handles HttpMessageNotReadableException thrown when a request body is not readable.
     * Returns a response entity with a predefined JSON type mismatch message.
     *
     * @param e The exception that was thrown.
     * @return A ResponseEntity with the JSON type mismatch message and a BAD_REQUEST status.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<String> jsonFieldTypeMismatchException(HttpMessageNotReadableException e) {

        return returnResponse(JSON_TYPE_MISMATCH_MESSAGE);
    }
}