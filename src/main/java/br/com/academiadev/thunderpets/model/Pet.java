package br.com.academiadev.thunderpets.model;

import br.com.academiadev.thunderpets.enums.*;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper=false)
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Pet extends EntidadeAuditavel<String>{

    @Id
    @GeneratedValue
    private UUID id;

    private String nome;

    @NotNull
    private String descricao;

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

    @ManyToOne(optional = false)
    private Usuario usuario;

    @OneToOne
    private Localizacao localizacao;

    @Builder.Default
    private boolean ativo = true;
}
