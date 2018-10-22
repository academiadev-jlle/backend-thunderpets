package br.com.academiadev.thunderpets.model;

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
public class Usuario {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Endereco localizacao;

    @OneToOne
    private Foto foto;

    private String nome;
    private String email;
    private String senha;
}
