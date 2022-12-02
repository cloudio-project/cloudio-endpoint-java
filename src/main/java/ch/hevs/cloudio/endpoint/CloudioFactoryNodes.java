package ch.hevs.cloudio.endpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * A CloudioFactoryNodes is used as "buffer object" during the deserialization of an Endpoint structure between
 * the serialized map of nodes and the mao of {@link CloudioNode} inside the {@link CloudioEndpoint}.
 */
public class CloudioFactoryNodes {
    public Map<String, CloudioFactoryNode> nodes = new HashMap<String, CloudioFactoryNode>();

    public Map<String, Object> properties = new HashMap<String, Object>();
}
