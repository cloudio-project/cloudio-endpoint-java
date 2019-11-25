package ch.hevs.cloudio.endpoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark an attribute as a status (constraint). Status attributes should be values that the device
 * actually generates. In contrast to measure attributes, status attributes should not be external physical signals
 * measured by the device. An example for a status attribute is the feedback of a relay output or the actual mode a
 * device is operating.
 *
 * Todo: Add example code.
 *
 * @see CloudioAttribute
 * @see CloudioAttributeConstraint
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Status {}
