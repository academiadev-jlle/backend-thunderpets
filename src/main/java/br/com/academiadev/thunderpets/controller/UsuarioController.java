package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.exception.FotoNaoEncontradaException;
import br.com.academiadev.thunderpets.exception.UsuarioNaoEncontradoException;
import br.com.academiadev.thunderpets.mapper.ContatoMapper;
import br.com.academiadev.thunderpets.mapper.UsuarioMapper;
import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
<<<<<<< HEAD

import br.com.academiadev.thunderpets.service.UsuarioService;
=======
>>>>>>> master
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuario")
@Api(description = "Controller de Usuários")
public class UsuarioController {

    @Autowired
<<<<<<< HEAD
    private UsuarioService usuarioService;

=======
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private ContatoMapper contatoMapper;

>>>>>>> master
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
<<<<<<< HEAD

        return usuarioService.listar(paginaAtual, tamanho, direcao, campoOrdenacao);
=======
        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);
        Page<Usuario> paginaUsuarios = usuarioRepository.findAll(paginacao);
        int totalDeElementos = (int) paginaUsuarios.getTotalElements();

        return new PageImpl<UsuarioDTO>(paginaUsuarios.stream()
                .map(usuario -> usuarioMapper.converterUsuarioParaUsuarioDTO(usuario)).collect(Collectors.toList()),
                paginacao,
                totalDeElementos);
>>>>>>> master
    }

    @ApiOperation(value = "Busca um usuário com base no id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuário encontrado com sucesso"),
            @ApiResponse(code = 500, message = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscar(@PathVariable("id") UUID id) {
        UsuarioDTO usuarioDTO = null;
        try {
            usuarioDTO = (UsuarioDTO) usuarioService.buscar(id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }

        return ResponseEntity.ok(usuarioDTO);
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
<<<<<<< HEAD
    @PostMapping("")
    public ResponseEntity<Object> salvar(@RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuarioPersistido = new UsuarioDTO();
        try {
            usuarioPersistido = (UsuarioDTO) usuarioService.salvar(usuarioDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
=======
    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody UsuarioDTO usuarioDTO) {
        usuarioDTO.setSenha(new BCryptPasswordEncoder().encode(usuarioDTO.getSenha()));
        usuarioDTO.setAtivo(true);

        if (usuarioDTO.getContatos() == null || usuarioDTO.getContatos().size() == 0) {
            return ResponseEntity
                    .status(502)
                    .body(new Exception("O usuário precisa ter pelo menos um contato cadastrado."));
>>>>>>> master
        }

        final Usuario usuario = usuarioRepository
                .saveAndFlush(usuarioMapper.converterUsuarioDTOparaUsuario(usuarioDTO));

        List<Contato> contatosDoUsuario = contatoRepository.findByUsuario(usuario);
        contatosDoUsuario.forEach(contatoRepository::delete);

        usuarioDTO.getContatos().forEach(contatoDTO -> contatoRepository.save(
                contatoMapper.converterContatoDTOParaContato(contatoDTO, usuario)));

        return ResponseEntity.ok(usuarioMapper.converterUsuarioParaUsuarioDTO(usuario));
    }

    @ApiOperation(value = "Inativa um usuário com base no id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuário inativado com sucesso"),
            @ApiResponse(code = 500, message = "Usuário não encontrado. Erro ao inativar o usuário")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable("id") UUID id) {
        try {
            usuarioService.deletar(id);
        } catch (Exception e) {
            ResponseEntity.status(500).body(e);
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
        byte[] bytes;

        try {
            bytes = usuarioService.getFoto(id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }
}
