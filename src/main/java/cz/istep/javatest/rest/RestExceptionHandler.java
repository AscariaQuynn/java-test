package cz.istep.javatest.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Errors> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        Errors errors = new Errors();
        List<ValidationError> errorList = result.getFieldErrors().stream().map(e -> {
            return new ValidationError(e.getField(), e.getCode(), e.getDefaultMessage());
        }).collect(Collectors.toList());
        errors.setErrors(errorList);
        return ResponseEntity.badRequest().body(errors);
    }
}
