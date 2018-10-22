package br.com.academiadev.thunderpets.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public enum Status {
    ACHEI_DONO(Categoria.ACHADOS),
    PROCURANDO_DONO(Categoria.ACHADOS),
    NOVO_DONO(Categoria.ACHADOS),
    ENCONTREI_MEU_PET(Categoria.PERDIDOS),
    PROCURANDO_PET(Categoria.PERDIDOS),
    ADOTADO(Categoria.ADOCAO),
    PARA_ADOTAR(Categoria.ADOCAO);

    private Categoria categoria;

    Status(Categoria categoria) {
        this.categoria = categoria;
    }
}
