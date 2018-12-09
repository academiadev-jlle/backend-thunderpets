package br.com.academiadev.thunderpets.service;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.dto.UsuarioRespostaDTO;
import br.com.academiadev.thunderpets.exception.ErroAoProcessarException;
import br.com.academiadev.thunderpets.exception.NaoEncontradoException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface UsuarioService {

    PageImpl<UsuarioRespostaDTO> listar(int paginaAtual, int tamanho, Sort.Direction direcao, String campoOrdenacao);

    UsuarioRespostaDTO buscar(UUID id);

    UsuarioRespostaDTO salvar(UsuarioDTO usuarioDTO);

    void deletar(UUID id);

    byte[] getFoto(UUID id);

    List<PetDTO> getPets(UUID id);

    String esqueciMinhaSenha(String email);

    String redefinirSenha(UUID idRecuperarSenha, String senha) throws ErroAoProcessarException;
}
