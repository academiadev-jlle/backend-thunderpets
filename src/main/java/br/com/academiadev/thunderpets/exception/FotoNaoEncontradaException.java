package br.com.academiadev.thunderpets.exception;

public class FotoNaoEncontradaException extends NaoEncontradoException {

    public FotoNaoEncontradaException(String mensagem) {
        super(mensagem);
    }

    public FotoNaoEncontradaException() {
        super("Foto não encontrada");
    }
}
