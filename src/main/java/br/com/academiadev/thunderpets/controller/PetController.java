package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.exception.PetNaoEncontradoException;
import br.com.academiadev.thunderpets.exception.UsuarioNaoEncontradoException;
import br.com.academiadev.thunderpets.mapper.PetMapper;
import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Localizacao;
import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.FotoRepository;
import br.com.academiadev.thunderpets.repository.LocalizacaoRepository;
import br.com.academiadev.thunderpets.repository.PetRepository;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/pet")
@Api(description = "Controller de Pets")
@Transactional
public class PetController {

    private PetRepository petRepository;
    private LocalizacaoRepository localizacaoRepository;
    private FotoRepository fotoRepository;
    private PetMapper petMapper;
    private UsuarioRepository usuarioRepository;

    @Autowired
    public PetController(PetRepository petRepository,
                         LocalizacaoRepository localizacaoRepository,
                         FotoRepository fotoRepository,
                         PetMapper petMapper,
                         UsuarioRepository usuarioRepository) {
        this.petRepository = petRepository;
        this.localizacaoRepository = localizacaoRepository;
        this.fotoRepository = fotoRepository;
        this.petMapper = petMapper;
        this.usuarioRepository = usuarioRepository;
    }

    @ApiOperation(
            value = "Busca um pet com base no id.",
            notes = " O objeto é do tipo PetDTO.",
            response = PetDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Pet encontrado com sucesso."),
            @ApiResponse(code = 404, message = "Pet não encontrado.")
    })
    @GetMapping("/{id}")
    public PetDTO buscarPorId(@ApiParam(value = "ID no pet") @PathVariable("id") UUID id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNaoEncontradoException(String.format("Pet %s não encontrado", id.toString())));

        return petMapper.converterPetParaPetDTO(pet, false);
    }

    @ApiOperation(
            value = "Busca os pet com os parâmetros passados.",
            notes = " O objeto é do tipo PetDTO.",
            response = PetDTO.class,
            responseContainer = "Lists"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Pets listados com sucesso.")
    })
    @GetMapping
    public PageImpl<PetDTO> buscar(
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
                                    @RequestParam(defaultValue = "DESC") Sort.Direction direcao,
                                @ApiParam(value = "Nome da coluna que será usada para a ordenação")
                                    @RequestParam(defaultValue = "dataRegistro") String campoOrdenacao,
                                @ApiParam(value = "Escolha para buscar os pets ativos")
                                    @RequestParam(defaultValue = "true") boolean ativo) {

        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);
        Page<Pet> paginaPetsFiltrados = petRepository.findAll(Example.of(pet), paginacao);

        return (PageImpl<PetDTO>) paginaPetsFiltrados.map(p -> petMapper.converterPetParaPetDTO(p, true));
    }

    @ApiOperation(
            value = "Salva um pet na plataforma.",
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
    public PetDTO salvar(@RequestBody PetDTO petDTO) {
        Usuario usuario = usuarioRepository.findById(petDTO.getUsuarioId())
                .orElseThrow(UsuarioNaoEncontradoException::new);

        Localizacao localizacao = null;
        if (petDTO.getLocalizacao() != null) {
             localizacao = localizacaoRepository.saveAndFlush(petDTO.getLocalizacao());
        }

        final Pet pet = petRepository.saveAndFlush(
                petMapper.convertPetDTOparaPet(petDTO, localizacao, usuario));

        petDTO.getFotos().forEach(f -> {
            Foto foto = new Foto();
            foto.setImage(f);
            foto.setPet(pet);

            fotoRepository.save(foto);
        });

        return petMapper.converterPetParaPetDTO(pet, false);
    }

    @ApiOperation("Inativa um pet com base no id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Pet inativado com sucesso"),
            @ApiResponse(code = 404, message = "Pet não encontrado.")
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("{id}")
    public void excluir(@PathVariable("id") UUID id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNaoEncontradoException(String.format("Pet %s não encontrado", id)));

        pet.setAtivo(false);
        petRepository.save(pet);
    }
}
