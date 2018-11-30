package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.model.Foto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

@Component
@ActiveProfiles("test")
public class PetDTOUtil {

    @Autowired
    public LocalizacaoUtil localizacaoUtil;

    @Autowired
    public Util util;

    @Autowired
    public FotoUtil fotoUtil;

    public PetDTO criaPetDTOBrabo() {

        List<byte[]> fotos = fotoUtil.criaTresFotos();

        PetDTO petDTO = PetDTO.builder()
                .nome("Brabo")
                .descricao("Bixo e brabo")
                .dataAchado(LocalDate.now())
                .dataRegistro(LocalDate.now())
                .especie(Especie.CACHORRO)
                .porte(Porte.GRANDE)
                .sexo(Sexo.MACHO)
                .status(Status.PARA_ADOTAR)
                .idade(Idade.ADULTO)
                .usuarioId(util.criarUsuarioKamuela().getId())
                .localizacao(localizacaoUtil.criaLocalizacaoGaruva())
                .fotos(fotos)
                .ativo(true)
                .build();

        return petDTO;
    }

}
