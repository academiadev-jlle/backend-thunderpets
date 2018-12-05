package br.com.academiadev.thunderpets.service;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Optional;

public interface GoogleService {

    String criarUrlAutorizacaoGoogle();

    Optional<OAuth2AccessToken> login(String code);
}
