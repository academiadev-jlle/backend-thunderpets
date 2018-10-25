package br.com.academiadev.thunderpets.model;

import br.com.academiadev.thunderpets.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

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
    private Status status;

    @Enumerated(EnumType.STRING)
    private Idade idade;

    @ElementCollection(targetClass = Foto.class)
    private Set<Foto> fotos;

    @ManyToOne
    private Usuario usuario;

    @OneToOne
    private Endereco localizacao;

    private String nome;
    private String descricao;
    private String contato;
}
