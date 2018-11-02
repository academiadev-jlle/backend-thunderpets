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
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private PetMapper petMapper;

    @GetMapping
    private PageImpl<PetDTO> buscar(@RequestParam(defaultValue = "0") int paginaAtual,
                                    @RequestParam(defaultValue = "10") int tamanho,
                                    @RequestParam(defaultValue = "ASC") Sort.Direction direcao,
                                    @RequestParam(defaultValue = "dataRegistro") String campoOrdenacao,
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

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable("id") UUID id) {
        Optional<Pet> pet = petRepository.findById(id);

        if (!pet.isPresent()) {
            return ResponseEntity.status(500).body(new PetNaoEncontradoException("Pet " + id + "não encontrado."));
        }

        return ResponseEntity.ok().body(petMapper.converterPetParaPetDTO(pet.get()));
    }

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
