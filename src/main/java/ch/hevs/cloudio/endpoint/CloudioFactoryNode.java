package ch.hevs.cloudio.endpoint;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudioFactoryNode {
    public String type;
    public Map<String, CloudioFactoryObject> objects = new HashMap<String, CloudioFactoryObject>();
    public Map<String, String> customParameters= new HashMap<String, String>();
}
