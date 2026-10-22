package service;

/**
 * Indicates a key value already exists in the Data Access
 */
public class AlreadyExistsException extends Exception{
    public AlreadyExistsException(String message) {
        super(message);
    }
}