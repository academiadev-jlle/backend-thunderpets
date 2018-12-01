package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.model.Foto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ActiveProfiles("test")
public class PetDTOUtil {

    private LocalizacaoUtil localizacaoUtil;
    private Util util;
    private FotoUtil fotoUtil;

    @Autowired
    public PetDTOUtil(LocalizacaoUtil localizacaoUtil, Util util, FotoUtil fotoUtil) {
        this.localizacaoUtil = localizacaoUtil;
        this.util = util;
        this.fotoUtil = fotoUtil;
    }

    public PetDTO criaPetDTOBrabo(UsuarioDTO usuario) {
        List<Foto> fotos = fotoUtil.criaTresFotos();

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
                .fotos(fotos.stream().map(Foto::getImage).collect(Collectors.toList()))
                .ativo(true)
                .build();

        return petDTO;
    }

}
