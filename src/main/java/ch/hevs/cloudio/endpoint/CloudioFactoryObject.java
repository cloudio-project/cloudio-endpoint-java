package ch.hevs.cloudio.endpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * A CloudioFactoryObject is used as "buffer object" during the deserialization of an Endpoint structure between
 * the serialized Attribute and the {@link CloudioDynamicObject}.
 */
public class CloudioFactoryObject {
    public String type;
    public Map<String, CloudioFactoryObject> objects = new HashMap<String, CloudioFactoryObject>();
    public Map<String, CloudioFactoryAttribute> attributes = new HashMap<String, CloudioFactoryAttribute>();

    public Map<String, Object> properties = new HashMap<String, Object>();
}
