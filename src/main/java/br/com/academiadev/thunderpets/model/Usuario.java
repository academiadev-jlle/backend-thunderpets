package br.com.academiadev.thunderpets.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    private Foto foto;

    @NotNull
    @Size(min = 3)
    private String nome;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 8)
    private String senha;

    @Column(columnDefinition = "bool default true")
    private boolean ativo;
}
