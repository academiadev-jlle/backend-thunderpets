package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ContatoRepository contatoRepository;

    @GetMapping("/")
    public PageImpl<UsuarioDTO> listar(@RequestParam(defaultValue = "0") int paginaAtual,
                                       @RequestParam(defaultValue = "10") int tamanho,
                                       @RequestParam(defaultValue = "ASC") Sort.Direction direcao,
                                       @RequestParam(defaultValue = "nome") String campoOrdenacao) {
        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);
        Page<Usuario> paginaUsuarios = usuarioRepository.findAll(paginacao);
        int totalDeElementos = (int) paginaUsuarios.getTotalElements();

        return new PageImpl<UsuarioDTO>(paginaUsuarios.stream()
                .map(usuario -> converterUsuarioParaUsuarioDTO(usuario)).collect(Collectors.toList()),
                paginacao,
                totalDeElementos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscar(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(converterUsuarioParaUsuarioDTO(usuarioRepository.findById(id).get()));
    }

    @PostMapping("/")
    public ResponseEntity<Object> salvar(@RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuarioPersistido = new UsuarioDTO();
        try {

            Usuario usuario = usuarioRepository.findById(usuarioDTO.getId()).get();
            usuario = usuarioRepository.saveAndFlush(usuario);

            List<Contato> contatosDoUsuario = contatoRepository.findByUsuario(usuario);
            for (Contato contatoDelete : contatosDoUsuario) {
                contatoRepository.delete(contatoDelete);
            }

            for (ContatoDTO contatoDTO : usuarioDTO.getContatos()) {
                Contato contato = new Contato();
                contato.setId(contatoDTO.getId());
                contato.setTipo(contatoDTO.getTipo());
                contato.setDescricao(contatoDTO.getDescricao());
                contato.setUsuario(usuario);
                contatoRepository.saveAndFlush(contato);
            }

            usuarioPersistido = converterUsuarioParaUsuarioDTO(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }

        return ResponseEntity.ok(usuarioPersistido);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable("id") UUID id) {
        try {
            Usuario usuario = usuarioRepository.findById(id).get();
            usuario.setAtivo(false);
            usuarioRepository.saveAndFlush(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }

        return ResponseEntity.ok(true);
    }

    @GetMapping("{id}/foto")
    public ResponseEntity<Object> getFoto(@PathVariable("id") UUID id) {
        byte[] bytes = null;
        try {
            Usuario usuario = usuarioRepository.findById(id).get();
            bytes = usuario.getFoto();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }

    public UsuarioDTO converterUsuarioParaUsuarioDTO(Usuario usuario) {
        UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .senha(usuario.getSenha())
                .foto(usuario.getFoto())
                .ativo(usuario.isAtivo())
                .build();

        Set<ContatoDTO> contatos = new HashSet<>();
        List<Contato> contatosDoUsuario = contatoRepository.findByUsuario(usuario);
        for (Contato c : contatosDoUsuario) {
            ContatoDTO contatoDTO = ContatoDTO.builder()
                    .id(c.getId())
                    .tipo(c.getTipo())
                    .descricao(c.getDescricao())
                    .build();
            contatos.add(contatoDTO);
        }

        usuarioDTO.setContatos(contatos);

        return usuarioDTO;
    }
}
