package br.com.academiadev.thunderpets.config.security.facebook;

import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;

@Service
public class FacebookConnectionSignup implements ConnectionSignUp {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String execute(Connection<?> connection) {
        UserProfile profile  = connection.fetchUserProfile();
        Usuario usuario = new Usuario();

        if (profile.getEmail() != null && !profile.getEmail().isEmpty()) {
            usuario.setEmail(profile.getEmail());
        } else {
            usuario.setEmail(profile.getUsername());
        }

        usuario.setNome(profile.getName());
        usuario.setSenha(encoder.encode(profile.getId()));

        usuario = usuarioRepository.saveAndFlush(usuario);

        return usuario.getId().toString();
    }
}
