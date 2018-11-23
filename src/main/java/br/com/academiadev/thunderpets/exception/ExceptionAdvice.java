package br.com.academiadev.thunderpets.exception;

import br.com.academiadev.thunderpets.dto.ExceptionDTO;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ExceptionDTO handleNaoEncontradoException(NaoEncontradoException exception) {
        return new ExceptionDTO(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
