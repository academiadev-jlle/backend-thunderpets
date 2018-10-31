package br.com.academiadev.thunderpets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

    private UUID id;
    private String nome;
    private String email;
    private String senha;
    private byte[] foto;
    private boolean ativo;
    private Set<ContatoDTO> contatos;
}
