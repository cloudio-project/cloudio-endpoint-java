package ch.hevs.cloudio.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudioFactoryNodes {
    public Map<String, CloudioFactoryNode> nodes = new HashMap<String, CloudioFactoryNode>();
}
