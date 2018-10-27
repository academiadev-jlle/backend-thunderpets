package br.com.academiadev.thunderpets.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
public class Localizacao {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private BigDecimal latitude;

    @NotNull
    private BigDecimal longitude;

    @NotNull
    private String cidade;

    @NotNull
    @Size(min = 2, max = 2)
    private String estado;
}
