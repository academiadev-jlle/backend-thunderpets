package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.model.RecuperarSenha;
import br.com.academiadev.thunderpets.repository.RecuperarSenhaRepository;
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

    @Autowired
    private RecuperarSenhaRepository recuperarSenhaRepository;

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
                .andExpect(jsonPath("$.content[0].nome", is("Epaminondas Silva")))
                .andExpect(jsonPath("$.content[1].nome", is("Jekaterina Souza")))
                .andExpect(jsonPath("$.content[2].nome", is("Kamuela Pereira")));
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
                .andExpect(jsonPath("$.content[0].email", is("kamuela@mail.com")))
                .andExpect(jsonPath("$.content[1].email", is("jekaterina@mail.com")));
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
                .andExpect(jsonPath("$.email", is("kamuela@mail.com")))
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

    @Test
    public void dadoEmailValido_quandoEsqueciMinhaSenha_entaoEnviaEmailDeRedefinicaoDeSenha() throws Exception {
        //Dado
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())));

        //Quando
        ResultActions enviaEmail = mvc.perform(get("/usuario/esqueci-minha-senha")
                .param("email", "epaminondas@mail.com"));

        //Entao
        enviaEmail.andExpect(status().isOk())
                .andExpect(jsonPath("$", is("E-mail enviado com sucesso.")));
    }

    @Test
    public void dadoEmailInvalido_quandoEsqueciMinhaSenha_entaoErro404UsuarioNaoEncontrado() throws Exception {
        //Dado
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())));

        //Quando
        ResultActions enviaEmail = mvc.perform(get("/usuario/esqueci-minha-senha")
                .param("email", "emailinvalido@mail.com"));

        //Entao
        enviaEmail.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Não há usuário com o e-mail emailinvalido@mail.com cadastrado na plataforma.")));
    }

    @Test
    public void dadoEmailTokenSenhaValidos_quandoRedefinoSenha_entaoSenhaAlteradaComSucesso() throws Exception {
        //Dado
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())));

        mvc.perform(get("/usuario/esqueci-minha-senha")
                .param("email", "epaminondas@mail.com"));

        RecuperarSenha recuperarSenha = recuperarSenhaRepository.findAll().get(0);

        //Quando
        ResultActions enviaEmail = mvc.perform(get("/usuario/redefinir-senha")
                .param("email", "epaminondas@mail.com")
                .param("token", String.valueOf(recuperarSenha.getId()))
                .param("senha", "novasenha"));

        //Entao
        enviaEmail.andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Senha alterada com sucesso.")));
    }

    @Test
    public void dadoTokenInexistente_quandoRedefinoSenha_entaoErro404TokenNaoEncontrado() throws Exception {
        //Dado
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())));

        mvc.perform(get("/usuario/esqueci-minha-senha")
                .param("email", "epaminondas@mail.com"));

        UUID token = UUID.randomUUID();

        //Quando
        ResultActions enviaEmail = mvc.perform(get("/usuario/redefinir-senha")
                .param("email", "epaminondas@mail.com")
                .param("token", String.valueOf(token))
                .param("senha", "novasenha"));

        //Entao
        enviaEmail.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format("Token %s de recuperação de senha não encontrado.", token))));
    }

    @Test
    public void dadoTokenInativo_quandoRedefinoSenha_entaoErro400TokenInvalidoInativo() throws Exception {
        //Dado
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())));

        mvc.perform(get("/usuario/esqueci-minha-senha")
                .param("email", "epaminondas@mail.com"));

        RecuperarSenha recuperarSenha = recuperarSenhaRepository.findAll().get(0);
        recuperarSenha.setAtivo(false);
        recuperarSenhaRepository.saveAndFlush(recuperarSenha);

        //Quando
        ResultActions enviaEmail = mvc.perform(get("/usuario/redefinir-senha")
                .param("email", "epaminondas@mail.com")
                .param("token", String.valueOf(recuperarSenha.getId()))
                .param("senha", "novasenha"));

        //Entao
        enviaEmail.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("O token não é válido, ele encontra-se inativo pois já foi utilizado.")));
    }

    @Test
    public void dadoTokenComMaisDeDuasHoras_quandoRedefinoSenha_entaoErro400TokenInvalidoSolicitadoHaMaisDeDuasHoras()
            throws Exception {
        //Dado
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())));

        mvc.perform(get("/usuario/esqueci-minha-senha")
                .param("email", "epaminondas@mail.com"));

        RecuperarSenha recuperarSenha = recuperarSenhaRepository.findAll().get(0);
        recuperarSenha.setCreatedAt(recuperarSenha.getCreatedAt().plusHours(-3));
        recuperarSenhaRepository.saveAndFlush(recuperarSenha);

        //Quando
        ResultActions enviaEmail = mvc.perform(get("/usuario/redefinir-senha")
                .param("email", "epaminondas@mail.com")
                .param("token", String.valueOf(recuperarSenha.getId()))
                .param("senha", "novasenha"));

        //Entao
        enviaEmail.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("O token não é válido, ele foi solicitado há mais de 2 horas.")));
    }

    @Test
    public void dadoEmailInvalido_quandoRedefinoSenha_entaoErro404UsuarioNaoEncontrado() throws Exception {
        //Dado
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())));

        mvc.perform(get("/usuario/esqueci-minha-senha")
                .param("email", "epaminondas@mail.com"));

        RecuperarSenha recuperarSenha = recuperarSenhaRepository.findAll().get(0);
        //Quando
        ResultActions enviaEmail = mvc.perform(get("/usuario/redefinir-senha")
                .param("email", "emailinvalido@mail.com")
                .param("token", String.valueOf(recuperarSenha.getId()))
                .param("senha", "novasenha"));

        //Entao
        enviaEmail.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Não há usuário com o e-mail emailinvalido@mail.com cadastrado na plataforma.")));
    }

    @Test
    public void dadoEmailDeOutroUsuario_quandoRedefinoSenha_entaoErro400EmaileUsuarioTokenDiferentes() throws Exception {
        //Dado
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOEpaminondas())));

        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(usuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOJekaterina())));

        mvc.perform(get("/usuario/esqueci-minha-senha")
                .param("email", "epaminondas@mail.com"));

        RecuperarSenha recuperarSenha = recuperarSenhaRepository.findAll().get(0);
        //Quando
        ResultActions enviaEmail = mvc.perform(get("/usuario/redefinir-senha")
                .param("email", "jekaterina@mail.com")
                .param("token", String.valueOf(recuperarSenha.getId()))
                .param("senha", "novasenha"));

        //Entao
        enviaEmail.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("O token de recuperação de senha não é referente ao usuário do e-mail.")));
    }
}
