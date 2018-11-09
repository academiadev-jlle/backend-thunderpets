package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.mapper.UsuarioMapper;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
import br.com.academiadev.thunderpets.util.Util;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

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
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private Util util;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    private Map<String, UUID> mapaUsuarios = new HashMap<String, UUID>();

    @Test
    public void listar() throws Exception {
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(util.convertObjectToJsonBytes(util.criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString();

        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(util.convertObjectToJsonBytes(util.criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(util.convertObjectToJsonBytes(util.criarUsuarioDTOKamuela())))
                .andReturn().getResponse().getContentAsString();

        PageRequest paginacao = PageRequest.of(0, 10, Sort.Direction.ASC, "nome");
        Page<Usuario> paginaUsuarios = usuarioRepository.findAll(paginacao);
        PageImpl<UsuarioDTO> pageImplTest = new PageImpl<UsuarioDTO>(paginaUsuarios.stream()
                .map(usuario -> usuarioMapper.toDTO(usuario)).collect(Collectors.toList()),
                paginacao, (int) paginaUsuarios.getTotalElements());
        JSONObject jsonObj = new JSONObject(new Gson().toJson(pageImplTest));

        ResultActions performGET = mvc.perform(get("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

    }

    @Test
    public void buscar() throws Exception {
        String conteudoRetorno = mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(util.convertObjectToJsonBytes(util.criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString();

        String jekaterina = (String) new JSONObject(conteudoRetorno).get("id");

        ResultActions performGET = mvc.perform(get("/usuario/" + jekaterina))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Jekaterina Souza")))
                .andExpect(jsonPath("$.email", is("jekaterina@gmail.com")))
                .andExpect(jsonPath("$.contatos[0].tipo", is("TELEFONE")))
                .andExpect(jsonPath("$.contatos[0].descricao", is("(47) 3434-3232")))
                .andExpect(jsonPath("$.contatos[1].descricao", is("(47) 98739-6879")));
    }

    @Test
    public void buscarUsuarioNaoEncontrado() throws Exception {
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(util.convertObjectToJsonBytes(util.criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        UUID uuid = UUID.randomUUID();

        ResultActions performGET = mvc.perform(get("/usuario/" + uuid))
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.message", is("Usuário " + uuid + " não encontrado.")));
    }

    @Test
    public void salvar() throws Exception {
        ResultActions performGET = mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(util.convertObjectToJsonBytes(util.criarUsuarioDTOEpaminondas())))
                .andExpect(status().isOk());
    }

    @Test
    public void deletar() throws Exception {
        String conteudoRetorno = mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(util.convertObjectToJsonBytes(util.criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        String epaminondas = (String) new JSONObject(conteudoRetorno).get("id");

        JSONObject jsonObj = new JSONObject(new Gson().toJson(util.criarUsuarioDTOEpaminondas()));

        ResultActions performGET = mvc.perform(delete("/usuario/" + epaminondas))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void deletarUsuarioNaoEncontrado() throws Exception {
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(util.convertObjectToJsonBytes(util.criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        UUID uuid = UUID.randomUUID();

        ResultActions performGET = mvc.perform(delete("/usuario/" + uuid))
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.message", is("Usuário " + uuid + " não encontrado.")));
    }

    @Test
    public void getFoto() {

    }
}
