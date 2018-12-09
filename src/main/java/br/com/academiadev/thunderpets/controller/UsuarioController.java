package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.FotoDTO;
import br.com.academiadev.thunderpets.dto.PetRespostaDTO;
import br.com.academiadev.thunderpets.dto.UsuarioDTO;
import br.com.academiadev.thunderpets.dto.UsuarioRespostaDTO;
import br.com.academiadev.thunderpets.exception.ErroAoProcessarException;
import br.com.academiadev.thunderpets.exception.NaoEncontradoException;
import br.com.academiadev.thunderpets.service.UsuarioService;
import io.swagger.annotations.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuario")
@Api(description = "Controller de Usuários")
public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ApiOperation(
            value = "Lista os usuários da plataforma",
            notes = "Retorna uma lista com os detalhes do usuário. A lista é paginada com base nos parâmetros."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Usuários listados com sucesso")
    })
    @GetMapping
    public PageImpl<UsuarioRespostaDTO> listar(@ApiParam(value = "Número da página atual")
                                           @RequestParam(defaultValue = "0") int paginaAtual,
                                               @ApiParam(value = "Número do tamanho da página")
                                           @RequestParam(defaultValue = "10") int tamanho,
                                               @ApiParam(value = "Direção da ordenação: ascendente ou descendente")
                                           @RequestParam(defaultValue = "ASC") Sort.Direction direcao,
                                               @ApiParam(value = "Nome da coluna que será usada para a ordenação")
                                           @RequestParam(defaultValue = "nome") String campoOrdenacao) {
        return usuarioService.listar(paginaAtual, tamanho, direcao, campoOrdenacao);
    }

    @ApiOperation("Busca um usuário com base no id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Usuário encontrado com sucesso"),
            @ApiResponse(code = 404, message = "Usuário não encontrado")
    })
    @GetMapping("{id}")
    public UsuarioRespostaDTO buscar(@PathVariable("id") UUID id) {
        return usuarioService.buscar(id);
    }

    @ApiOperation(
            value = "Salva um usuário na plataforma",
            notes = "Caso não exista nenhum usuário com o id fornecido, um novo usuário será criado. " +
                    "Do contrário, caso já exista um usuário com o id fornecido, " +
                    "os dados do usuário existente serão atualizados."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Usuário criado e/ou atualizado com sucesso"),
            @ApiResponse(code = 500, message = "Erro ao criar e/ou atualizar o usuário")
    })
    @PostMapping
    public UsuarioRespostaDTO salvar(@RequestBody UsuarioDTO usuarioDTO) {
            return usuarioService.salvar(usuarioDTO);
    }

    @ApiOperation("Inativa um usuário com base no id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Usuário inativado com sucesso"),
            @ApiResponse(code = 404, message = "Usuário não encontrado")
    })
    @DeleteMapping("{id}")
    public void deletar(@PathVariable("id") UUID id) {
        usuarioService.deletar(id);
    }

    @ApiOperation("Busca a foto de determinado usuário com base no id do usuário")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Foto do usuário encontrada com sucesso"),
            @ApiResponse(code = 404, message = "Usuário não encontrado; Foto não encontrada")
    })
    @GetMapping("{id}/foto")
    public ResponseEntity<Object> getFoto(@PathVariable("id") UUID id) {
        byte[] bytes = usuarioService.getFoto(id);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }

    @ApiOperation("Busca os pets de determinado usuário com base no id do usuário")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Pets do usuário listados com sucesso"),
            @ApiResponse(code = 404, message = "Usuário não encontrado")
    })
    @GetMapping("{id}/pets")
    public List<PetRespostaDTO> getPets(@PathVariable("id") UUID id) {
        return usuarioService.getPets(id);
    }

    @ApiOperation("Envia um e-mail para redefinição da senha")
    @ApiResponses({
            @ApiResponse(code = 200, message = "E-mail enviado com sucesso"),
            @ApiResponse(code = 404, message = "Usuário não encontrado")
    })
    @GetMapping("/esqueci-minha-senha")
    public String esqueciMinhaSenha(@RequestParam(required = true) String email) {
        return usuarioService.esqueciMinhaSenha(email);
    }

    @ApiOperation("Redefini a senha")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Senha alterada com sucesso"),
            @ApiResponse(code = 400, message = "Não foi possível processar a requisição"),
            @ApiResponse(code = 404, message = "Token não encontrado")
    })
    @GetMapping("/redefinir-senha")
    public String redefinirSenha(@RequestParam(required = true) UUID token,
                                 @RequestParam(required = true) String senha)
            throws NaoEncontradoException, ErroAoProcessarException {
        return usuarioService.redefinirSenha(token, senha);
    }

    @ApiOperation("Salvar apenas a foto do usuário")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Foto salva com sucesso"),
            @ApiResponse(code = 400, message = "Erro ao salvar foto"),
            @ApiResponse(code = 404, message = "Usuário não encontrado")

    })
    @PostMapping("{usuarioId}/foto")
    public UsuarioRespostaDTO salvarFoto(@PathVariable("usuarioId") UUID usuarioId, @RequestBody FotoDTO foto) {
        return usuarioService.salvarFoto(usuarioId, foto.getImage())
                .orElseThrow(() -> new ErroAoProcessarException("Erro ao salvar foto"));
    }
}
