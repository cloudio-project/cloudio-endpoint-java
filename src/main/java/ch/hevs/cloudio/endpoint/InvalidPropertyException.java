package ch.hevs.cloudio.endpoint;

/**
 * This exception is thrown if either a property is missing or invalid during endpoint initialization.
 */
public class InvalidPropertyException extends Exception {
    InvalidPropertyException(String message) {
        super(message);
    }
}
