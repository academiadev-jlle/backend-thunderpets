package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Localizacao;
import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.repository.FotoRepository;
import br.com.academiadev.thunderpets.repository.LocalizacaoRepository;
import br.com.academiadev.thunderpets.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

        Pet pet = new Pet(petDTO.getId(), petDTO.getNome(),
                petDTO.getDescricao(), petDTO.getDataAchado(),
                petDTO.getDataRegistro(), petDTO.getEspecie(),
                petDTO.getPorte(), petDTO.getSexo(), petDTO.getStatus(),
                petDTO.getIdade(), petDTO.getUsuario(),
                localizacao, petDTO.isAtivo());

        petRepository.saveAndFlush(pet);

        for (Foto foto : petDTO.getFotos()) {
            foto.setPet(pet);
            fotoRepository.saveAndFlush(foto);
        }

        return pet;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluir(@PathVariable("id") UUID id) {
        try {
            petRepository.deleteById(id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }

        return ResponseEntity.ok(true);
    }
}
