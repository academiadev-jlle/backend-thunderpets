package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.enums.TipoContato;
import br.com.academiadev.thunderpets.model.Contato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContatoUtil {

    @Autowired
    private UsuarioUtil usuarioUtil;

    public ContatoDTO criarContatoDTOTelefone() {
        return ContatoDTO.builder()
                .tipo(TipoContato.TELEFONE)
                .descricao("(47) 3434-3232")
                .build();
    }

    public ContatoDTO criarContatoDTOCelular() {
        return ContatoDTO.builder()
                .tipo(TipoContato.CELULAR)
                .descricao("(47) 98739-6879")
                .build();
    }

    public Contato criarContatoEmail() {
        return Contato.builder()
                .tipo(TipoContato.EMAIL)
                .usuario(usuarioUtil.criarUsuarioKamuela())
                .descricao("thunderpets.dev@gmail.com")
                .build();
    }

    public Contato criarContatoRedeSocial() {
        return Contato.builder()
                .tipo(TipoContato.REDE_SOCIAL)
                .usuario(usuarioUtil.criarUsuarioKamuela())
                .descricao("https://www.facebook.com/thunderpets")
                .build();
    }
}
