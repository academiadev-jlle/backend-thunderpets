package br.com.academiadev.thunderpets.service;

public interface GoogleService {

    String criarUrlAutorizacaoGoogle();

    public void login(String code);
}
