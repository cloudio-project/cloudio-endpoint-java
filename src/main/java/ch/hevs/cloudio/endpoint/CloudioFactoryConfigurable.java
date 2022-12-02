package ch.hevs.cloudio.endpoint;

import java.util.Map;

/**
 * The CloudioFactoryConfigurable interface declares the methods that can be implemented by any custom object containing
 * custom properties. Those custom properties are retrieved by the factory and passed to the object via the
 * setConfigurationProperties method.
 */
public interface CloudioFactoryConfigurable {

    /**
     * A CloudioFactoryConfigurable implementation will be given its properties when instantiate by the factory.
     *
     * @param properties Map of properties that have been deserialized by the factory.
     */
    void setConfigurationProperties(Map<String, Object> properties);
}
