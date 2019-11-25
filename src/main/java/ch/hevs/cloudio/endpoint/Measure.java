package ch.hevs.cloudio.endpoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark an attribute as a physical measure (constraint). Measures are all values which a device
 * reads from a sensor. For calculated values prefer the constraint {@link Status}.
 *
 * Todo: Add example code.
 *
 * @see CloudioAttribute
 * @see CloudioAttributeConstraint
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Measure {}
