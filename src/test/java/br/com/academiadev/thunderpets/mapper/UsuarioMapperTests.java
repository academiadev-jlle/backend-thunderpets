package br.com.academiadev.thunderpets.mapper;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
import br.com.academiadev.thunderpets.util.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { UsuarioMapperImpl.class, Util.class, ContatoMapperImpl.class })
public class UsuarioMapperTests {

    @Autowired
    private Util util;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private ContatoRepository contatoRepository;

    @Test
    public void dadoDTO_quandoMapeio_entaoEntity() {
        //Dado
        UsuarioDTO usuarioDTO = util.criarUsuarioDTOEpaminondas();

        //Quando
        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);

        //Entao
        Assert.assertEquals(usuario.getNome(), "Epaminondas Silva");
        Assert.assertEquals(usuario.getEmail(), "epaminondas@gmail.com");
        Assert.assertEquals(Arrays.toString(usuario.getFoto()), Arrays.toString(new byte[]{1, 2, 3}));
        Assert.assertTrue(usuario.isAtivo());
    }

    @Test
    public void dadoEntity_quandoMapeio_entaoDTO() {
        //Dado
        Usuario usuario = util.criarUsuarioKamuela();

        Contato contato1 = util.criarContatoEmail();
        contato1.setUsuario(usuario);

        Contato contato2 = util.criarContatoRedeSocial();
        contato2.setUsuario(usuario);

        Set<ContatoDTO> contatosEsperados = new HashSet<>();
        contatosEsperados.add(new ContatoDTO(contato1.getId(), contato1.getTipo(), contato1.getDescricao()));
        contatosEsperados.add(new ContatoDTO(contato2.getId(), contato2.getTipo(), contato2.getDescricao()));

        //Quando
        UsuarioDTO usuarioDTO = usuarioMapper.toDTO(
                usuario,
                contatosEsperados.stream().map(c -> contatoMapper.toEntity(c, usuario)).collect(Collectors.toSet()));

        //Entao
        Assert.assertEquals(usuarioDTO.getNome(), "Kamuela Pereira");
        Assert.assertEquals(usuarioDTO.getEmail(), "kamuela@gmail.com");
        Assert.assertEquals(Arrays.toString(usuarioDTO.getFoto()), Arrays.toString(new byte[]{7, 8, 9}));
        Assert.assertTrue(usuarioDTO.isAtivo());
        Assert.assertEquals(usuarioDTO.getContatos(), contatosEsperados);
    }
}

