package ch.hevs.cloudio.endpoint;

import java.util.HashMap;
import java.util.Map;

public class CloudioFactoryNode {
    public String type;
    public Map<String, CloudioFactoryObject> objects = new HashMap<String, CloudioFactoryObject>();

    public Map<String, Object> properties = new HashMap<String, Object>();
}
