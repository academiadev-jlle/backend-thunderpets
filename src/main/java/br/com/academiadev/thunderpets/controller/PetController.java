package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Localizacao;
import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.repository.FotoRepository;
import br.com.academiadev.thunderpets.repository.LocalizacaoRepository;
import br.com.academiadev.thunderpets.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private List<Pet> buscar() {
        return petRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> buscarPorId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(petRepository.findById(id).get());
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
}
