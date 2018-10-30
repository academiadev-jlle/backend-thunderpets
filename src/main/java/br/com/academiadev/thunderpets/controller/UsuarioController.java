package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public Page<Usuario> listar(@RequestParam(defaultValue = "0") int paginaAtual,
                                @RequestParam(defaultValue = "10") int tamanho,
                                @RequestParam(defaultValue = "ASC") Sort.Direction direcao,
                                @RequestParam(defaultValue = "nome") String campoOrdenacao) {
        PageRequest pagina = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);
        Page<Usuario> page = usuarioRepository.findAll(pagina);

        return page;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(usuarioRepository.findById(id).get());
    }

    @PostMapping("/")
    public ResponseEntity<Object> salvar(@RequestBody Usuario usuario) {
        Usuario usuarioPersistido;
        try {
            usuarioPersistido = usuarioRepository.saveAndFlush(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }

        return ResponseEntity.ok(usuarioPersistido);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable("id") UUID id) {
        try {
            Usuario usuario = usuarioRepository.findById(id).get();
            usuarioRepository.deleteById(usuario.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
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
}
