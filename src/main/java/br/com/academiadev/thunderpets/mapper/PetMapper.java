package br.com.academiadev.thunderpets.mapper;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.repository.FotoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PetMapper {

    @Autowired
    private FotoRepository fotoRepository;

    public PetDTO converterPetParaPetDTO(Pet pet) {
        List<Foto> fotos = fotoRepository.findByPetId(pet.getId());

        PetDTO petDTO = PetDTO.builder()
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
                .usuario(pet.getUsuario())
                .localizacao(pet.getLocalizacao())
                .fotos(fotos)
                .ativo(pet.isAtivo())
                .build();

        return petDTO;
    }
}
