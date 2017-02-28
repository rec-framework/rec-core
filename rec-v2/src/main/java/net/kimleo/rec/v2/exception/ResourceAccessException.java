package net.kimleo.rec.v2.exception;

public class ResourceAccessException extends RuntimeException {
    public ResourceAccessException(String message, Exception ex) {
        super(message, ex);
    }
}
