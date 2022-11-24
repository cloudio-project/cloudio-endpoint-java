package ch.hevs.cloudio.endpoint;

import java.io.IOException;

/**
 * This exception is thrown whenever an error occur when adding new nodes to an endpoint using the factory feature.
 */
public class CloudioFactoryException extends IOException {
    CloudioFactoryException(final String message) {
        super(message);
    }

    CloudioFactoryException(final Exception cause) {
        super(cause);
    }
}
