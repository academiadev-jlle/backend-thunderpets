package br.com.academiadev.thunderpets.mapper;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.model.Localizacao;
import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mappings({
        @Mapping(target = "fotos", ignore = true)
    })
    PetDTO toDTO(Pet pet, List<byte[]> fotos);


    @Mappings({
        @Mapping(source = "petDTO.id", target = "id")
    })
    Pet toEntity(PetDTO petDTO, Localizacao localizacao, Usuario usuario);
}
