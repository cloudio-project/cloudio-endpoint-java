package ch.hevs.cloudio.endpoint;

/**
 * This exception is thrown every time the construction of an object fails.
 */
public class InvalidCloudioObjectException extends RuntimeException {
    InvalidCloudioObjectException(String message) {
        super(message);
    }

    InvalidCloudioObjectException(Exception cause) {
        super(cause);
    }
}
