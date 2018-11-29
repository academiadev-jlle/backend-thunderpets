package br.com.academiadev.thunderpets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PetDistanciaDTO {

    PetDTO pet;
    BigDecimal distancia;
}
