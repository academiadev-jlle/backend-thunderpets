package br.com.academiadev.thunderpets.exception;

public class UsuarioNaoEncontradoException extends NaoEncontradoException {

    public UsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public UsuarioNaoEncontradoException() {
        super("Usuário não encontrado");
    }
}
