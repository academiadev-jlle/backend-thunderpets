package br.com.academiadev.thunderpets.repository;

import br.com.academiadev.thunderpets.model.Localizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LocalizacaoRepository extends JpaRepository<Localizacao, UUID> {
}
