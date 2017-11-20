package ch.hevs.cloudio.endpoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark an attribute as a parameter (constraint). parameters are setting for the device that will
 * last even power cycles. Set point in contrast to Parameters are not saved by cloud.iO to a persistent storage and
 * will be initialized to default values during device boot.
 *
 * Todo: Add example code.
 *
 * @see CloudioAttribute
 * @see CloudioAttributeConstraint
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Parameter {}
