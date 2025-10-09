package services;

/**
 * Indicates that the provided username and password don't match
 */
public class IncorrectUsernameOrPasswordException extends Exception{
    public IncorrectUsernameOrPasswordException(String message) {
        super(message);
    }
    public IncorrectUsernameOrPasswordException(String message, Throwable ex) {
        super(message, ex);
    }
}
