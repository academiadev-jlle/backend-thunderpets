package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

@Component
@ActiveProfiles("test")
public class PetDTOUtil {

    private LocalizacaoUtil localizacaoUtil;
    private FotoUtil fotoUtil;

    @Autowired
    public PetDTOUtil(LocalizacaoUtil localizacaoUtil, FotoUtil fotoUtil) {
        this.localizacaoUtil = localizacaoUtil;
        this.fotoUtil = fotoUtil;
    }

    public PetDTO criaPetDTOBrabo(UsuarioDTO usuario) {
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
                .usuarioId(usuario.getId())
                .localizacao(localizacaoUtil.criaLocalizacaoGaruva())
                .fotos(fotos)
                .ativo(true)
                .build();

        return petDTO;
    }

}
