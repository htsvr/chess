package services;

/**
 * Indicates that the provided username and password don't match
 */
public class UnrecognizedAuthTokenException extends Exception{
    public UnrecognizedAuthTokenException(String message) {
        super(message);
    }
    public UnrecognizedAuthTokenException(String message, Throwable ex) {
        super(message, ex);
    }
}
