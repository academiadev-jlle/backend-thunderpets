package br.com.academiadev.thunderpets.exception;

public class ErroAoProcessarException extends RuntimeException {

    public ErroAoProcessarException() {
    }

    public ErroAoProcessarException(String message) {
        super(message);
    }
}
