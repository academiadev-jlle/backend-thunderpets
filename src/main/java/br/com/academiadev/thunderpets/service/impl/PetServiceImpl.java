package br.com.academiadev.thunderpets.service.impl;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.exception.FotoNaoEncontradaException;
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
import br.com.academiadev.thunderpets.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PetServiceImpl implements PetService {

    private PetRepository petRepository;
    private LocalizacaoRepository localizacaoRepository;
    private UsuarioRepository usuarioRepository;
    private FotoRepository fotoRepository;
    private PetMapper petMapper;

    @Autowired
    public PetServiceImpl(PetRepository petRepository,
                          LocalizacaoRepository localizacaoRepository,
                          UsuarioRepository usuarioRepository,
                          FotoRepository fotoRepository,
                          PetMapper petMapper) {
        this.petRepository = petRepository;
        this.localizacaoRepository = localizacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.fotoRepository = fotoRepository;
        this.petMapper = petMapper;
    }

    @Override
    public PetDTO buscarPorId(UUID id) throws PetNaoEncontradoException {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNaoEncontradoException(String.format("Pet %s não encontrado", id.toString())));

        return petMapper.toDTO(
                pet, fotoRepository.findByPetId(pet.getId()).stream().map(Foto::getImage).collect(Collectors.toList()));
    }

    @Override
    public Page<PetDTO> buscar(LocalDate dataAchado,
                                LocalDate dataRegistro,
                                Especie especie,
                                Porte porte,
                                Sexo sexo,
                                Status status,
                                Idade idade,
                                Integer paginaAtual,
                                Integer tamanho,
                                Sort.Direction direcao,
                                String campoOrdenacao,
                                boolean ativo) {
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

        return paginaPetsFiltrados.map(p ->
                petMapper.toDTO(p, Arrays.asList(fotoRepository.findOneByPet(p).getImage())));
    }

    @Override
    public PetDTO salvar(@RequestBody PetDTO petDTO) {
        Usuario usuario = usuarioRepository.findById(petDTO.getUsuarioId())
                .orElseThrow(UsuarioNaoEncontradoException::new);

        Localizacao localizacao = null;
        if (petDTO.getLocalizacao() != null) {
            localizacao = localizacaoRepository.saveAndFlush(petDTO.getLocalizacao());
        }

        final Pet pet = petRepository.saveAndFlush(petMapper.toEntity(petDTO, localizacao, usuario));

        petDTO.getFotos().forEach(f -> {
            Foto foto = new Foto();
            foto.setImage(f);
            foto.setPet(pet);

            fotoRepository.saveAndFlush(foto);
        });

        return petMapper.toDTO(
                pet, fotoRepository.findByPetId(pet.getId()).stream().map(Foto::getImage).collect(Collectors.toList()));
    }

    @Override
    public void excluir(@PathVariable("id") UUID id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNaoEncontradoException(String.format("Pet %s não encontrado", id)));

        pet.setAtivo(false);
        petRepository.save(pet);
    }
}
