package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.enums.TipoContato;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.PetRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class UsuarioDTOUtil {

    @Autowired
    private ContatoUtil contatoUtil;

    public UsuarioDTO criarUsuarioDTOEpaminondas() {
        Set<ContatoDTO> contatos = new HashSet<>();
        contatos.add(contatoUtil.criarContatoDTOTelefone());

        return UsuarioDTO.builder()
                .nome("Epaminondas Silva")
                .email("epaminondas@gmail.com")
                .senha("epaminondas123")
                .foto(new byte[]{1, 2, 3})
                .ativo(true)
                .contatos(contatos)
                .build();
    }

    public UsuarioDTO criarUsuarioDTOJekaterina() {
        Set<ContatoDTO> contatos = new HashSet<>();
        contatos.add(contatoUtil.criarContatoDTOTelefone());
        contatos.add(contatoUtil.criarContatoDTOCelular());

        return UsuarioDTO.builder()
                .nome("Jekaterina Souza")
                .email("jekaterina@gmail.com")
                .senha("jekaterina123")
                .foto(new byte[]{4, 5, 6})
                .ativo(true)
                .contatos(contatos)
                .build();
    }

    public UsuarioDTO criarUsuarioDTOKamuela() {
        Set<ContatoDTO> contatos = new HashSet<>();
        contatos.add(contatoUtil.criarContatoDTOCelular());

        return UsuarioDTO.builder()
                .nome("Kamuela Pereira")
                .email("kamuela@gmail.com")
                .senha("kamuela123")
                .foto(new byte[]{7, 8, 9})
                .ativo(true)
                .contatos(contatos)
                .build();
    }

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        return mapper.writeValueAsBytes(object);
    }
}
