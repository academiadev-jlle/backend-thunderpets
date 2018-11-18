package br.com.academiadev.thunderpets.service.impl;

import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.exception.FotoNaoEncontradaException;
import br.com.academiadev.thunderpets.exception.UsuarioNaoEncontradoException;
import br.com.academiadev.thunderpets.mapper.ContatoMapper;
import br.com.academiadev.thunderpets.mapper.UsuarioMapper;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
import br.com.academiadev.thunderpets.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

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
                .map(usuario -> usuarioMapper.toDTO(usuario, contatoRepository.findByUsuario(usuario))).collect(Collectors.toList()),
                paginacao,
                totalDeElementos);
    }

    public Object buscar(UUID id) throws Exception {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário " + id + " não encontrado."));

        return usuarioMapper.toDTO(usuario, contatoRepository.findByUsuario(usuario));
    }

    public Object salvar(UsuarioDTO usuarioDTO) throws Exception {
        usuarioDTO.setSenha(new BCryptPasswordEncoder().encode(usuarioDTO.getSenha()));
        usuarioDTO.setAtivo(true);

        if (usuarioDTO.getContatos() == null || usuarioDTO.getContatos().size() == 0) {
            throw new Exception("O usuário precisa ter pelo menos um contato cadastrado.");
        }

        final Usuario usuario = usuarioRepository
                .saveAndFlush(usuarioMapper.toEntity(usuarioDTO));

        Set<Contato> contatosDoUsuario = contatoRepository.findByUsuario(usuario);
        contatosDoUsuario.forEach(contatoRepository::delete);

        usuarioDTO.getContatos().forEach(contatoDTO -> contatoRepository.save(
                contatoMapper.toEntity(contatoDTO, usuario)));

        return usuarioMapper.toDTO(usuario, contatoRepository.findByUsuario(usuario));
    }

    public Object deletar(UUID id) throws Exception {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário " + id + " não encontrado."));
        usuario.setAtivo(false);
        usuarioRepository.saveAndFlush(usuario);

        return true;
    }

    public byte[] getFoto(UUID id) throws Exception {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário " + id + " não encontrado."));
        byte[] bytes = usuario.getFoto();

        if (bytes == null) {
            throw new FotoNaoEncontradaException("O usuário " + id + " não possui foto.");
        }

        return bytes;
    }
}
