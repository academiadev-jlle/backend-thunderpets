package br.com.academiadev.thunderpets.service.impl;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.dto.UsuarioRespostaDTO;
import br.com.academiadev.thunderpets.exception.FotoNaoEncontradaException;
import br.com.academiadev.thunderpets.exception.NaoEncontradoException;
import br.com.academiadev.thunderpets.exception.UsuarioNaoEncontradoException;
import br.com.academiadev.thunderpets.mapper.ContatoMapper;
import br.com.academiadev.thunderpets.mapper.PetMapper;
import br.com.academiadev.thunderpets.mapper.UsuarioMapper;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.RecuperarSenha;
import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import br.com.academiadev.thunderpets.repository.FotoRepository;
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

import java.time.Duration;
import java.time.LocalDateTime;
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
    private FotoRepository fotoRepository;
    private UsuarioMapper usuarioMapper;
    private PetMapper petMapper;
    private ContatoMapper contatoMapper;
    private EmailService emailService;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PetRepository petRepository,
                              ContatoRepository contatoRepository,
                              RecuperarSenhaRepository recuperarSenhaRepository,
                              FotoRepository fotoRepository,
                              UsuarioMapper usuarioMapper,
                              PetMapper petMapper,
                              ContatoMapper contatoMapper,
                              EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.petRepository = petRepository;
        this.contatoRepository = contatoRepository;
        this.recuperarSenhaRepository = recuperarSenhaRepository;
        this.fotoRepository = fotoRepository;
        this.usuarioMapper = usuarioMapper;
        this.petMapper = petMapper;
        this.contatoMapper = contatoMapper;
        this.emailService = emailService;
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

        return petRepository.findByUsuarioAndAtivoIsTrue(usuario).stream()
                .map(pet -> petMapper.toDTO(
                        pet,
                        fotoRepository.findFirstByPetId(pet.getId())
                                .stream().map(Foto::getImage).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public String esqueciMinhaSenha(String email) {
        Usuario usuario = usuarioRepository.findOneByEmail(email);
        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(String.format("Não há usuário com o e-mail %s cadastrado na plataforma.", email));
        }

        RecuperarSenha recuperarSenha = RecuperarSenha.builder()
                .usuario(usuario)
                .ativo(true)
                .build();
        recuperarSenha = recuperarSenhaRepository.saveAndFlush(recuperarSenha);

        String conteudo = String.format("Olá, \n\n" +
                "Clique no link abaixo para redefinir sua senha: \n" +
                "Link: https://thunderpets.netlify.com/forgotPassword/%s \n\n" +
                "O link de redefinição de senha é válido por 2 horas.", recuperarSenha.getId());

        return emailService.enviaMensagemSimples(email, "Redefinição de senha ThunderPets", conteudo);
    }

    @Override
    public String redefinirSenha(UUID idRecuperarSenha, String senha) throws NaoEncontradoException, ErroAoProcessarException {
        RecuperarSenha recuperarSenha = recuperarSenhaRepository.findById(idRecuperarSenha)
                .orElseThrow(() -> new NaoEncontradoException(String.format("Token %s de recuperação de senha não encontrado.", idRecuperarSenha)));

        if(!recuperarSenha.isAtivo()) {
            throw new ErroAoProcessarException("O token não é válido, ele encontra-se inativo pois já foi utilizado.");
        }

        if((Duration.between(recuperarSenha.getCreatedAt(), LocalDateTime.now()).getSeconds()) > 60*60*2) {
            throw new ErroAoProcessarException("O token não é válido, ele foi solicitado há mais de 2 horas.");
        }

        Usuario usuario = recuperarSenha.getUsuario();
        try {
            usuario.setSenha(new BCryptPasswordEncoder().encode(senha));
            usuarioRepository.saveAndFlush(usuario);

            recuperarSenha.setAtivo(false);
            recuperarSenhaRepository.saveAndFlush(recuperarSenha);

            return "Senha alterada com sucesso.";
        } catch (Exception e) {
            return "Erro ao alterar a senha do usuário. " + e.getMessage();
        }
    }
}
