package ch.hevs.cloudio.endpoint;

import java.lang.reflect.Type;

/**
 * This exception is thrown every time the construction of an attribute fails.
 */
public class InvalidCloudioAttributeException extends RuntimeException {
    InvalidCloudioAttributeException(Type type) {
        super("Data type " + type + " not supported by cloud.io!");
    }

    InvalidCloudioAttributeException(String message) {
        super(message);
    }
}
