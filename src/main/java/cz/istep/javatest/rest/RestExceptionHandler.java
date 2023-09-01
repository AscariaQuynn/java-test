package cz.istep.javatest.rest;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Errors> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        Errors errors = new Errors();
        List<ValidationError> errorList = result.getFieldErrors().stream()
            .map(e -> new ValidationError(e.getField(), e.getCode(), e.getDefaultMessage()))
            .sorted(Comparator.comparing(ValidationError::getField))
            .collect(Collectors.toList());
        errors.setErrors(errorList);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Errors> handleDeletionException(EmptyResultDataAccessException ex) {
        return ResponseEntity.badRequest()
            .body(new Errors(List.of(new ValidationError(null, null, ex.getMessage()))));
    }
}
