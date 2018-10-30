package br.com.academiadev.thunderpets.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public enum Status {
    ACHEI_DONO(Categoria.ACHADO),
    PROCURANDO_DONO(Categoria.ACHADO),
    NOVO_DONO(Categoria.ACHADO),
    ENCONTREI_MEU_PET(Categoria.PERDIDO),
    PROCURANDO_PET(Categoria.PERDIDO),
    ADOTADO(Categoria.ADOCAO),
    PARA_ADOTAR(Categoria.ADOCAO);

    private Categoria categoria;

    Status(Categoria categoria) {
        this.categoria = categoria;
    }
}
