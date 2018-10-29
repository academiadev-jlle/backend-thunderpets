package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.model.Pet;
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

    @GetMapping("/")
    private List<Pet> buscar() {
        return petRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> buscar(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(petRepository.findById(id).get());
    }

    @GetMapping("/categoria/{id}")
    public List<Pet> buscarPorCategoria(@PathVariable("id") String id) {
        return petRepository.findByStatus(id);
    }

    @PostMapping("/")
    public Pet salvar(@RequestBody Pet pet) {
        return petRepository.saveAndFlush(pet);
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
