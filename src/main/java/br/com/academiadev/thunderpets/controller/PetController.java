package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Localizacao;
import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.repository.FotoRepository;
import br.com.academiadev.thunderpets.repository.LocalizacaoRepository;
import br.com.academiadev.thunderpets.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    @Autowired
    private FotoRepository fotoRepository;

    @GetMapping
    private PageImpl<PetDTO> buscar(@RequestParam(defaultValue = "0") int paginaAtual,
                             @RequestParam(defaultValue = "10") int tamanho,
                             @RequestParam(defaultValue = "ASC") Sort.Direction direcao,
                             @RequestParam(defaultValue = "nome") String campoOrdenação) {
        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenação);
        Page<Pet> paginaPets = petRepository.findAll(paginacao);
        int totalDeElementos = (int) paginaPets.getTotalElements();

        return new PageImpl<PetDTO>(paginaPets.stream()
                .map(pet -> converterPetParaPetDTO(pet))
                .collect(Collectors.toList())
                ,paginacao
                ,totalDeElementos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> buscarPorId(@PathVariable("id") UUID id) {
        return Optional.ofNullable(petRepository.findById(id))
                .map(pet -> ResponseEntity.ok().body(converterPetParaPetDTO(pet.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{id}")
    public List<Pet> buscarPorCategoria(@PathVariable("id") String id) {
        return petRepository.findByStatus(id);
    }

    @PostMapping
    public Pet salvar(@RequestBody PetDTO petDTO) {
        Localizacao localizacao = localizacaoRepository.saveAndFlush(petDTO.getLocalizacao());

        Pet petBuilt = Pet.builder()
                .id(petDTO.getId())
                .nome(petDTO.getNome())
                .descricao(petDTO.getDescricao())
                .dataAchado(petDTO.getDataAchado())
                .dataRegistro(petDTO.getDataRegistro())
                .especie(petDTO.getEspecie())
                .porte(petDTO.getPorte())
                .sexo(petDTO.getSexo())
                .status(petDTO.getStatus())
                .idade(petDTO.getIdade())
                .usuario(petDTO.getUsuario())
                .localizacao(localizacao)
                .ativo(petDTO.isAtivo())
                .build();

        Pet pet = petRepository.saveAndFlush(petBuilt);

        for (Foto foto : petDTO.getFotos()) {
            foto.setPet(pet);
            fotoRepository.saveAndFlush(foto);
        }

        return pet;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluir(@PathVariable("id") UUID id) {
        try {
            Pet pet = petRepository.findById(id).get();
            pet.setAtivo(false);
            petRepository.saveAndFlush(pet);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }

        return ResponseEntity.ok(true);
    }

    public PetDTO converterPetParaPetDTO(Pet pet) {
        List<Foto> fotos = fotoRepository.findByPetId(pet.getId());

        PetDTO petDTO = PetDTO.builder()
                .id(pet.getId())
                .nome(pet.getNome())
                .descricao(pet.getDescricao())
                .dataAchado(pet.getDataAchado())
                .dataRegistro(pet.getDataRegistro())
                .especie(pet.getEspecie())
                .porte(pet.getPorte())
                .sexo(pet.getSexo())
                .status(pet.getStatus())
                .idade(pet.getIdade())
                .usuario(pet.getUsuario())
                .localizacao(pet.getLocalizacao())
                .fotos(fotos)
                .ativo(pet.isAtivo())
                .build();

        return petDTO;
    }
}
