package ch.hevs.cloudio.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudioFactoryAttribute {
    public String constraint;
    public String type;

}
