package br.com.academiadev.thunderpets.mapper;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UsuarioMapper {

    @Autowired
    ContatoRepository contatoRepository;

    @Autowired
    ContatoMapper contatoMapper;

    public abstract UsuarioDTO toDTO(Usuario usuario);

    public abstract Usuario toEntity(UsuarioDTO usuarioDTO);

    @AfterMapping
    void setContato(Usuario usuario, @MappingTarget UsuarioDTO usuarioDTO) {
        Set<ContatoDTO> contatos = contatoRepository.findByUsuario(usuario)
                .stream()
                .map(contato -> contatoMapper.toDTO(contato))
                .collect(Collectors.toSet());

        usuarioDTO.setContatos(contatos);
    }

}
