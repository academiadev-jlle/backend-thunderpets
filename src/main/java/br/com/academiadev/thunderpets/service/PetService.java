package br.com.academiadev.thunderpets.service;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.exception.FotoNaoEncontradaException;
import br.com.academiadev.thunderpets.exception.PetNaoEncontradoException;
import br.com.academiadev.thunderpets.mapper.PetMapper;
import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Localizacao;
import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.FotoRepository;
import br.com.academiadev.thunderpets.repository.LocalizacaoRepository;
import br.com.academiadev.thunderpets.repository.PetRepository;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PetMapper petMapper;

    public PageImpl<PetDTO> buscar(int paginaAtual, int tamanho, Sort.Direction direcao, String campoOrdenacao, boolean ativo) {
        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);
        Page<Pet> paginaPets = petRepository.findByAtivo(ativo, paginacao);
        int totalDeElementos = (int) paginaPets.getTotalElements();

        return new PageImpl<PetDTO>(paginaPets.stream().map(pet -> petMapper.converterPetParaPetDTO(pet,true)).collect(Collectors.toList()), paginacao, totalDeElementos);
    }

    public PetDTO buscarPorId(UUID id) throws PetNaoEncontradoException {

        Optional<Pet> pet = petRepository.findById(id);

        if (!pet.isPresent()) {
            throw new PetNaoEncontradoException("Pet " + id + "não encontrado.");
        }

        return petMapper.converterPetParaPetDTO(pet.get(), true);
    }

    public PageImpl<PetDTO> filtrar(LocalDate dataAchado, LocalDate dataRegistro, Especie especie,
                                    Porte porte, Sexo sexo, Status status,
                                    Idade idade, int paginaAtual, int tamanho,
                                    Sort.Direction direcao, String campoOrdenacao, boolean ativo) {
        Pet pet = Pet.builder()
            .dataAchado(dataAchado)
            .dataRegistro(dataRegistro)
            .especie(especie)
            .porte(porte)
            .sexo(sexo)
            .status(status)
            .idade(idade)
            .ativo(ativo)
            .build();

        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);
        Page<Pet> paginaPetsFiltrados = petRepository.findAll(Example.of(pet), paginacao);
        int totalDeElementos = (int) paginaPetsFiltrados.getTotalElements();

        return new PageImpl<PetDTO>(paginaPetsFiltrados.stream().map(p -> petMapper.converterPetParaPetDTO(p, false)).collect(Collectors.toList()), paginacao, totalDeElementos);
    }

    public Object salvar(@RequestBody PetDTO petDTO) {

        Localizacao local = localizacaoRepository.saveAndFlush(petDTO.getLocalizacao());
        Usuario usuario = usuarioRepository.findById(petDTO.getUsuarioId()).get();
        Pet petConstruido = petMapper.convertPetDTOparaPet(petDTO, local, usuario);
        Pet pet = petRepository.saveAndFlush(petConstruido);

        for (byte[] b : petDTO.getFotos()) {
            Foto foto = new Foto();
            foto.setImage(b);
            foto.setPet(pet);
            fotoRepository.saveAndFlush(foto);
        }

        return pet;
    }

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
