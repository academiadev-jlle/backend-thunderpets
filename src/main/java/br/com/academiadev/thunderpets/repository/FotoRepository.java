package br.com.academiadev.thunderpets.repository;

import br.com.academiadev.thunderpets.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FotoRepository extends JpaRepository<Foto, UUID> {

}
