package ch.hevs.cloudio.endpoint;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;

/**
 * Decode node list using the Jackson serialization API. The actual format is determined by the passed factory instance.
 * If a JsonFactory object is passed, JSON is used to serialize the data, if a CBORFactory object is passed the format
 * used will be CBOR.
 */
public class GenericJacksonFactoryFormat implements CloudioFactoryFormat {

    static class JSON extends GenericJacksonFactoryFormat {
        JSON() {
            super(new JsonFactory());
        }
    }

    static class CBOR extends GenericJacksonFactoryFormat {
        CBOR() {
            super(new CBORFactory());
        }
    }

    private final Logger log = LogManager.getLogger(GenericJacksonMessageFormat.class);
    private final JsonFactory factory;

    GenericJacksonFactoryFormat(JsonFactory factory) {
        this.factory = factory;
    }

    @Override
    public CloudioFactoryNodes deserializeNodes(InputStream jsonNodesInputStream) throws Exception {
        ObjectMapper mapper = new ObjectMapper(this.factory);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CloudioFactoryNodes cloudioFactoryNodes = mapper.readValue(jsonNodesInputStream, CloudioFactoryNodes.class);
        return cloudioFactoryNodes;
    }
}
