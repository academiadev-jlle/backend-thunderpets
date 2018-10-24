package br.com.academiadev.thunderpets.controller;


import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.repository.PetRepository;
import com.sun.net.httpserver.Authenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @GetMapping("/")
    private List<Pet> getAll(){
        return petRepository.findAll();
    }

    @GetMapping("/{id}")
    public Pet findById(@PathVariable("id") Long id){
        return petRepository.findById(id).get();
    }

    @GetMapping("/categoria/{id}")
    public ResponseEntity<List<Pet>> getByCategories(@PathVariable("id") String id){
        return ResponseEntity.ok(petRepository.findByStatus(id));
    }

    @PostMapping("/")
    public Pet save(@RequestBody Pet pet){
        return petRepository.saveAndFlush(pet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable("id") Long id){
        try{
            petRepository.deleteById(id);
        } catch (Exception e){
            return ResponseEntity.status(500).body(e);
        }
        return ResponseEntity.ok(true);
    }




}
