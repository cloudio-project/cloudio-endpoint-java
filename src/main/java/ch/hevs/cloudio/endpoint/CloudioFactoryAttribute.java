package ch.hevs.cloudio.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudioFactoryAttribute {
    public String constraint;
    public String type;

    public Map<String, Object> properties = new HashMap<String, Object>();

}
