package br.com.academiadev.thunderpets.config.audit;

import br.com.academiadev.thunderpets.model.Usuario;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(((Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNome());
    }
}
