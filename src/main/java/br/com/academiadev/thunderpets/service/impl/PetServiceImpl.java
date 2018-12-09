package br.com.academiadev.thunderpets.service.impl;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.dto.PetRespostaDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.exception.ErroAoProcessarException;
import br.com.academiadev.thunderpets.exception.NaoPermitidoException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    public Page<PetRespostaDTO> buscar(String nome,
                                       LocalDate dataAchado,
                                       Especie especie,
                                       Porte porte,
                                       Sexo sexo,
                                       Status status,
                                       Idade idade,
                                       TipoPesquisaLocalidade tipoPesquisaLocalidade,
                                       String cidade,
                                       String estado,
                                       String latitude,
                                       String longitude,
                                       Double raioDistancia,
                                       Integer paginaAtual,
                                       Integer tamanho,
                                       Sort.Direction direcao,
                                       String campoOrdenacao) {
        PageRequest paginacao = PageRequest.of(paginaAtual, tamanho, direcao, campoOrdenacao);

        Page<Pet> paginaPetsFiltrados = petRepository.buscar(nome,
                dataAchado,
                especie,
                porte,
                sexo,
                status,
                idade,
                (tipoPesquisaLocalidade != null && tipoPesquisaLocalidade.equals(TipoPesquisaLocalidade.CIDADE_ESTADO))
                        ? (cidade != null ? cidade.toLowerCase() : null) : null,
                (tipoPesquisaLocalidade != null && tipoPesquisaLocalidade.equals(TipoPesquisaLocalidade.CIDADE_ESTADO))
                        ? (estado != null ? estado.toLowerCase() : null) : null,
                latitude != null ? latitude : "null",
                longitude != null ? longitude : "null",
                raioDistancia,
                paginacao);

        PageImpl<PetRespostaDTO> paginaPetsFiltradosDTO = (PageImpl<PetRespostaDTO>) paginaPetsFiltrados
                .map(p -> petMapper.toDTO(p, fotoRepository.findByPetId(p.getId()).stream()
                        .map(Foto::getImage).collect(Collectors.toList())));


        if(tipoPesquisaLocalidade != null && tipoPesquisaLocalidade.equals(TipoPesquisaLocalidade.RAIO_DISTANCIA)) {
            if (latitude == null || longitude == null) {
                throw new ErroAoProcessarException("Para buscas por raio de distância é necessário informar a latitude e longitude do usuário atual.");
            }

            if(raioDistancia != null) {
                paginaPetsFiltradosDTO.map((petDTO) -> {
                    petDTO.setDistancia(petRepository.findDistancia(new BigDecimal(latitude), new BigDecimal(longitude), petDTO.getId()));
                    return petDTO;
                });
            }
        }

        return paginaPetsFiltradosDTO;
    }

    @Override
    public PetRespostaDTO buscarPorId(UUID id) throws PetNaoEncontradoException {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNaoEncontradoException(String.format("Pet %s não encontrado", id.toString())));

        return petMapper.toDTO(
                pet, fotoRepository.findByPetId(pet.getId()).stream().map(Foto::getImage).collect(Collectors.toList()));
    }

    @Override
    public PetRespostaDTO salvar(PetDTO petDTO) {
        Usuario usuario = usuarioRepository.findById(petDTO.getUsuarioId())
                .orElseThrow(UsuarioNaoEncontradoException::new);

        Localizacao localizacao = null;
        if (petDTO.getLocalizacao() != null) {
            localizacao = localizacaoRepository.saveAndFlush(petDTO.getLocalizacao());
        }

        if (petDTO.getId() != null) {
            Pet pet = petRepository.findById(petDTO.getId()).orElse(new Pet());

            if (!currentUser().getId().equals(pet.getUsuario().getId())) {
                throw new NaoPermitidoException("Esse pet não pertence a esse usuário");
            }
        }

        final Pet pet = petRepository.saveAndFlush(petMapper.toEntity(petDTO, localizacao, usuario));

        fotoRepository.findByPetId(pet.getId()).forEach(foto -> {
            fotoRepository.delete(foto);
        });

        List<byte[]> fotos = new ArrayList<>();
        for (byte[] f : petDTO.getFotos()) {
            Foto foto = new Foto();
            foto.setImage(f);
            foto.setPet(pet);

            fotos.add(fotoRepository.saveAndFlush(foto).getImage());
        }

        return petMapper.toDTO(pet, fotos);
    }

    @Override
    public void excluir(UUID id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNaoEncontradoException(String.format("Pet %s não encontrado", id)));

        pet.setAtivo(false);
        petRepository.save(pet);
    }

    private Usuario currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return (Usuario) authentication.getPrincipal();
    }
}
