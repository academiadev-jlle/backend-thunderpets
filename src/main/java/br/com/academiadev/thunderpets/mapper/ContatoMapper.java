package br.com.academiadev.thunderpets.mapper;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContatoMapper {

    @Autowired
    private ContatoRepository contatoRepository;

    public ContatoDTO converterContatoParaContatoDTO(Contato contato) {
        return ContatoDTO.builder()
                .id(contato.getId())
                .tipo(contato.getTipo())
                .descricao(contato.getDescricao())
                .build();
    }

    public Contato converterContatoDTOParaContato(ContatoDTO contatoDTO, Usuario usuario) {
        return Contato.builder()
                .id(contatoDTO.getId())
                .descricao(contatoDTO.getDescricao())
                .tipo(contatoDTO.getTipo())
                .usuario(usuario)
                .build();
    }
}
