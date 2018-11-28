package br.com.academiadev.thunderpets.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
public class ApiValidationErrors {

    private Integer status;
    private String message;
    private Map<String, String> errors;

    public ApiValidationErrors(HttpStatus status, String message, Map<String, String> errors) {
        this.status = status.value();
        this.message = message;
        this.errors = errors;
    }
}
