package br.com.academiadev.thunderpets.config.security;

import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
import br.com.academiadev.thunderpets.config.dto.UsuarioCustomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebConfigSecurityAdapter extends WebSecurityConfigurerAdapter {

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManagerBean();
    }

    @Autowired
    public void authenticationManager(AuthenticationManagerBuilder builder, UsuarioRepository usuarioRepository)
            throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario usuario = new Usuario();
            usuario.setEmail("ramon@mail.com");
            usuario.setSenha(passwordEncoder().encode("admin"));
            usuario.setNome("Ramon");
            usuario.setAtivo(true);

            usuarioRepository.save(usuario);
        }

        builder.userDetailsService(email -> new UsuarioCustomDTO(usuarioRepository.findOneByEmail(email)))
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
