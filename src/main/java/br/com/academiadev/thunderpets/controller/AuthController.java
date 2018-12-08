package br.com.academiadev.thunderpets.controller;

import br.com.academiadev.thunderpets.dto.FacebookLoginDTO;
import br.com.academiadev.thunderpets.exception.UsuarioNaoEncontradoException;
import br.com.academiadev.thunderpets.mapper.UsuarioMapper;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.ContatoRepository;
import br.com.academiadev.thunderpets.service.FacebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;

@RestController
@RequestMapping("oauth")
@Transactional
public class AuthController {

    private ConsumerTokenServices tokenServices;
    private ContatoRepository contatoRepository;
    private UsuarioMapper usuarioMapper;
    private FacebookService facebookService;

    @Autowired
    public AuthController(ConsumerTokenServices tokenServices,
                          ContatoRepository contatoRepository,
                          UsuarioMapper usuarioMapper,
                          FacebookService facebookService) {
        this.tokenServices = tokenServices;
        this.contatoRepository = contatoRepository;
        this.usuarioMapper = usuarioMapper;
        this.facebookService = facebookService;
    }

    @GetMapping("whoAmI")
    public Object whoAmI() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof String) {
            return authentication.getPrincipal();
        }

        Usuario usuario = (Usuario) authentication.getPrincipal();

        return usuarioMapper.toDTO(usuario, contatoRepository.findByUsuario(usuario));
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

    @GetMapping("facebook/getUrl")
    public String createFacebookAuthorization() {
        return facebookService.criarUrlAutorizacaoFacebook();
    }

    @PostMapping("facebook/login")
    public OAuth2AccessToken createFacebookAccessToken(@RequestBody FacebookLoginDTO dto) {
        return facebookService.login(dto.getCode()).orElseThrow(UsuarioNaoEncontradoException::new);
    }
}
