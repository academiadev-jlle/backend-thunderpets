package br.com.academiadev.thunderpets.mapper;

import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.dto.UsuarioRespostaDTO;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioRespostaDTO toDTO(Usuario usuario, Set<Contato> contatos);

    Usuario toEntity(UsuarioDTO usuarioDTO);
}
