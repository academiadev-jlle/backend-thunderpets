package br.com.academiadev.thunderpets.service.impl;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.exception.ErroAoProcessarException;
import br.com.academiadev.thunderpets.exception.FotoNaoEncontradaException;
import br.com.academiadev.thunderpets.exception.UsuarioNaoEncontradoException;
import br.com.academiadev.thunderpets.mapper.ContatoMapper;
import br.com.academiadev.thunderpets.mapper.PetMapper;
import br.com.academiadev.thunderpets.mapper.UsuarioMapper;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.RecuperarSenha;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import br.com.academiadev.thunderpets.repository.PetRepository;
import br.com.academiadev.thunderpets.repository.RecuperarSenhaRepository;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
import br.com.academiadev.thunderpets.service.EmailService;
import br.com.academiadev.thunderpets.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository usuarioRepository;
    private PetRepository petRepository;
    private ContatoRepository contatoRepository;
    private RecuperarSenhaRepository recuperarSenhaRepository;
    private UsuarioMapper usuarioMapper;
    private PetMapper petMapper;
    private ContatoMapper contatoMapper;
    private EmailService emailService;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PetRepository petRepository,
                              ContatoRepository contatoRepository,
                              RecuperarSenhaRepository recuperarSenhaRepository,
                              UsuarioMapper usuarioMapper,
                              PetMapper petMapper,
                              ContatoMapper contatoMapper,
                              EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.petRepository = petRepository;
        this.contatoRepository = contatoRepository;
        this.recuperarSenhaRepository = recuperarSenhaRepository;
        this.usuarioMapper = usuarioMapper;
        this.petMapper = petMapper;
        this.contatoMapper = contatoMapper;
        this.emailService = emailService;
    }

    @Override
    public PageImpl<UsuarioDTO> listar(int paginaAtual, int tamanho, Sort.Direction direcao, String campoOrdenacao) {
        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);
        Page<Usuario> paginaUsuarios = usuarioRepository.findAll(paginacao);

        return (PageImpl<UsuarioDTO>) paginaUsuarios
                .map(usuario -> usuarioMapper.toDTO(usuario, contatoRepository.findByUsuario(usuario)));
    }

    @Override
    public UsuarioDTO buscar(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(String.format("Usuário %s não encontrado.", id)));

        return usuarioMapper.toDTO(usuario, contatoRepository.findByUsuario(usuario));
    }

    @Override
    public UsuarioDTO salvar(UsuarioDTO usuarioDTO) {
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

    @Override
    public byte[] getFoto(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(String.format("Usuário %s não encontrado.", id)));

        byte[] bytes = usuario.getFoto();

        if (bytes == null) {
            throw new FotoNaoEncontradaException(String.format("O usuário %s não possui foto.", id));
        }

        return bytes;
    }

    @Override
    public List<PetDTO> getPets(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(String.format("Usuário %s não encontrado.", id)));

        return petRepository.findByUsuario(usuario).stream()
                .map(pet -> petMapper.converterPetParaPetDTO(pet, true))
                .collect(Collectors.toList());
    }

    @Override
    public String esqueciMinhaSenha(String email) {
        Usuario usuario = usuarioRepository.findOneByEmail(email);
        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(String.format("Não há usuário com o e-mail %s cadastrado na plataforma.", email));
        }

        RecuperarSenha recuperarSenha = RecuperarSenha.builder().usuario(usuario).build();
        recuperarSenha = recuperarSenhaRepository.saveAndFlush(recuperarSenha);

        String url = "http://localhost:8080/swagger-ui.html#/" +recuperarSenha.getId();

        String conteudo = String.format("Olá, \n\nClique no link abaixo para redefinir sua senha: \nLink: %s \n\n O link de redefinição de senha é válido por 2 horas.", url);

        return emailService.enviaMensagemSimples(email, "Redefinição de senha ThunderPets", conteudo);
    }

    //@Override
    public void redefineSenha(String email) {

    }
}
