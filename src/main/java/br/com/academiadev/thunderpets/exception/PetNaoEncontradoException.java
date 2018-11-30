package br.com.academiadev.thunderpets.exception;

public class PetNaoEncontradoException extends NaoEncontradoException {

    public PetNaoEncontradoException(String msg) {
        super(msg);
    }

    public PetNaoEncontradoException() {
        super("Pet não encontrado");
    }
}
