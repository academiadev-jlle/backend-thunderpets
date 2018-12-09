package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.dto.PetRespostaDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.service.PetService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("pet")
@Api("Controller de Pets")
@Transactional
public class PetController {

    private PetService service;

    @Autowired
    public PetController(PetService service) {
        this.service = service;
    }

    @ApiOperation(
            value = "Busca os pet com os parâmetros passados",
            notes = " O objeto é do tipo PetRespostaDTO.",
            response = PetRespostaDTO.class,
            responseContainer = "Lists"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Pets listados com sucesso")
    })
    @GetMapping
    public Page<PetRespostaDTO> buscar(@RequestParam(value = "nome", required = false) String nome,
                                       @RequestParam(value = "dataAchado", required = false) LocalDate dataAchado,
                                       @RequestParam(value = "especie", required = false) Especie especie,
                                       @RequestParam(value = "porte", required = false) Porte porte,
                                       @RequestParam(value = "sexo", required = false) Sexo sexo,
                                       @RequestParam(value = "status", required = false) Status status,
                                       @RequestParam(value = "idade", required = false) Idade idade,
                                       @RequestParam(value = "buscarPorLocalidade", required = false) TipoPesquisaLocalidade tipoPesquisaLocalidade,
                                       @RequestParam(value = "cidade", required = false) String cidade,
                                       @RequestParam(value = "estado", required = false) String estado,
                                       @RequestParam(value = "latitudeUsuario", required = false) BigDecimal latitude,
                                       @RequestParam(value = "longitudeUsuario", required = false) BigDecimal longitude,
                                       @RequestParam(value = "raioDistancia", required = false) Integer raioDistancia,
                                       @ApiParam(value = "Número da página atual")
                                           @RequestParam(defaultValue = "0") Integer paginaAtual,
                                       @ApiParam(value = "Número do tamanho da página")
                                           @RequestParam(defaultValue = "10") Integer tamanho,
                                       @ApiParam(value = "Direção da ordenação: ascendente ou descendente")
                                           @RequestParam(defaultValue = "DESC") Sort.Direction direcao,
                                       @ApiParam(value = "Nome da coluna que será usada para a ordenação")
                                           @RequestParam(defaultValue = "dataRegistro") String campoOrdenacao,
                                       @ApiParam(value = "Escolha para buscar os pets ativos ou inativos")
                                           @RequestParam(defaultValue = "true") boolean ativo) {

        return service.buscar(nome,
                dataAchado,
                especie,
                porte,
                sexo,
                status,
                idade,
                tipoPesquisaLocalidade,
                cidade,
                estado,
                latitude,
                longitude,
                raioDistancia,
                paginaAtual,
                tamanho,
                direcao,
                campoOrdenacao,
                ativo);
    }

    @ApiOperation(
            value = "Busca um pet com base no id",
            notes = " O objeto é do tipo PetRespostaDTO.",
            response = PetRespostaDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Pet encontrado com sucesso"),
            @ApiResponse(code = 404, message = "Pet não encontrado")
    })
    @GetMapping("/{id}")
    public PetRespostaDTO buscarPorId(@ApiParam(value = "ID no pet") @PathVariable("id") UUID id) {
        return service.buscarPorId(id);
    }

    @ApiOperation(
            value = "Salva um pet na plataforma",
            notes = "Caso não exista nenhum pet com o id fornecido, um novo pet será criado. " +
                    "Caso contrário, os dados do pet existente serão atualizados."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Authorization",
                    value = "Authorization token",
                    required = true,
                    paramType = "header"
            )
    })
    @PostMapping
    public PetRespostaDTO salvar(@RequestBody PetDTO petDTO) {
        return service.salvar(petDTO);
    }

    @ApiOperation("Inativa um pet com base no id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Pet inativado com sucesso"),
            @ApiResponse(code = 404, message = "Pet não encontrado")
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("{id}")
    public void excluir(@PathVariable("id") UUID id) {
        service.excluir(id);
    }
}
