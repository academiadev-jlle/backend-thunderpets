package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.repository.PetRepository;
import br.com.academiadev.thunderpets.util.*;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @Test
    public void dadoPetDTO_quandoSalvo_entaoRetornaSucesso() throws Exception {

        //Dado
         PetDTO petDTO = petDTOUtil.criaPetDTOBrabo();

        //Quando
        ResultActions petSalvo = mvc.perform(post("/pet")
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(Util.convertObjectToJsonBytes(petDTO)));

        //Então
        petSalvo.andExpect(status().isOk());
    }

    @Test
    public void dadoPet_quandoDeleto_entaoRetornaSucesso() throws Exception {

        //Dado
        PetDTO petDTO = petDTOUtil.criaPetDTOBrabo();

        String conteudoRetorno = mvc.perform(post("/pet")
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(Util.convertObjectToJsonBytes(petDTO)))
            .andReturn()
            .getResponse()
            .getContentAsString();

        String idPet = (String) new JSONObject(conteudoRetorno).get("id");

        //Quando
        ResultActions delete = mvc.perform(MockMvcRequestBuilders.delete("/pet" + idPet));

        //Então

        delete.andExpect(status().isOk()).andExpect(content().string("true"));
    }

    @Test
    public void dadoPet_quandoBuscoPorIdPet_entaoPet() throws Exception {

        PetDTO petDTO = petDTOUtil.criaPetDTOBrabo();

        String conteudoRetorno = mvc.perform(post("/pet")
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(Util.convertObjectToJsonBytes(petDTO)))
            .andReturn()
            .getResponse()
            .getContentAsString();

        String idPet = (String) new JSONObject(conteudoRetorno).get("id");

        //Quando
        ResultActions delete = mvc.perform(MockMvcRequestBuilders.delete("/pet" + idPet));
    }


}
