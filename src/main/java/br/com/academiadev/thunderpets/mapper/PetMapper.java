package br.com.academiadev.thunderpets.mapper;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Localizacao;
import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.FotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PetMapper {

    @Autowired
    private FotoRepository fotoRepository;

    public PetDTO converterPetParaPetDTO(Pet pet, boolean listagem) {
        List<byte[]> fotos = fotoRepository.findByPetId(pet.getId()).stream()
                .map(Foto::getImage).collect(Collectors.toList());

        if (listagem && fotos.size() > 1) {
            fotos = new ArrayList<>(fotos.subList(0, 1));
        }

        return PetDTO.builder()
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
                .usuarioId(pet.getUsuario().getId())
                .localizacao(pet.getLocalizacao())
                .fotos(fotos)
                .ativo(pet.isAtivo())
                .build();
    }

    public Pet convertPetDTOparaPet(PetDTO petDTO, Localizacao localizacao, Usuario usuario) {
        return Pet.builder()
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
                .usuario(usuario)
                .localizacao(localizacao)
                .ativo(petDTO.isAtivo())
                .build();
    }
}
