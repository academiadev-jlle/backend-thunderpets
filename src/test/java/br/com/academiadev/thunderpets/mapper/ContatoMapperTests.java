package br.com.academiadev.thunderpets.mapper;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.enums.TipoContato;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.util.ContatoUtil;
import br.com.academiadev.thunderpets.util.UsuarioDTOUtil;
import br.com.academiadev.thunderpets.util.UsuarioUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ContatoMapperTests {

    @Autowired
    private UsuarioDTOUtil usuarioDTOUtil;

    @Autowired
    private UsuarioUtil usuarioUtil;

    @Autowired
    private ContatoUtil contatoUtil;

    @Autowired
    private ContatoMapper contatoMapper;

    @Test
    public void dadoDTO_quandoMapeio_entaoEntity() {
        //Dado
        ContatoDTO contatoDTO = contatoUtil.criarContatoDTOCelular();
        Usuario usuario = usuarioUtil.criarUsuarioKamuela();

        //Quando
        Contato contato = contatoMapper.toEntity(contatoDTO, usuario);

        //Entao
        Assert.assertEquals(contato.getTipo(), TipoContato.CELULAR);
        Assert.assertEquals(contato.getUsuario(), usuario);
        Assert.assertEquals(contato.getDescricao(), "(47) 98739-6879");
    }

    @Test
    public void dadoEntity_quandoMapeio_entaoDTO() {
        //Dado
        Contato contato = contatoUtil.criarContatoRedeSocial();

        //Quando
        ContatoDTO contatoDTO = contatoMapper.toDTO(contato);

        //Entao
        Assert.assertEquals(contatoDTO.getTipo(), TipoContato.REDE_SOCIAL);
        Assert.assertEquals(contatoDTO.getDescricao(), "https://www.facebook.com/thunderpets");
    }

}
