package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioUtil {

    public Usuario criarUsuarioKamuela() {
        return Usuario.builder()
                .nome("Kamuela Pereira")
                .email("kamuela@mail.com")
                .senha("kamuela123")
                .foto(new byte[]{7, 8, 9})
                .ativo(true)
                .build();
    }
}
