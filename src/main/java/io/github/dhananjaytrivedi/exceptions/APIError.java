package io.github.dhananjaytrivedi.exceptions;

public class APIError extends RuntimeException {
    private final int status;
    public APIError(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
