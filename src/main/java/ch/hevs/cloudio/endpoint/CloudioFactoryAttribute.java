package ch.hevs.cloudio.endpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * A CloudioFactoryAttribute is used as "buffer object" during the deserialization of an Endpoint structure between
 * the serialized Attribute and the {@link CloudioAttribute}.
 */
public class CloudioFactoryAttribute {
    public String constraint;
    public String type;

    public Map<String, Object> properties = new HashMap<String, Object>();

}
