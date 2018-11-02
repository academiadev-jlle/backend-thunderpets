package br.com.academiadev.thunderpets.repository;

import br.com.academiadev.thunderpets.model.Pet;
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

    @Query("select p from Pet p where"
            + " (:dataAchado is null or p.dataAchado like %:dataAchado%)"
            + " and (:dataRegistro is null or p.dataRegistro like %:dataRegistro%)"
            + " and (:especie is null or p.especie like %:especie%)"
            + " and (:porte is null or p.porte like %:porte%)"
            + " and (:sexo is null or p.sexo like %:sexo%)"
            + " and (:status is null or p.status like %:status%)"
            + " and (:idade is null or p.idade like %:idade%)")
    List<Pet> findFiltro(@Param("dataAchado") LocalDate dataAchado,
                         @Param("dataRegistro") LocalDate dataRegistro,
                         @Param("especie") String especie,
                         @Param("porte") String porte,
                         @Param("sexo") String sexo,
                         @Param("status") String status,
                         @Param("idade") String idade);
}
