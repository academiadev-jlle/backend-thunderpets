package br.com.academiadev.thunderpets.repository;

import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface PetRepository extends JpaRepository<Pet, UUID> {

    Page<Pet> findByAtivo(boolean ativo, Pageable pageable);

    List<Pet> findByUsuario(Usuario usuario);

    @Query(value = "SELECT (6371 * " +
            "  acos( " +
            "    cos(radians(:latitude)) * " +
            "    cos(radians(cast(loc.latitude as double precision))) * " +
            "    cos(radians(:longitude) - radians(cast(loc.longitude as double precision))) + " +
            "    sin(radians(:latitude)) * " +
            "    sin(radians(cast(loc.latitude as double precision))) " +
            "  ) " +
            ") AS distancia " +
            "FROM Pet pet " +
            "INNER JOIN Localizacao loc ON (pet.localizacao_id = loc.id) " +
            "WHERE pet.id = :petId", nativeQuery = true)
    BigDecimal findDistancia(@Param("latitude") BigDecimal latitudeCentro,
                            @Param("longitude") BigDecimal longitudeCentro,
                            @Param("petId") UUID petId);
}
