package net.kimleo.rec.v2.exception;

public class InitializationException extends RuntimeException {
    public InitializationException(String s, Exception ex) {
        super(s, ex);
    }
}
