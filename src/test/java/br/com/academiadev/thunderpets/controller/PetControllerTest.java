package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.dto.UsuarioRespostaDTO;
import br.com.academiadev.thunderpets.repository.PetRepository;
import br.com.academiadev.thunderpets.util.PetDTOUtil;
import br.com.academiadev.thunderpets.util.UsuarioDTOUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class PetControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UsuarioDTOUtil usuarioDTOUtil;

    @Autowired
    private PetDTOUtil petDTOUtil;

    @Value("${security.oauth2.client.client-id}")
    private String client;

    @Value("${security.oauth2.client.client-secret}")
    private String secret;

    private ObjectMapper objectMapper = new ObjectMapper();
    private JacksonJsonParser parser = new JacksonJsonParser();
    private String token = "";

    @Test
    public void dadoPetDTO_quandoSalvo_entaoRetornaSucesso() throws Exception {
        getAuthHeader();

        UsuarioRespostaDTO usuario = objectMapper.readValue(mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString(), UsuarioRespostaDTO.class);

        //Dado
         PetDTO petDTO = petDTOUtil.criaPetDTOBrabo(usuario);

        //Quando
        ResultActions petSalvo = mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(petDTO)));

        //Então
        petSalvo.andExpect(status().isOk());
    }

    @Test
    public void dadoIdPetInexistente_quandoBuscoPorId_entaoRetornaErro() throws Exception{
        getAuthHeader();

        UUID uuid = UUID.randomUUID();

        mvc.perform(MockMvcRequestBuilders.get("/pet/" + uuid.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void dadoIdPetInexistente_quandoDeleto_entaoRetornaErro() throws Exception{
        getAuthHeader();

        UUID uuid = UUID.randomUUID();

        mvc.perform(MockMvcRequestBuilders.delete("/pet/" + uuid.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void dadoPetDTO_quandoSalvoSemAutenticao_entaoRetornaErro() throws Exception{

        UsuarioRespostaDTO usuario = objectMapper.readValue(mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString(), UsuarioRespostaDTO.class);

        //Dado
        PetDTO petDTO = petDTOUtil.criaPetDTOBrabo(usuario);

        //Quando
        ResultActions petSalvo = mvc.perform(post("/pet")).andExpect((status().isUnauthorized()));

    }

    @Test
    public void dadoPetExistente_quandoBuscoPorId_entaoRetornaSucesso() throws Exception {
        getAuthHeader();

        UsuarioRespostaDTO usuario = objectMapper.readValue(mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString(), UsuarioRespostaDTO.class);

        //Dado
        PetDTO petDTO = petDTOUtil.criaPetDTOBrabo(usuario);

        String conteudoRetorno = mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(petDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONObject pet = new JSONObject(conteudoRetorno);

        ResultActions response = mvc.perform(MockMvcRequestBuilders.get("/pet/" + pet.get("id")))
                .andExpect(status().isOk());

        JSONObject petResposta = new JSONObject(response.andReturn().getResponse().getContentAsString());

        Assert.assertEquals(petResposta.get("id"), pet.get("id"));
        Assert.assertEquals(petResposta.get("nome"), pet.get("nome"));
        Assert.assertEquals(petResposta.get("descricao"), pet.get("descricao"));
    }

    @Test
    public void dadoPetExistente_quandoDeletaPet_entaoOk() throws Exception {
        getAuthHeader();

        UsuarioDTO usuarioDTO = usuarioDTOUtil.criarUsuarioDTOJekaterina();
        usuarioDTO.setFoto(null);

        UsuarioRespostaDTO usuario = objectMapper.readValue(mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(usuarioDTO)))
                .andReturn().getResponse().getContentAsString(), UsuarioRespostaDTO.class);

        //Dado
        PetDTO petDTO = petDTOUtil.criaPetDTOBrabo(usuario);

        String conteudoRetorno = mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(petDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String idPet = (String) new JSONObject(conteudoRetorno).get("id");

        //Quando
        mvc.perform(MockMvcRequestBuilders.delete("/pet/" + idPet))
                .andExpect(status().isOk());
    }

    @Test
    public void dadoPets_quandoBusco2PorNomeDESC_entaoBuscar2PorNomeDESC() throws Exception {
        getAuthHeader();

        UsuarioRespostaDTO usuario = objectMapper.readValue(mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString(), UsuarioRespostaDTO.class);

        mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(petDTOUtil.criaPetDTOCarijo(usuario))));

        mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(petDTOUtil.criaPetDTOBrabo(usuario))));

        mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(petDTOUtil.criaPetDTOPocoto(usuario))));

        //Quando
        ResultActions listaDePets = mvc.perform(get("/pet")
                .param("paginaAtual", "0")
                .param("tamanho", "2")
                .param("direcao", "DESC")
                .param("campoOrdenacao", "nome"));

        //Entao
        listaDePets.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome", is("Pocotó")))
                .andExpect(jsonPath("$.content[1].nome", is("Carijó")));
    }

    @Test
    public void dadoPets_quandoBuscoPorCidadeJoinvilleOrdenacaoPadrao_entaoBuscarPetsCidadeJoinvilleOrdenacaoPadrao()
            throws Exception {
        getAuthHeader();

        UsuarioRespostaDTO usuario = objectMapper.readValue(mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString(), UsuarioRespostaDTO.class);

        mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(petDTOUtil.criaPetDTOCarijo(usuario))));

        mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(petDTOUtil.criaPetDTOBrabo(usuario))));

        mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(petDTOUtil.criaPetDTOPocoto(usuario))));

        //Quando
        ResultActions listaDePets = mvc.perform(get("/pet")
                .param("buscarPorLocalidade", "CIDADE_ESTADO")
                .param("cidade", "JOINVILLE"));

        //Entao
        listaDePets.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome", is("Pocotó")))
                .andExpect(jsonPath("$.content[0].localizacao.cidade", is("Joinville")))
                .andExpect(jsonPath("$.content[0].localizacao.estado", is("Blumenal")))
                .andExpect(jsonPath("$.content[1].nome", is("Carijó")))
                .andExpect(jsonPath("$.content[1].localizacao.cidade", is("Joinville")))
                .andExpect(jsonPath("$.content[1].localizacao.estado", is("Santa Catarina")));
    }

    @Test
    public void dadoPets_quandoBuscoPorRaioDeDistancia3OrdenacaoNomeASC_entaoBuscarPetsRaioDeDistancia3OrdenacaoNomeASC()
            throws Exception {
        getAuthHeader();

        UsuarioRespostaDTO usuario = objectMapper.readValue(mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(usuarioDTOUtil.criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString(), UsuarioRespostaDTO.class);

        mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(petDTOUtil.criaPetDTOCarijo(usuario))));

        mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(petDTOUtil.criaPetDTOBrabo(usuario))));

        mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UsuarioDTOUtil.convertObjectToJsonBytes(petDTOUtil.criaPetDTOPocoto(usuario))));

        //Quando
        ResultActions listaDePets = mvc.perform(get("/pet")
                .param("buscarPorLocalidade", "RAIO_DISTANCIA")
                .param("latitudeUsuario", "-26.301245")
                .param("longitudeUsuario", "-48.844594")
                .param("raioDistancia", "3")
                .param("direcao", "ASC")
                .param("campoOrdenacao", "nome"));

        //Entao
        listaDePets.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome", is("Carijó")))
                .andExpect(jsonPath("$.content[0].localizacao.descricao", is("R. Dona Francisca - Centro, Joinville - SC, 89201-070")))
                .andExpect(jsonPath("$.content[1].nome", is("Pocotó")))
                .andExpect(jsonPath("$.content[1].localizacao.descricao", is("R. Carlos Gruensch, 2-260 - América, Joinville - SC, 89201-745")));

        JSONObject jsonListaDePets = new JSONObject(listaDePets.andReturn().getResponse().getContentAsString());
        JSONObject jsonPetCarijo = (JSONObject) ((JSONArray) jsonListaDePets.get("content")).get(0);
        JSONObject jsonPetPocoto = (JSONObject) ((JSONArray) jsonListaDePets.get("content")).get(1);

        BigDecimal distanciaPetCarijo = (new BigDecimal((Double) jsonPetCarijo.get("distancia"))).setScale(2);
        BigDecimal distanciaPetPocoto = (new BigDecimal((Double) jsonPetPocoto.get("distancia"))).setScale(2, RoundingMode.HALF_UP);

        Assert.assertEquals(new BigDecimal("0.00"), distanciaPetCarijo);
        Assert.assertEquals(new BigDecimal("1.26"), distanciaPetPocoto);
    }

    private void getAuthHeader() throws Exception {
        if (token.isEmpty()) {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "password");
            params.add("username", "admin@mail.com");
            params.add("password", "admin");

            ResultActions login = mvc.perform(
                    post("/oauth/token")
                            .params(params)
                            .accept("application/json;charset=UTF-8")
                            .with(httpBasic(client, secret)))
                    .andExpect(status().isOk());

            String refreshToken = parser.parseMap(login
                    .andReturn()
                    .getResponse()
                    .getContentAsString()).get("refresh_token").toString();

            params.clear();
            params.add("grant_type", "refresh_token");
            params.add("refresh_token", refreshToken);

            ResultActions refresh = mvc.perform(
                    post("/oauth/token")
                            .params(params)
                            .accept("application/json;charset=UTF-8")
                            .with(httpBasic(client, secret)))
                    .andExpect(status().isOk());

            String token = parser.parseMap(refresh
                    .andReturn()
                    .getResponse()
                    .getContentAsString()).get("access_token").toString();

            this.token = String.format("Bearer %s", token);
        }
    }
}
