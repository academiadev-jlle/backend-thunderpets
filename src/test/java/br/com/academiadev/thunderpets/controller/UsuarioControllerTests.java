package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.util.UsuarioDTOUtil;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.transaction.Transactional;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class UsuarioControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UsuarioDTOUtil usuarioDTOUtil;

    @Test
    public void dadoUsuarios_quandoListo10PorNomeASC_entaoListar10PorNomeASC() throws Exception {
        //Dado
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString();

        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOKamuela())))
                .andReturn().getResponse().getContentAsString();

        //Quando
        ResultActions listaDeUsuarios = mvc.perform(get("/usuario"));

        //Entao
        listaDeUsuarios.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[1].nome", is("Epaminondas Silva")))
                .andExpect(jsonPath("$.content[2].nome", is("Jekaterina Souza")))
                .andExpect(jsonPath("$.content[3].nome", is("Kamuela Pereira")));
    }

    @Test
    public void dadoUsuarios_quandoListo2PorEmailDESC_entaoListar3PorEmailDESC() throws Exception {
        //Dado
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString();

        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOKamuela())))
                .andReturn().getResponse().getContentAsString();

        //Quando
        ResultActions listaDeUsuarios = mvc.perform(get("/usuario")
                .param("paginaAtual", "0")
                .param("tamanho", "3")
                .param("direcao", "DESC")
                .param("campoOrdenacao", "email"));

        //Entao
        listaDeUsuarios.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email", is("kamuela@gmail.com")))
                .andExpect(jsonPath("$.content[1].email", is("jekaterina@gmail.com")));
    }

    @Test
    public void dadoUsuario_quandoBuscoPorIdDoUsuario_entaoUsuario() throws Exception {
        //Dado
        String conteudoRetorno = mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOKamuela())))
                .andReturn().getResponse().getContentAsString();

        String idJekaterina = (String) new JSONObject(conteudoRetorno).get("id");

        //Quando
        ResultActions usuario = mvc.perform(get("/usuario/" + idJekaterina));

        //Entao
        usuario.andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Kamuela Pereira")))
                .andExpect(jsonPath("$.email", is("kamuela@gmail.com")))
                .andExpect(jsonPath("$.contatos[0].tipo", is("CELULAR")))
                .andExpect(jsonPath("$.contatos[0].descricao", is("(47) 98739-6879")));
    }

    @Test
    public void dadoUsuario_quandoBuscoPorIdInexistente_entaoErro404UsuarioNaoEncontrado() throws Exception {
        //Dado
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        UUID uuid = UUID.randomUUID();

        //Quando
        ResultActions usuarioNaoEncontrado = mvc.perform(get("/usuario/" + uuid));

        //Entao
        usuarioNaoEncontrado.andExpect(status().is(404))
                .andExpect(jsonPath("$.message", is("Usuário " + uuid + " não encontrado.")));
    }

    @Test
    public void dadoUsuario_quandoSalvo_entaoSucesso() throws Exception {
        //Dado
        UsuarioDTO usuarioDTO = usuarioDTOUtil.criarUsuarioDTOEpaminondas();

        //Quando
        ResultActions usuarioNaoEncontrado = mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTO)));

        //Entao
        usuarioNaoEncontrado.andExpect(status().isOk());
    }

    @Test
    public void dadoUsuario_quandoDeleto_entaoSucesso() throws Exception {
        //Dado
        String conteudoRetorno = mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        String idEpaminondas = (String) new JSONObject(conteudoRetorno).get("id");

        //Quando
        ResultActions delete = mvc.perform(delete("/usuario/" + idEpaminondas));

        //Entao
        delete.andExpect(status().isOk());
    }

    @Test
    public void dadoUsuarioInexistente_quandoDeleto_entaoErro404UsuarioNaoEncontrado() throws Exception {
        //Dado
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        UUID uuid = UUID.randomUUID();

        //Quando
        ResultActions deleteUsuarioNaoEncontrado = mvc.perform(delete("/usuario/" + uuid));

        //Entao
        deleteUsuarioNaoEncontrado.andExpect(status().is(404))
                .andExpect(jsonPath("$.message", is("Usuário " + uuid + " não encontrado.")));
    }

    @Test
    public void dadoUsuarioComFoto_quandoSolicitoFoto_entaoFoto() throws Exception {
        //Dado
        String conteudoRetorno = mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        String idEpaminondas = (String) new JSONObject(conteudoRetorno).get("id");

        //Quando
        ResultActions listaDeUsuarios = mvc.perform(get("/usuario/" + idEpaminondas + "/foto"));
        MockHttpServletResponse retorno = listaDeUsuarios.andReturn().getResponse();

        //Entao
        listaDeUsuarios.andExpect(content().contentType(MediaType.IMAGE_JPEG));
        assertThat(retorno.getContentAsByteArray()).isEqualTo(new byte[]{1, 2, 3});
    }
}
