package net.kimleo.rec.exception;

public class ResourceAccessException extends RuntimeException {
    public ResourceAccessException(String message, Exception ex) {
        super(message, ex);
    }
}
