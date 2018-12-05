package br.com.academiadev.thunderpets.mapper;


import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.model.Localizacao;
import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.util.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {
        UsuarioUtil.class, UsuarioDTOUtil.class, UsuarioMapper.class, UsuarioMapperImpl.class,
        PetUtil.class, PetDTOUtil.class, PetMapperImpl.class,
        LocalizacaoUtil.class, ContatoUtil.class, FotoUtil.class})
public class PetMapperTest {

    @Autowired
    private UsuarioUtil usuarioUtil;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private PetUtil petUtil;

    @Autowired
    private PetDTOUtil petDTOUtil;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private LocalizacaoUtil localizacaoUtil;

    @Test
    public void dadoPet_quandoMapeioParaPetDTO_entaoRetornaPetDTO(){
        //Dado
        Localizacao localizacao = localizacaoUtil.criaLocalizacaoGaruva();
        Usuario usuario = usuarioUtil.criarUsuarioKamuela();
        Pet pet = petUtil.criaPetBrabo();

        //Quando
        PetDTO petDTO = petMapper.toDTO(pet, null);

        //Então
        Assert.assertEquals(petDTO.getNome(), "Brabo");
        Assert.assertEquals(petDTO.getDescricao(), "Bixo e brabo");
        Assert.assertEquals(petDTO.getDataAchado(), LocalDate.now());
        Assert.assertEquals(petDTO.getEspecie(), Especie.CACHORRO);
        Assert.assertEquals(petDTO.getPorte(), Porte.GRANDE);
        Assert.assertEquals(petDTO.getSexo(), Sexo.MACHO);
        Assert.assertEquals(petDTO.getStatus(), Status.PARA_ADOTAR);
        Assert.assertEquals(petDTO.getIdade(), Idade.ADULTO);
        Assert.assertEquals(petDTO.getUsuarioId(), usuario.getId());
        Assert.assertEquals(petDTO.getLocalizacao(), localizacao);
        Assert.assertTrue(petDTO.isAtivo());
    }

    @Test
    public void dadoPetDTO_quandoMapeioParaPet_entaoRetornaPet(){
        //Dado
        Localizacao localizacao = localizacaoUtil.criaLocalizacaoGaruva();
        Usuario usuario = usuarioUtil.criarUsuarioKamuela();
        PetDTO petDTO = petDTOUtil.criaPetDTOBrabo(usuarioMapper.toDTO(usuario, null));

        //Quando
        Pet pet = petMapper.toEntity(petDTO, localizacao, usuario);

        //Então
        Assert.assertEquals(pet.getNome(), "Brabo");
        Assert.assertEquals(pet.getDescricao(), "Bixo e brabo");
        Assert.assertEquals(pet.getDataAchado(), LocalDate.now());
        Assert.assertEquals(pet.getDataRegistro(), LocalDate.now());
        Assert.assertEquals(pet.getEspecie(), Especie.CACHORRO);
        Assert.assertEquals(pet.getPorte(), Porte.GRANDE);
        Assert.assertEquals(pet.getSexo(), Sexo.MACHO);
        Assert.assertEquals(pet.getStatus(), Status.PARA_ADOTAR);
        Assert.assertEquals(pet.getIdade(), Idade.ADULTO);
        Assert.assertEquals(pet.getUsuario(), usuario);
        Assert.assertEquals(pet.getLocalizacao(), localizacao);
        Assert.assertTrue(pet.isAtivo());
    }

}
