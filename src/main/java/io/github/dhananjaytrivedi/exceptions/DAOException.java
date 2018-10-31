package io.github.dhananjaytrivedi.exceptions;

public class DAOException extends Exception {
    // Want to catch any sort of exception thrown from our DAO

    private final Exception originalException;

    public DAOException(Exception originalException, String message) {
        super(message);
        this.originalException = originalException;
    }
}
