package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.exception.PetNaoEncontradoException;
import br.com.academiadev.thunderpets.mapper.PetMapper;
import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Localizacao;
import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.repository.FotoRepository;
import br.com.academiadev.thunderpets.repository.LocalizacaoRepository;
import br.com.academiadev.thunderpets.repository.PetRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pet")
@Api(description = "Controller de Pets")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private PetMapper petMapper;

    @ApiOperation(value = "Lista os pets da plataforma",
            notes = "Retorna uma lista com os detalhes do pet."
                    + " A lista é paginada com base nos parâmetros.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pets listados com sucesso")
    })
    @GetMapping
    private PageImpl<PetDTO> buscar(@ApiParam(value = "Número da página atual")
                                        @RequestParam(defaultValue = "0") int paginaAtual,
                                    @ApiParam(value = "Número do tamanho da página")
                                        @RequestParam(defaultValue = "10") int tamanho,
                                    @ApiParam(value = "Direção da ordenação: ascendente ou descendente")
                                        @RequestParam(defaultValue = "ASC") Sort.Direction direcao,
                                    @ApiParam(value = "Nome da coluna que será usada para a ordenação")
                                        @RequestParam(defaultValue = "dataRegistro") String campoOrdenacao,
                                    @ApiParam(value = "Escolha para buscar os pets ativos")
                                        @RequestParam(defaultValue = "true") boolean ativo) {
        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);
        Page<Pet> paginaPets = petRepository.findByAtivo(ativo, paginacao);
        int totalDeElementos = (int) paginaPets.getTotalElements();

        return new PageImpl<PetDTO>(paginaPets.stream()
                .map(pet -> petMapper.converterPetParaPetDTO(pet))
                .collect(Collectors.toList()),
                paginacao,
                totalDeElementos);
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
        Optional<Pet> pet = petRepository.findById(id);

        if (!pet.isPresent()) {
            return ResponseEntity.status(500).body(new PetNaoEncontradoException("Pet " + id + "não encontrado."));
        }

        return ResponseEntity.ok().body(petMapper.converterPetParaPetDTO(pet.get()));
    }

    @ApiOperation(value = "Salva um pet na plataforma.",
            notes = " Caso não exista nenhum usuário com o id fornecido, um novo usuário será criado."
                    + " Caso contrário, os dados do pet existente serão atualizados."
    )
    @PostMapping
    public Pet salvar(@RequestBody PetDTO petDTO) {
        Localizacao localizacao = localizacaoRepository.saveAndFlush(petDTO.getLocalizacao());
        Pet petConstruido = petMapper.convertPetDTOparaPet(petDTO, localizacao);
        Pet pet = petRepository.saveAndFlush(petConstruido);

        for (Foto foto : petDTO.getFotos()) {
            foto.setPet(pet);
            fotoRepository.saveAndFlush(foto);
        }

        return pet;
    }

    @ApiOperation(value = "Inativa um pet com base no id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pet inativado com sucesso"),
            @ApiResponse(code = 500, message = "Pet não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluir(@PathVariable("id") UUID id) {
        try {
            Optional<Pet> pet = petRepository.findById(id);

            if (!pet.isPresent()) {
                return ResponseEntity.status(500).body(new PetNaoEncontradoException("Pet " + id + "não encontrado."));
            }

            Pet petSalvar = pet.get();
            petSalvar.setAtivo(false);
            petRepository.saveAndFlush(petSalvar);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }

        return ResponseEntity.ok(true);
    }
}
