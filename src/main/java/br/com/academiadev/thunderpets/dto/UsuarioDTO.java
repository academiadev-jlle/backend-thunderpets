package br.com.academiadev.thunderpets.dto;

import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {

    private Usuario usuario;
    private Set<ContatoDTO> contatos = new HashSet<>();;
}
