package ch.hevs.cloudio.endpoint;

import java.io.InputStream;

/**
 * The CloudioFactoryFormat interface declares the methods that are used by the {@link CloudioEndpoint} implementation
 * in order to build an Endpoint using the Factory feature.
 */
public interface CloudioFactoryFormat {

    /**
     * A CloudioFactoryNodes implementation should parse a serialized node list and convert into a
     * {@link CloudioFactoryNodes}.
     * @param jsonNodesInputStream              InputStream containing the serialized node list
     * @return The structure of nodes to be created by the {@link CloudioEndpoint}
     * @throws Exception                        If deserialization cannot be done for any reason.
     */
    CloudioFactoryNodes deserializeNodes(InputStream jsonNodesInputStream) throws Exception;
}
