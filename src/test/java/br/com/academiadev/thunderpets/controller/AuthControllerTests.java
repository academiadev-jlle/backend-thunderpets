package br.com.academiadev.thunderpets.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthControllerTests {

    @Autowired
    private MockMvc mock;

    @Value("${security.oauth2.client.client-id}")
    private String client;

    @Value("${security.oauth2.client.client-secret}")
    private String secret;

    private JacksonJsonParser parser = new JacksonJsonParser();

    @Test
    public void dadoUsuarioExistente_quandoSolicitoToken_entaoSucesso() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", "admin@mail.com");
        params.add("password", "admin");

        ResultActions login = mock.perform(
                post("/oauth/token")
                        .params(params)
                        .accept("application/json;charset=UTF-8")
                        .with(httpBasic(client, secret)))
                .andExpect(status().isOk());

        String token = parser.parseMap(login
                .andReturn()
                .getResponse()
                .getContentAsString()).get("access_token").toString();

        ResultActions usuario = mock.perform(
                get("/oauth/whoAmI")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        usuario.andExpect(jsonPath("$.email", is("admin@mail.com")));
    }

    @Test
    public void dadoUsuarioLogado_quandoAtualizoToken_entaoRetornaNovoToken() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", "admin@mail.com");
        params.add("password", "admin");

        ResultActions login = mock.perform(
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

        ResultActions refresh = mock.perform(
                post("/oauth/token")
                        .params(params)
                        .accept("application/json;charset=UTF-8")
                        .with(httpBasic(client, secret)))
                .andExpect(status().isOk());

        String token = parser.parseMap(refresh
                .andReturn()
                .getResponse()
                .getContentAsString()).get("access_token").toString();

        ResultActions usuario = mock.perform(
                get("/oauth/whoAmI")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        usuario.andExpect(jsonPath("$.email", is("admin@mail.com")));
    }

    @Test
    public void dadoNenhumaSecao_quandoWhoAmI_entaoUsuarioAnonimo() throws Exception {
        String usuario = mock.perform(get("/oauth/whoAmI"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assert.assertEquals("anonymousUser", usuario);
    }

    @Test
    public void dadoUsuarioCadastrado_quandoSenhaIncorreta_entaoBadCredentials() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", "admin@mail.com");
        params.add("senha", "senhaincorreta");

        ResultActions result = mock.perform(
                post("/oauth/token")
                        .params(params)
                        .accept("application/json;charset=UTF-8")
                        .with(httpBasic(client, secret)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        String error = parser.parseMap(result
                .andReturn()
                .getResponse()
                .getContentAsString()).get("error").toString();

        Assert.assertEquals("invalid_grant", error);
    }

    @Test
    public void dadoUsuarioLogado_quandoFazLogout_entaoSucesso() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", "admin@mail.com");
        params.add("password", "admin");

        ResultActions login = mock.perform(
                post("/oauth/token")
                        .params(params)
                        .accept("application/json;charset=UTF-8")
                        .with(httpBasic(client, secret)))
                .andExpect(status().isOk());

        String token = parser.parseMap(login
                .andReturn()
                .getResponse()
                .getContentAsString()).get("access_token").toString();

        ResultActions logout = mock.perform(
                get("/oauth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());


        Assert.assertEquals("true", logout.andReturn().getResponse().getContentAsString());
    }

    @Test
    public void dadoSemUsuario_quandoFazLogout_entaoErro() throws Exception {
        mock.perform(get("/oauth/logout")).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }
}
