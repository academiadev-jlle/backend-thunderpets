package br.com.academiadev.thunderpets.service.impl;

import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.dto.UsuarioRespostaDTO;
import br.com.academiadev.thunderpets.exception.ErroAoProcessarException;
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

    private UsuarioRepository usuarioRepository;
    private ContatoRepository contatoRepository;
    private UsuarioMapper usuarioMapper;
    private ContatoMapper contatoMapper;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              ContatoRepository contatoRepository,
                              UsuarioMapper usuarioMapper,
                              ContatoMapper contatoMapper) {
        this.usuarioRepository = usuarioRepository;
        this.contatoRepository = contatoRepository;
        this.usuarioMapper = usuarioMapper;
        this.contatoMapper = contatoMapper;
    }

    @Override
    public PageImpl<UsuarioRespostaDTO> listar(int paginaAtual, int tamanho, Sort.Direction direcao, String campoOrdenacao) {
        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);
        Page<Usuario> paginaUsuarios = usuarioRepository.findAll(paginacao);

        return (PageImpl<UsuarioRespostaDTO>) paginaUsuarios
                .map(usuario -> usuarioMapper.toDTO(usuario, contatoRepository.findByUsuario(usuario)));
    }

    @Override
    public UsuarioRespostaDTO buscar(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(String.format("Usuário %s não encontrado.", id)));

        return usuarioMapper.toDTO(usuario, contatoRepository.findByUsuario(usuario));
    }

    @Override
    public UsuarioRespostaDTO salvar(UsuarioDTO usuarioDTO) {
        usuarioDTO.setSenha(new BCryptPasswordEncoder().encode(usuarioDTO.getSenha()));
        usuarioDTO.setAtivo(true);

        final Usuario usuario = usuarioRepository
                .saveAndFlush(usuarioMapper.toEntity(usuarioDTO));

        Set<Contato> contatosDoUsuario = contatoRepository.findByUsuario(usuario);
        contatosDoUsuario.forEach(contatoRepository::delete);

        usuarioDTO.getContatos().forEach(contatoDTO -> contatoRepository.save(
                contatoMapper.toEntity(contatoDTO, usuario)));

        return usuarioMapper.toDTO(usuario, contatoRepository.findByUsuario(usuario));
    }

    @Override
    public void deletar(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(String.format("Usuário %s não encontrado.", id)));

        usuario.setAtivo(false);
        usuarioRepository.saveAndFlush(usuario);
    }

    public byte[] getFoto(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(String.format("Usuário %s não encontrado.", id)));

        byte[] bytes = usuario.getFoto();

        if (bytes == null) {
            throw new FotoNaoEncontradaException(String.format("O usuário %s não possui foto.", id));
        }

        return bytes;
    }
}
