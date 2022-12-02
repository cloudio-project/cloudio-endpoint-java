package ch.hevs.cloudio.endpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * A CloudioFactoryNode is used as "buffer object" during the deserialization of an Endpoint structure between
 * the serialized Node and a {@link CloudioDynamicNode} or any derived class.
 */
public class CloudioFactoryNode {
    public String type;
    public Map<String, CloudioFactoryObject> objects = new HashMap<String, CloudioFactoryObject>();

    public Map<String, Object> properties = new HashMap<String, Object>();
}
