package br.com.academiadev.thunderpets.repository;

import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.model.Pet;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PetRepository extends JpaRepository<Pet, UUID> {

    Page<Pet> findByAtivo(boolean ativo, Pageable pageable);
}
