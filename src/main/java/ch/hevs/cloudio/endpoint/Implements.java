package ch.hevs.cloudio.endpoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used by cloud.io to declare that the node implements a a given interface. An interface in
 * cloud.io is very similar to Java interface with the difference that the cloud.io
 * interface defines an object structure and not methods. An interface defines a scheme what attributes and child
 * objects a node should have at least. A node is conform to such a scheme if it contains along other objects the
 * structure of the interface. It can contain more attributes or child objects and it would still implement the
 * declared interface.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Implements {
    /**
     * List of interfaces that the node implements.
     *
     * @return List of implemented interfaces.
     */
    String[] value();
}
