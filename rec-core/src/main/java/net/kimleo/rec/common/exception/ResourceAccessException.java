package net.kimleo.rec.common.exception;

public class ResourceAccessException extends RuntimeException {
    public ResourceAccessException(String message, Throwable ex) {
        super(message, ex);
    }
}
