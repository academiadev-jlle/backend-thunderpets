package br.com.academiadev.thunderpets.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class ApiResponse {

    private Integer status;
    private String message;

    public ApiResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }
}
