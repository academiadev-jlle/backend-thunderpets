package br.com.academiadev.thunderpets.repository;

import br.com.academiadev.thunderpets.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    public List<Pet> findByStatus(String tipo);
}
