package br.com.academiadev.thunderpets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRespostaDTO {

    private UUID id;
    private String nome;
    private String email;
    private byte[] foto;
    private boolean ativo;
    private Set<ContatoDTO> contatos;
}
