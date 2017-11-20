package ch.hevs.cloudio.endpoint;

/**
 * Exception indicating that either the endpoint or the cloud do not have the right to change an attribute.
 */
public class CloudioAttributeConstraintException extends Exception {
    /**
     * Creates a new exception with the given message.
     *
     * @param message   The exception message.
     */
    CloudioAttributeConstraintException(final String message) {
        super(message);
    }
}
