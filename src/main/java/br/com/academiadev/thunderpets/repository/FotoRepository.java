package br.com.academiadev.thunderpets.repository;

import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FotoRepository extends JpaRepository<Foto, UUID> {

    List<Foto> findFirstByPetId(UUID id);

    Foto findOneByPet(Pet pet);
}
