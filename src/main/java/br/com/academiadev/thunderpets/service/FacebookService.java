package br.com.academiadev.thunderpets.service;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Optional;

public interface FacebookService {

    String criarUrlAutorizacaoFacebook();

    Optional<OAuth2AccessToken> login(String codigo);
}
