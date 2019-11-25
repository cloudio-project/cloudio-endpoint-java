package ch.hevs.cloudio.endpoint;

/**
 * This exception is thrown if the initialization of an attribute was not possible. The main reason for this is that
 * either the attribute has already been initialized or the attribute is already part of an endpoint structure and
 * thus can not be initialized anymore.
 */
public class CloudioAttributeInitializationException extends Exception {
    /**
     * Creates a new exception with the given message.
     *
     * @param message   The exception message.
     */
    CloudioAttributeInitializationException(final String message) {
        super(message);
    }
}
