package br.com.academiadev.thunderpets.repository;

import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.model.Pet;
import br.com.academiadev.thunderpets.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PetRepository extends JpaRepository<Pet, UUID> {

    List<Pet> findByUsuarioAndAtivoIsTrue(Usuario usuario);

    @Query("select p from Pet p " +
            "where (:nome is null or lower(p.nome) like %:nome%) " +
            "and (:dataAchado is null or :dataAchado = p.dataAchado)  " +
            "and (:especie is null or :especie = p.especie) " +
            "and (:porte is null or :porte = p.porte) " +
            "and (:sexo is null or :sexo = p.sexo) " +
            "and (:status is null or :status = p.status) " +
            "and (:idade is null or :idade = p.idade) " +
            "and (:cidade is null or lower(p.localizacao.cidade) like %:cidade%) " +
            "and (:estado is null or lower(p.localizacao.estado) like %:estado%) " +
            "and (:raioDistancia is null or  " +
            "    (6371 * " +
            "      acos( " +
            "        cos(radians(cast(:latitude as double))) * " +
            "        cos(radians(cast(p.localizacao.latitude as double))) * " +
            "        cos(radians(cast(:longitude as double)) - radians(cast(p.localizacao.longitude as double))) + " +
            "        sin(radians(cast(:latitude as double))) * " +
            "        sin(radians(cast(p.localizacao.latitude as double))) " +
            "      ) " +
            "    ) <= :raioDistancia " +
            "  ) " +
            "and (:ativo is true)")
    Page<Pet> buscar(@Param("nome") String nome,
                     @Param("dataAchado") LocalDate dataAchado,
                     @Param("especie") Especie especie,
                     @Param("porte") Porte porte,
                     @Param("sexo") Sexo sexo,
                     @Param("status") Status status,
                     @Param("idade") Idade idade,
                     @Param("cidade") String cidade,
                     @Param("estado") String estado,
                     @Param("latitude") String latitude,
                     @Param("longitude") String longitude,
                     @Param("raioDistancia") Double raioDistancia,
                     Pageable paginacao);

    @Query("SELECT (6371 * " +
            "  acos( " +
            "    cos(radians(:latitude)) * " +
            "    cos(radians(cast(loc.latitude as double))) * " +
            "    cos(radians(:longitude) - radians(cast(loc.longitude as double))) + " +
            "    sin(radians(:latitude)) * " +
            "    sin(radians(cast(loc.latitude as double))) " +
            "  ) " +
            ") AS distancia " +
            "FROM Pet pet JOIN pet.localizacao loc " +
            "WHERE pet.id = :petId")
    BigDecimal findDistancia(@Param("latitude") BigDecimal latitudeCentro,
                            @Param("longitude") BigDecimal longitudeCentro,
                            @Param("petId") UUID petId);
}
