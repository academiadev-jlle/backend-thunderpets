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
            @Mapping(source = "pet.usuario.id", target = "usuarioId")
    })
    PetDTO toDTO(Pet pet, List<byte[]> fotos);


    @Mappings({
            @Mapping(source = "petDTO.id", target = "id"),
            @Mapping(source = "petDTO.nome", target = "nome"),
            @Mapping(source = "petDTO.descricao", target = "descricao"),
            @Mapping(source = "petDTO.ativo", target = "ativo")
    })
    Pet toEntity(PetDTO petDTO, Localizacao localizacao, Usuario usuario);
}
