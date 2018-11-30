package br.com.academiadev.thunderpets.service;

public interface EmailService {

    String sendSimpleMessage(String para, String assunto, String conteudo);
}
