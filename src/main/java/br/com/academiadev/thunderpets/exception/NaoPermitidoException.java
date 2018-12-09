package br.com.academiadev.thunderpets.exception;

public class NaoPermitidoException extends RuntimeException {

    public NaoPermitidoException() {
    }

    public NaoPermitidoException(String message) {
        super(message);
    }
}
