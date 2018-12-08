package br.com.academiadev.thunderpets.service;

public interface EmailService {

    String enviaMensagemSimples(String para, String assunto, String conteudo);
}
