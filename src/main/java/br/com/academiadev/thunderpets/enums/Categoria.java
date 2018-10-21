package br.com.academiadev.thunderpets.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Getter
public enum Categoria {
    ACHADOS(Status.PROCURANDO_DONO, Status.ACHEI_DONO, Status.NOVO_DONO),
    PERDIDOS(Status.PROCURANDO_PET, Status.ENCONTREI_MEU_PET),
    ADOCAO(Status.PARA_ADOTAR, Status.ADOTADO);

    private List<Status> status;

    Categoria(Status... status) {
        this.status = Arrays.asList(status);
    }
}
