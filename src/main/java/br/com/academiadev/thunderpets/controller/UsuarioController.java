package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.ContatoDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.exception.FotoNaoEncontradaException;
import br.com.academiadev.thunderpets.exception.UsuarioNaoEncontradoException;
import br.com.academiadev.thunderpets.mapper.UsuarioMapper;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuario")
@Api(description = "Controller de Usuários")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ContatoRepository contatoRepository;
    @Autowired
    private UsuarioMapper usuarioMapper;

    @ApiOperation(value = "Lista os usuários da plataforma",
            notes = "Retorna uma lista com os detalhes do usuário. A lista é paginada com base nos parâmetros.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuários listados com sucesso")
    })
    @GetMapping("")
    public PageImpl<UsuarioDTO> listar(@ApiParam(value = "Número da página atual")
                                           @RequestParam(defaultValue = "0") int paginaAtual,
                                       @ApiParam(value = "Número do tamanho da página")
                                           @RequestParam(defaultValue = "10") int tamanho,
                                       @ApiParam(value = "Direção da ordenação: ascendente ou descendente")
                                           @RequestParam(defaultValue = "ASC") Sort.Direction direcao,
                                       @ApiParam(value = "Nome da coluna que será usada para a ordenação")
                                           @RequestParam(defaultValue = "nome") String campoOrdenacao) {
        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);
        Page<Usuario> paginaUsuarios = usuarioRepository.findAll(paginacao);
        int totalDeElementos = (int) paginaUsuarios.getTotalElements();

        return new PageImpl<UsuarioDTO>(paginaUsuarios.stream()
                .map(usuario -> usuarioMapper.converterUsuarioParaUsuarioDTO(usuario)).collect(Collectors.toList()),
                paginacao,
                totalDeElementos);
    }

    @ApiOperation(value = "Busca um usuário com base no id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuário encontrado com sucesso"),
            @ApiResponse(code = 500, message = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscar(@PathVariable("id") UUID id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.status(500)
                    .body(new UsuarioNaoEncontradoException("Usuario " + id + " não encontrado."));
        }

        return ResponseEntity.ok(usuarioMapper.converterUsuarioParaUsuarioDTO(usuarioRepository.findById(id).get()));
    }

    @ApiOperation(value = "Salva um usuário na plataforma",
            notes = "Caso não exista nenhum usuário com o id fornecido, "
                    + "um novo usuário será criado. "
                    + "Do contrário, caso já exista um usuário com o id fornecido, "
                    + "os dados do usuário existente serão atualizados.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuário criado e/ou atualizado com sucesso"),
            @ApiResponse(code = 500, message = "Erro ao criar e/ou atualizar o usuário")
    })
    @PostMapping("")
    public ResponseEntity<Object> salvar(@RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuarioPersistido = new UsuarioDTO();
        try {
            Usuario usuario = usuarioMapper.converterUsuarioDTOparaUsuario(usuarioDTO);
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

            usuarioPersistido = usuarioMapper.converterUsuarioParaUsuarioDTO(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }

        return ResponseEntity.ok(usuarioPersistido);
    }

    @ApiOperation(value = "Inativa um usuário com base no id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuário inativado com sucesso"),
            @ApiResponse(code = 500, message = "Usuário não encontrado. Erro ao inativar o usuário")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable("id") UUID id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.status(500)
                    .body(new UsuarioNaoEncontradoException("Usuario " + id + " não encontrado."));
        }

        try {
            Usuario usuario = usuarioRepository.findById(id).get();
            usuario.setAtivo(false);
            usuarioRepository.saveAndFlush(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }

        return ResponseEntity.ok(true);
    }

    @ApiOperation(value = "Busca a foto do usuário com base no id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Foto do usuário encontrada com sucesso"),
            @ApiResponse(code = 500, message = "Usuário não encontrado. "
                    + "Foto não encontrada. "
                    + "Erro ao buscar a foto do usuário")
    })
    @GetMapping("{id}/foto")
    public ResponseEntity<Object> getFoto(@PathVariable("id") UUID id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.status(500)
                    .body(new UsuarioNaoEncontradoException("Usuario " + id + " não encontrado."));
        }

        Usuario usuario = usuarioRepository.findById(id).get();
        byte[] bytes = usuario.getFoto();

        if (bytes == null) {
            return ResponseEntity.status(500)
                    .body(new FotoNaoEncontradaException("O usuário " + id + " não possui foto."));
        }

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }
}
