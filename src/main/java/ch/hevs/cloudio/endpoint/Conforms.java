package ch.hevs.cloudio.endpoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used by cloud.io to declare a conformance to a given class. A class in cloud.io is just a scheme
 * what attributes and child objects an object has to have. An object is conform to such a scheme if it matches exactly
 * the structure of the class. It can not contain more attributes or child objects, then it would be not anymore
 * conform to that class.
 *
 * <h3>Example:</h3>
 * <pre>
 *{@literal @}Conforms(value = "BinarySwitch")
 * class MySwitch extends ch.hevs.cloudio.endpoint.CloudioObject {
 *    {@literal @}SetPoint
 *     public CloudioAttribute&lt;boolean&gt; state;
 *
 *    {@literal @}Status
 *     public CloudioAttribute&lt;boolean&gt; stateFeedback;
 * }
 * </pre>
 * The class MySwitch in the example above conforms to the cloud.io data class "BinarySwitch".
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Conforms {
    /**
     * The class to which the object is conform.
     *
     * @return The common data class the object conforms to.
     */
    String value();
}
