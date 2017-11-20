package ch.hevs.cloudio.endpoint;

/**
 * This exception is thrown if an UUID for an endpoint is invalid.
 *
 * The reason why the UUID is actually invalid is part of the exception message.
 */
public class InvalidUuidException extends Exception {
    InvalidUuidException(String uuid) {
        super("The UUID \"" + uuid + "\" is invalid!");
    }
}
