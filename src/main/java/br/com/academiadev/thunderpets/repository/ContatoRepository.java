package br.com.academiadev.thunderpets.repository;

import br.com.academiadev.thunderpets.model.Contato;
import br.com.academiadev.thunderpets.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, UUID> {

    public List<Contato> findByUsuario(Usuario usuario);
}
