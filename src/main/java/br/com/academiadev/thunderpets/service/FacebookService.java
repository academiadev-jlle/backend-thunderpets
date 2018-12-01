package br.com.academiadev.thunderpets.service;

public interface FacebookService {

    String criarUrlAutorizacaoFacebook();

    void criarTokenFacebook(String codigo);
}
