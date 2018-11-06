package br.com.academiadev.thunderpets.service;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.exception.FotoNaoEncontradaException;
import br.com.academiadev.thunderpets.exception.UsuarioNaoEncontradoException;
import br.com.academiadev.thunderpets.mapper.ContatoMapper;
import br.com.academiadev.thunderpets.mapper.UsuarioMapper;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private ContatoMapper contatoMapper;

    public PageImpl<UsuarioDTO> listar(int paginaAtual, int tamanho, Sort.Direction direcao, String campoOrdenacao) {
        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);
        Page<Usuario> paginaUsuarios = usuarioRepository.findAll(paginacao);
        int totalDeElementos = (int) paginaUsuarios.getTotalElements();

        return new PageImpl<UsuarioDTO>(paginaUsuarios.stream()
                .map(usuario -> usuarioMapper.converterUsuarioParaUsuarioDTO(usuario)).collect(Collectors.toList()),
                paginacao,
                totalDeElementos);
    }

    public Object buscar(UUID id) throws Exception {
        if (!usuarioRepository.existsById(id)) {
           throw new UsuarioNaoEncontradoException("Usuário " + id + " não encontrado.");
        }

        return usuarioMapper.converterUsuarioParaUsuarioDTO(usuarioRepository.findById(id).get());
    }

    public Object salvar(UsuarioDTO usuarioDTO) throws Exception {
        usuarioDTO.setSenha(new BCryptPasswordEncoder().encode(usuarioDTO.getSenha()));
        usuarioDTO.setAtivo(true);

        if (usuarioDTO.getContatos() == null || usuarioDTO.getContatos().size() == 0) {
            throw new Exception("O usuário precisa ter pelo menos um contato cadastrado.");
        }

        final Usuario usuario = usuarioRepository
                .saveAndFlush(usuarioMapper.converterUsuarioDTOParaUsuario(usuarioDTO));

        List<Contato> contatosDoUsuario = contatoRepository.findByUsuario(usuario);
        contatosDoUsuario.forEach(contatoRepository::delete);

        usuarioDTO.getContatos().forEach(contatoDTO -> contatoRepository.save(
                contatoMapper.converterContatoDTOParaContato(contatoDTO, usuario)));

        return usuarioMapper.converterUsuarioParaUsuarioDTO(usuario);
    }

    public Object deletar(UUID id) throws Exception {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNaoEncontradoException("Usuário " + id + " não encontrado.");
        }

        Usuario usuario = usuarioRepository.findById(id).get();
        usuario.setAtivo(false);
        usuarioRepository.saveAndFlush(usuario);

        return true;
    }

    public byte[] getFoto(UUID id) throws Exception {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNaoEncontradoException("Usuário " + id + " não encontrado.");
        }

        Usuario usuario = usuarioRepository.findById(id).get();
        byte[] bytes = usuario.getFoto();

        if (bytes == null) {
            throw new FotoNaoEncontradaException("O usuário " + id + " não possui foto.");
        }

        return bytes;
    }
}
