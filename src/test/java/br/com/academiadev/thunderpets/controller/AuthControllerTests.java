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

    @Test
    public void dadoUsuarioExistenteQuandoSolicitoTokenEntaoSucesso() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", "admin@mail.com");
        params.add("password", "admin");

        ResultActions result = mock.perform(
                post("/oauth/token")
                        .params(params)
                        .accept("application/json;charset=UTF-8")
                        .with(httpBasic(client, secret)))
                .andExpect(status().isOk());

        JacksonJsonParser parser = new JacksonJsonParser();
        String token = parser.parseMap(result
                .andReturn()
                .getResponse()
                .getContentAsString()).get("access_token").toString();

        ResultActions principal = mock.perform(
                get("/oauth/whoAmI")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        principal.andExpect(jsonPath("$.username", is("admin@mail.com")));
    }

    @Test
    public void dadoUsuarioCadastradoQuandoSenhaIncorretaEntaoBadCredentials() throws Exception {
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

        JacksonJsonParser parser = new JacksonJsonParser();
        String error = parser.parseMap(result
                .andReturn()
                .getResponse()
                .getContentAsString()).get("error").toString();

        Assert.assertEquals("invalid_grant", error);
    }

    @Test
    public void dadoUsuarioNaoCadastradoEntaoSemAutorizacao() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", "fake@mail.com");
        params.add("senha", "senhafake");

        ResultActions result = mock.perform(
                post("/oauth/token")
                        .params(params)
                        .accept("application/json;charset=UTF-8")
                        .with(httpBasic(client, secret)))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));

        JacksonJsonParser parser = new JacksonJsonParser();
        String error = parser.parseMap(result
                .andReturn()
                .getResponse()
                .getContentAsString()).get("error").toString();

        Assert.assertEquals("unauthorized", error);
    }
}