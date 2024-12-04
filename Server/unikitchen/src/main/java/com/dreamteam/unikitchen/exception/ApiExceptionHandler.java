package com.dreamteam.unikitchen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {

    // Handles generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponse> handleGenericException(Exception ex, WebRequest request) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                "Internal Server Error",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handles 404 errors
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiExceptionResponse> handleNoHandlerFoundException(NoHandlerFoundException ex, WebRequest request) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                "Not Found",
                "The requested endpoint does not exist",
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handles MethodArgumentNotValidException (validation errors)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ApiExceptionResponse response = new ApiExceptionResponse(
                "Validation Failed",
                errors.toString(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles MissingServletRequestParameterException
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiExceptionResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                "Missing Parameter",
                "Parameter '" + ex.getParameterName() + "' is missing",
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles MethodArgumentTypeMismatchException
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiExceptionResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                "Type Mismatch",
                "Parameter '" + ex.getName() + "' should be of type '" + ex.getRequiredType().getSimpleName() + "'",
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                "Illegal Argument",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles IngredientNotFoundException
    @ExceptionHandler(IngredientNotFoundException.class)
    public ResponseEntity<ApiExceptionResponse> handleIngredientNotFoundException(IngredientNotFoundException ex) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                "Ingredient Not Found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handles RecipeNotFoundException
    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ApiExceptionResponse> handleRecipeNotFoundException(RecipeNotFoundException ex) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                "Recipe Not Found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handles UnauthorizedAccessException
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiExceptionResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                "Unauthorized Access",
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Handles UserAlreadyExistsException
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiExceptionResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                "User Already Exists",
                ex.getMessage(),
                HttpStatus.CONFLICT.value()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Handles UserNotFoundException
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiExceptionResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                "User Not Found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
