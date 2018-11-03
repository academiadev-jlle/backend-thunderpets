package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.enums.TipoContato;
import br.com.academiadev.thunderpets.mapper.UsuarioMapper;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ContatoRepository contatoRepository;
    @Autowired
    private UsuarioMapper usuarioMapper;

    private Map<String, UUID> mapaUsuarios = new HashMap<String, UUID>();

    @Before
    public void antes() {
        // Apagar todos os registros do db para não interferir nos testes
        contatoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    public void listar() throws Exception {
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(convertObjectToJsonBytes(criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString();

        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(convertObjectToJsonBytes(criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(convertObjectToJsonBytes(criarUsuarioDTOKamuela())))
                .andReturn().getResponse().getContentAsString();

        PageRequest paginacao = PageRequest.of(0, 10, Sort.Direction.ASC, "nome");
        Page<Usuario> paginaUsuarios = usuarioRepository.findAll(paginacao);
        PageImpl<UsuarioDTO> pageImplTest = new PageImpl<UsuarioDTO>(paginaUsuarios.stream()
                .map(usuario -> usuarioMapper.converterUsuarioParaUsuarioDTO(usuario)).collect(Collectors.toList()),
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
                .content(convertObjectToJsonBytes(criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        String epaminondas = (String) new JSONObject(conteudoRetorno).get("id");

        JSONObject jsonObj = new JSONObject(new Gson().toJson(criarUsuarioDTOEpaminondas()));

        ResultActions performGET = mvc.perform(get("/usuario/" + epaminondas))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonObj.toString()));
    }

    @Test
    public void buscarUsuarioNaoEncontrado() throws Exception {
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(convertObjectToJsonBytes(criarUsuarioDTOEpaminondas())))
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
                .content(convertObjectToJsonBytes(criarUsuarioDTOEpaminondas())))
                .andExpect(status().isOk());
    }

    @Test
    public void deletar() throws Exception {
        String conteudoRetorno = mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(convertObjectToJsonBytes(criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        String epaminondas = (String) new JSONObject(conteudoRetorno).get("id");

        JSONObject jsonObj = new JSONObject(new Gson().toJson(criarUsuarioDTOEpaminondas()));

        ResultActions performGET = mvc.perform(delete("/usuario/" + epaminondas))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void deletarUsuarioNaoEncontrado() throws Exception {
        mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(convertObjectToJsonBytes(criarUsuarioDTOEpaminondas())))
                .andReturn().getResponse().getContentAsString();

        UUID uuid = UUID.randomUUID();

        ResultActions performGET = mvc.perform(delete("/usuario/" + uuid))
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.message", is("Usuário " + uuid + " não encontrado.")));
    }

    @Test
    public void getFoto() {

    }

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        return mapper.writeValueAsBytes(object);
    }

    public UsuarioDTO criarUsuarioDTOEpaminondas() {
        ContatoDTO contatoDTO = ContatoDTO.builder()
                .tipo(TipoContato.TELEFONE)
                .descricao("(47) 3434-3232")
                .build();
        ContatoDTO contatoDTO1 = ContatoDTO.builder()
                .tipo(TipoContato.CELULAR)
                .descricao("(47) 98739-6879")
                .build();

        Set<ContatoDTO> contatos = new HashSet<>();
        contatos.add(contatoDTO);
        contatos.add(contatoDTO1);

        UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .nome("Epaminondas Silva")
                .email("epaminondas@gmail.com")
                .senha("epaminondas123")
                .foto(null)
                .ativo(true)
                .contatos(contatos)
                .build();

        return usuarioDTO;
    }

    public UsuarioDTO criarUsuarioDTOJekaterina() {
        ContatoDTO contatoDTO = ContatoDTO.builder()
                .tipo(TipoContato.EMAIL)
                .descricao("jekaterina.contato@gmail.com")
                .build();
        ContatoDTO contatoDTO1 = ContatoDTO.builder()
                .tipo(TipoContato.CELULAR)
                .descricao("(47) 98739-6879")
                .build();

        Set<ContatoDTO> contatos = new HashSet<>();
        contatos.add(contatoDTO);
        contatos.add(contatoDTO1);

        UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .nome("Jekaterina Souza")
                .email("jekaterina@gmail.com")
                .senha("jekaterina123")
                .foto(null)
                .ativo(true)
                .contatos(contatos)
                .build();

        return usuarioDTO;
    }

    public UsuarioDTO criarUsuarioDTOKamuela() {
        ContatoDTO contatoDTO = ContatoDTO.builder()
                .tipo(TipoContato.REDE_SOCIAL)
                .descricao("https://www.facebook.com/kamuela.pereira")
                .build();
        ContatoDTO contatoDTO1 = ContatoDTO.builder()
                .tipo(TipoContato.CELULAR)
                .descricao("(47) 98739-6879")
                .build();

        Set<ContatoDTO> contatos = new HashSet<>();
        contatos.add(contatoDTO);
        contatos.add(contatoDTO1);

        UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .nome("Kamuela Pereira")
                .email("kamuela@gmail.com")
                .senha("kamuela123")
                .foto(null)
                .ativo(true)
                .contatos(contatos)
                .build();

        return usuarioDTO;
    }
}
