package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.model.Pet;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PetUtil {

    public Pet criaPetBrabo(){
        LocalizacaoUtil localizacaoUtil = new LocalizacaoUtil();
        UsuarioUtil usuarioUtil = new UsuarioUtil();

        Pet pet = Pet.builder()
                .nome("Brabo")
                .descricao("Bixo e brabo")
                .dataAchado(LocalDate.now())
                .especie(Especie.CACHORRO)
                .porte(Porte.GRANDE)
                .sexo(Sexo.MACHO)
                .status(Status.PARA_ADOTAR)
                .idade(Idade.ADULTO)
                .usuario(usuarioUtil.criarUsuarioKamuela())
                .localizacao(localizacaoUtil.criaLocalizacaoGaruva())
                .ativo(true)
                .build();

        return pet;
    }


}
