package ch.hevs.cloudio.endpoint;

/**
 * This exception is thrown every time the construction of a node fails.
 */
public class InvalidCloudioNodeException extends RuntimeException {
    InvalidCloudioNodeException() {
        super();
    }

    InvalidCloudioNodeException(final String message) {
        super(message);
    }

    InvalidCloudioNodeException(final Exception cause) {
        super(cause);
    }
}
