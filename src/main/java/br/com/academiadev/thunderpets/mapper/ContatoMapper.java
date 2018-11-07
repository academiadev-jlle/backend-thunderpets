package br.com.academiadev.thunderpets.mapper;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ContatoMapper {

    public abstract ContatoDTO toDTO(Contato contato);

    public Contato toEntity(ContatoDTO contatoDTO, Usuario usuario) {
        return Contato.builder()
                .id(contatoDTO.getId())
                .descricao(contatoDTO.getDescricao())
                .tipo(contatoDTO.getTipo())
                .usuario(usuario)
                .build();
    }
}
