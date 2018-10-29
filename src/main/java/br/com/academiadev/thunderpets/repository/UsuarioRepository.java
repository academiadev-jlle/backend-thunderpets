package br.com.academiadev.thunderpets.repository;

import br.com.academiadev.thunderpets.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findOneByEmail(String email);
}
