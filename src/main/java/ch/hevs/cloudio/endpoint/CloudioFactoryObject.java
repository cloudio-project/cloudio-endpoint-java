package ch.hevs.cloudio.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudioFactoryObject {
    public Map<String, CloudioFactoryObject> objects = new HashMap<String, CloudioFactoryObject>();
    public Map<String, CloudioFactoryAttribute> attributes = new HashMap<String, CloudioFactoryAttribute>();
}
