package br.com.academiadev.thunderpets.mapper;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Localizacao;
import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.repository.FotoRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PetMapper {

    @Autowired
    public FotoRepository fotoRepository;


    public abstract PetDTO toDTO(Pet pet);

    @Mappings({
        @Mapping(source = "local", target = "localizacao")
    })
    public abstract Pet toEntity(PetDTO petDTO, Localizacao local);

    @AfterMapping
    void setFoto(Pet pet, @MappingTarget PetDTO petDTO) {
        List<Foto> fotos = fotoRepository.findByPetId(pet.getId());
        petDTO.setFotos(fotos);
    }

}
