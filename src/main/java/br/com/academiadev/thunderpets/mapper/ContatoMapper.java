package br.com.academiadev.thunderpets.mapper;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ContatoMapper {

    ContatoDTO toDTO(Contato contato);

    @Mappings({
            @Mapping(source = "contatoDTO.id", target = "id"),
            @Mapping(source = "usuario", target = "usuario")
    })
    Contato toEntity(ContatoDTO contatoDTO, Usuario usuario);
}
