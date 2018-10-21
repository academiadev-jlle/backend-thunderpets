package br.com.academiadev.thunderpets.model;

import br.com.academiadev.thunderpets.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoPet tipo;

    @Enumerated(EnumType.STRING)
    private Porte porte;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    private Usuario usuario;

    private String nome;
    private String localizacao;
    private String descricao;
    private String contato;
}
