package br.com.academiadev.thunderpets.service;

public interface FacebookService {

    String criarUrlAutorizacaoFacebook();

    String criarTokenFacebook(String codigo);

    Object getUsuarioFacebook(String token);
}
