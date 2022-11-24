package ch.hevs.cloudio.endpoint;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudioFactoryNodes {
    //Map<String, Object> nodes = new HashMap<String, String>();
    //public String nodes;

    public Map<String, CloudioFactoryNode> nodes = new HashMap<String, CloudioFactoryNode>();
}
