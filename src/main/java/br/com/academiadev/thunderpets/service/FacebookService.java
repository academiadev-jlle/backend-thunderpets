package br.com.academiadev.thunderpets.service;

import br.com.academiadev.thunderpets.dto.LoginSocialDTO;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Optional;

public interface FacebookService {

    String criarUrlAutorizacaoFacebook();

    Optional<OAuth2AccessToken> login(LoginSocialDTO dto);
}
