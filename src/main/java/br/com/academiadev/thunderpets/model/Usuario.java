package br.com.academiadev.thunderpets.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @Size(min = 3)
    private String nome;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 8)
    private String senha;

    @Lob
    private byte[] foto;

    @Column(columnDefinition = "bool default true")
    private boolean ativo;
}
