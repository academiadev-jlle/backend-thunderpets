package br.com.academiadev.thunderpets.repository;

import br.com.academiadev.thunderpets.model.LoginSocial;
import br.com.academiadev.thunderpets.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoginSocialRepository extends JpaRepository<LoginSocial, UUID> {

    Optional<LoginSocial> findByUsuario(Usuario usuario);

    Optional<LoginSocial> findByIdSocial(String idSocial);
}
