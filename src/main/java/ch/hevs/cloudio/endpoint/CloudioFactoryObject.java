package ch.hevs.cloudio.endpoint;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudioFactoryObject {
    public Map<String, CloudioFactoryObject> objects = new HashMap<String, CloudioFactoryObject>();
    public Map<String, CloudioFactoryAttribute> attributes = new HashMap<String, CloudioFactoryAttribute>();
}
