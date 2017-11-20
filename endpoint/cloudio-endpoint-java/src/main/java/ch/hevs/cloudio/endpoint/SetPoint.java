package ch.hevs.cloudio.endpoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark an attribute as a set point (constraint). Set points are desired or target values for an
 * essential variable or output of the device. Set point in contrast to Parameters are not saved by cloud.iO to a
 * persistent storage and have to be initialized during device boot.
 *
 * Todo: Add example code.
 *
 * @see CloudioAttribute
 * @see CloudioAttributeConstraint
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface SetPoint {}
