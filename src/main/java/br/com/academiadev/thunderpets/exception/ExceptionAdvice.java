package br.com.academiadev.thunderpets.exception;

import br.com.academiadev.thunderpets.dto.ApiResponse;
import br.com.academiadev.thunderpets.dto.ApiValidationErrors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(NaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ApiResponse handleNaoEncontradoException(NaoEncontradoException exception) {
        return new ApiResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ErroAoProcessarException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ApiResponse handleErroAoProcessar(ErroAoProcessarException exception) {
        return new ApiResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    protected ApiValidationErrors handleErroValidacaoProcesso(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
                        (oldValue, newValue) -> newValue));

        return new ApiValidationErrors(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Não foi possível processar esta requisição",
                errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    protected ApiValidationErrors handleConstraintValidation(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(o -> o.getPropertyPath().toString(), ConstraintViolation::getMessage));

        return new ApiValidationErrors(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Não foi possível processar esta requisição",
                errors);
    }
}
