package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.model.Localizacao;
import br.com.academiadev.thunderpets.model.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PetUtil {

//    @Autowired
//    LocalizacaoUtil localizacaoUtil;
//
//    @Autowired
//    Util util;

    public Pet criaPetBrabo(){
        LocalizacaoUtil localizacaoUtil = new LocalizacaoUtil();
        Util util = new Util();

        Pet pet = Pet.builder()
                .nome("Brabo")
                .descricao("Bixo e brabo")
                .dataAchado(LocalDate.now())
                .dataRegistro(LocalDate.now())
                .especie(Especie.CACHORRO)
                .porte(Porte.GRANDE)
                .sexo(Sexo.MACHO)
                .status(Status.PARA_ADOTAR)
                .idade(Idade.ADULTO)
                .usuario(util.criarUsuarioKamuela())
                .localizacao(localizacaoUtil.criaLocalizacaoGaruva())
                .ativo(true)
                .build();

        return pet;
    }


}
