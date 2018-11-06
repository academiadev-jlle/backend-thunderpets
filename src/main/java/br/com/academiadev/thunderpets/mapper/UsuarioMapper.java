package br.com.academiadev.thunderpets.mapper;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    @Autowired
    private ContatoRepository contatoRepository;
    @Autowired
    private ContatoMapper contatoMapper;

    public UsuarioDTO converterUsuarioParaUsuarioDTO(Usuario usuario) {
        UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .senha(usuario.getSenha())
                .foto(usuario.getFoto())
                .ativo(usuario.isAtivo())
                .build();

        List<Contato> contatosDoUsuario = contatoRepository.findByUsuario(usuario);
        Set<ContatoDTO> contatos = contatosDoUsuario.stream()
                .map(contato -> contatoMapper.converterContatoParaContatoDTO(contato))
                .collect(Collectors.toSet());

        usuarioDTO.setContatos(contatos);

        return usuarioDTO;
    }

    public Usuario converterUsuarioDTOParaUsuario(UsuarioDTO usuarioDTO) {
        return Usuario.builder()
                .id(usuarioDTO.getId())
                .nome(usuarioDTO.getNome())
                .email(usuarioDTO.getEmail())
                .senha(usuarioDTO.getSenha())
                .foto(usuarioDTO.getFoto())
                .ativo(usuarioDTO.isAtivo())
                .build();
    }
}
