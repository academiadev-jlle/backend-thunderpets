package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.dto.UsuarioRespostaDTO;
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
    private FotoUtil fotoUtil;

    @Autowired
    public PetDTOUtil(LocalizacaoUtil localizacaoUtil, FotoUtil fotoUtil) {
        this.localizacaoUtil = localizacaoUtil;
        this.fotoUtil = fotoUtil;
    }

    public PetDTO criaPetDTOBrabo(UsuarioRespostaDTO usuario) {
        List<Foto> fotos = fotoUtil.criaTresFotos();

        PetDTO petDTO = PetDTO.builder()
                .nome("Brabo")
                .descricao("Bixo e brabo")
                .dataAchado(LocalDate.now())
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

    public PetDTO criaPetDTOCarijo(UsuarioRespostaDTO usuario) {
        List<Foto> fotos = fotoUtil.criaTresFotos();

        PetDTO petDTO = PetDTO.builder()
                .nome("Carijó")
                .descricao("Carijó é gentil")
                .dataAchado(LocalDate.of(2018, 8,4))
                .especie(Especie.OUTROS)
                .porte(Porte.MEDIO)
                .sexo(Sexo.FEMEA)
                .status(Status.ACHEI_DONO)
                .idade(Idade.ADULTO)
                .usuarioId(usuario.getId())
                .localizacao(localizacaoUtil.criaLocalizacaoTerminalCentral())
                .fotos(fotos.stream().map(Foto::getImage).collect(Collectors.toList()))
                .ativo(true)
                .build();

        return petDTO;
    }

    public PetDTO criaPetDTOPocoto(UsuarioRespostaDTO usuario) {
        List<Foto> fotos = fotoUtil.criaTresFotos();

        PetDTO petDTO = PetDTO.builder()
                .nome("Pocotó")
                .descricao("Pocotó é rápido")
                .dataAchado(LocalDate.of(2018, 12,1))
                .especie(Especie.OUTROS)
                .porte(Porte.GRANDE)
                .sexo(Sexo.MACHO)
                .status(Status.PROCURANDO_DONO)
                .idade(Idade.FILHOTE)
                .usuarioId(usuario.getId())
                .localizacao(localizacaoUtil.criaLocalizacaoAmerica())
                .fotos(fotos.stream().map(Foto::getImage).collect(Collectors.toList()))
                .ativo(true)
                .build();

        return petDTO;
    }

}
