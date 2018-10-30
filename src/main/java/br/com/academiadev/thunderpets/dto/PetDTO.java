package br.com.academiadev.thunderpets.dto;

import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Localizacao;
import br.com.academiadev.thunderpets.model.Usuario;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PetDTO {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    private String nome;

    @NotNull
    private String descricao;

    @NotNull
    private LocalDate dataAchado;

    @NotNull
    private LocalDate dataRegistro;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Especie especie;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Porte porte;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Idade idade;

    private Usuario usuario;

    private Localizacao localizacao;

    @Column(columnDefinition = "bool default true")
    private boolean ativo;

    @NotNull
    private List<Foto> fotos;

}
