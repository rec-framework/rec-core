package net.kimleo.rec.exception;

public class InitializationException extends RuntimeException {
    public InitializationException(String s, Exception ex) {
        super(s, ex);
    }

    public InitializationException(String message) {
        super(message);
    }
}
