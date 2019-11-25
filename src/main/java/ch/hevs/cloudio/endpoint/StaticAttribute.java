package ch.hevs.cloudio.endpoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used in order to add static attributes to your data model classes. As static values should not
 * be changed once the parent node is online and are only synchronized to the cloud when the actual node goes online,
 * static attributes actually do not need to be instances of the CloudioAttribute class.
 * <br><br>
 * cloud.iO supports these Java types for static attributes:
 * <ul>
 *     <li>Boolean</li>
 *     <li>Integers (Short, Integer, Long)</li>
 *     <li>Numbers (Float, Double)</li>
 *     <li>String</li>
 * </ul>
 *
 * Todo: Add example code.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface StaticAttribute {}
