package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.dto.UsuarioRespostaDTO;
import br.com.academiadev.thunderpets.repository.PetRepository;
import br.com.academiadev.thunderpets.util.PetDTOUtil;
import br.com.academiadev.thunderpets.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private Util util;

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
                .content(Util.convertObjectToJsonBytes(util.criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString(), UsuarioRespostaDTO.class);

        //Dado
         PetDTO petDTO = petDTOUtil.criaPetDTOBrabo(usuario);

        //Quando
        ResultActions petSalvo = mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(Util.convertObjectToJsonBytes(petDTO)));

        //Então
        petSalvo.andExpect(status().isOk());
    }

    @Test
    public void dadoPetExistente_quandoBuscoPorId_entaoRetornaSucesso() throws Exception {
        getAuthHeader();

        UsuarioRespostaDTO usuario = objectMapper.readValue(mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(Util.convertObjectToJsonBytes(util.criarUsuarioDTOJekaterina())))
                .andReturn().getResponse().getContentAsString(), UsuarioRespostaDTO.class);

        //Dado
        PetDTO petDTO = petDTOUtil.criaPetDTOBrabo(usuario);

        String conteudoRetorno = mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(Util.convertObjectToJsonBytes(petDTO)))
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

        UsuarioDTO usuarioDTO = util.criarUsuarioDTOJekaterina();
        usuarioDTO.setFoto(null);

        UsuarioRespostaDTO usuario = objectMapper.readValue(mvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(Util.convertObjectToJsonBytes(usuarioDTO)))
                .andReturn().getResponse().getContentAsString(), UsuarioRespostaDTO.class);

        //Dado
        PetDTO petDTO = petDTOUtil.criaPetDTOBrabo(usuario);

        String conteudoRetorno = mvc.perform(post("/pet")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(Util.convertObjectToJsonBytes(petDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String idPet = (String) new JSONObject(conteudoRetorno).get("id");

        //Quando
        mvc.perform(MockMvcRequestBuilders.delete("/pet/" + idPet))
                .andExpect(status().isOk());
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
