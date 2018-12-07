package br.com.academiadev.thunderpets.service.impl;

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
import br.com.academiadev.thunderpets.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public Page<PetDTO> buscar(String nome,
                               LocalDate dataAchado,
                               LocalDate dataRegistro,
                               Especie especie,
                               Porte porte,
                               Sexo sexo,
                               Status status,
                               Idade idade,
                               TipoPesquisaLocalidade tipoPesquisaLocalidade,
                               String cidade,
                               String estado,
                               BigDecimal latitude,
                               BigDecimal longitude,
                               Integer raioDistancia,
                               Integer paginaAtual,
                               Integer tamanho,
                               Sort.Direction direcao,
                               String campoOrdenacao,
                               boolean ativo) {

        Localizacao localizacao = new Localizacao();
        if (tipoPesquisaLocalidade != null && tipoPesquisaLocalidade.equals(TipoPesquisaLocalidade.CIDADE_ESTADO)) {
            localizacao = Localizacao.builder()
                    .cidade(cidade)
                    .estado(estado)
                    .build();
        }

        Pet pet = Pet.builder()
                .dataAchado(dataAchado)
                .dataRegistro(dataRegistro)
                .especie(especie)
                .porte(porte)
                .sexo(sexo)
                .status(status)
                .idade(idade)
                .ativo(ativo)
                .localizacao(localizacao)
                .build();

        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);
        Page<Pet> paginaPetsFiltrados = petRepository.findAll(Example.of(pet, ExampleMatcher.matching().withIgnoreCase()), paginacao);

        Page<PetDTO> paginaPetsFiltradosDTO = paginaPetsFiltrados.map(p -> petMapper.toDTO(p,
                fotoRepository.findByPetId(p.getId()).stream().map(Foto::getImage).collect(Collectors.toList())));

        if (latitude != null && longitude != null) {
            paginaPetsFiltradosDTO.map((petDTO) -> {
                petDTO.setDistancia(petRepository.findDistancia(latitude, longitude, petDTO.getId()));
                
                return petDTO;
            });

            if (tipoPesquisaLocalidade != null && tipoPesquisaLocalidade.equals(TipoPesquisaLocalidade.RAIO_DISTANCIA) && raioDistancia != null) {
                return (Page<PetDTO>) paginaPetsFiltradosDTO.filter(dto -> dto.getDistancia().compareTo(new BigDecimal(raioDistancia)) <= 0);
            }
        }

        return paginaPetsFiltradosDTO;
    }

    @Override
    public PetDTO buscarPorId(UUID id) throws PetNaoEncontradoException {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNaoEncontradoException(String.format("Pet %s não encontrado", id.toString())));

        return petMapper.toDTO(
                pet, fotoRepository.findByPetId(pet.getId()).stream().map(Foto::getImage).collect(Collectors.toList()));
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
