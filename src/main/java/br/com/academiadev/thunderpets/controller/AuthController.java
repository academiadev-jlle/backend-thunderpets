package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.exception.UsuarioNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("oauth")
@Transactional
public class AuthController {

    @Autowired
    private ConsumerTokenServices tokenServices;

    @GetMapping("whoAmI")
    public Object whoAmI() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("logout")
    public ResponseEntity<?> logout() {
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();

        if (details instanceof OAuth2AuthenticationDetails) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(tokenServices.revokeToken(((OAuth2AuthenticationDetails) details).getTokenValue()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new UsuarioNaoEncontradoException("Nenhum usuário está logado no sistema"));
    }
}
