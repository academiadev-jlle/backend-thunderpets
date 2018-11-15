package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.service.PetService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/pet")
@Api(description = "Controller de Pets")
public class PetController {

    @Autowired
    private PetService petService;

    @ApiOperation(value = "Lista os pets da plataforma",
            notes = "Retorna uma lista com os detalhes do pet."
                    + " A lista é paginada com base nos parâmetros.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pets listados com sucesso")
    })
    @GetMapping
    public PageImpl<PetDTO> buscar(@ApiParam(value = "Número da página atual")
                                        @RequestParam(defaultValue = "0") int paginaAtual, @ApiParam(value = "Número do tamanho da página")
                                        @RequestParam(defaultValue = "10") int tamanho, @ApiParam(value = "Direção da ordenação: ascendente ou descendente")
                                        @RequestParam(defaultValue = "ASC") Sort.Direction direcao, @ApiParam(value = "Nome da coluna que será usada para a ordenação")
                                        @RequestParam(defaultValue = "dataRegistro") String campoOrdenacao, @ApiParam(value = "Escolha para buscar os pets ativos")
                                        @RequestParam(defaultValue = "true") boolean ativo) {
        return petService.buscar(paginaAtual, tamanho, direcao, campoOrdenacao, ativo);
    }

    @ApiOperation(value = "Busca um pet com base no id.",
                    notes = " O objeto é do tipo PetDTO.",
                    response = PetDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pet encontrado com sucesso."),
            @ApiResponse(code = 500, message = "Pet não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@ApiParam(value = "ID no pet") @PathVariable("id") UUID id) {
        PetDTO petDTO;

        try {
            petDTO = petService.buscarPorId(id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }

        return ResponseEntity.ok(petDTO);
    }

    @ApiOperation(value = "Busca os pet com os parâmetros passados.",
            notes = " O objeto é do tipo PetDTO.",
            response = PetDTO.class,
            responseContainer = "Lists")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pets listados com sucesso.")
    })
    @GetMapping("/filtro")
    public PageImpl<PetDTO> filtrar(
                                @RequestParam(value = "dataAchado", required = false) LocalDate dataAchado,
                                @RequestParam(value = "dataRegistro", required = false) LocalDate dataRegistro,
                                @RequestParam(value = "especie", required = false) Especie especie,
                                @RequestParam(value = "porte", required = false) Porte porte,
                                @RequestParam(value = "sexo", required = false) Sexo sexo,
                                @RequestParam(value = "status", required = false) Status status,
                                @RequestParam(value = "idade", required = false) Idade idade,
                                @ApiParam(value = "Número da página atual")
                                    @RequestParam(defaultValue = "0") int paginaAtual,
                                @ApiParam(value = "Número do tamanho da página")
                                    @RequestParam(defaultValue = "10") int tamanho,
                                @ApiParam(value = "Direção da ordenação: ascendente ou descendente")
                                    @RequestParam(defaultValue = "ASC") Sort.Direction direcao,
                                @ApiParam(value = "Nome da coluna que será usada para a ordenação")
                                    @RequestParam(defaultValue = "dataRegistro") String campoOrdenacao,
                                @ApiParam(value = "Escolha para buscar os pets ativos")
                                    @RequestParam(defaultValue = "true") boolean ativo) {

        return petService.filtrar(dataAchado, dataRegistro, especie,
            porte, sexo, status,
            idade, paginaAtual, tamanho,
            direcao, campoOrdenacao, ativo);
    }

    @ApiOperation(value = "Salva um pet na plataforma.",
            notes = " Caso não exista nenhum pet com o id fornecido, um novo pet será criado."
                    + " Caso contrário, os dados do pet existente serão atualizados."
    )
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Authorization", value = "Authorization token", required = true, paramType = "header")
    })
    public ResponseEntity<Object> salvar(@RequestBody PetDTO petDTO) {

        return ResponseEntity.ok(petService.salvar(petDTO));
    }

    @ApiOperation(value = "Inativa um pet com base no id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pet inativado com sucesso"),
            @ApiResponse(code = 500, message = "Pet não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluir(@PathVariable("id") UUID id) {
        return petService.excluir(id);
    }
}
