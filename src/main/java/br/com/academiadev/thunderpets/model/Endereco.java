package br.com.academiadev.thunderpets.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Data
@Embeddable
public class Endereco {
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String endereco;
}
