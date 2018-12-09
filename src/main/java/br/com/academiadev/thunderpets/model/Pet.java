package br.com.academiadev.thunderpets.model;

import br.com.academiadev.thunderpets.enums.*;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Pet extends EntidadeAuditavel{

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    private String nome;

    @NotNull
    private String descricao;

    private LocalDate dataAchado;

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

    private boolean ativo = true;

    @Builder
    public Pet(LocalDateTime dataRegistro, String nome, @NotNull String descricao, LocalDate dataAchado, @NotNull Especie especie, @NotNull Porte porte, @NotNull Sexo sexo, @NotNull Status status, @NotNull Idade idade, Usuario usuario, Localizacao localizacao, boolean ativo) {
        super(dataRegistro);
        this.nome = nome;
        this.descricao = descricao;
        this.dataAchado = dataAchado;
        this.especie = especie;
        this.porte = porte;
        this.sexo = sexo;
        this.status = status;
        this.idade = idade;
        this.usuario = usuario;
        this.localizacao = localizacao;
        this.ativo = ativo;
    }
}
