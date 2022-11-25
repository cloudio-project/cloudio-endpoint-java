package ch.hevs.cloudio.endpoint;

import java.io.InputStream;

public interface CloudioFactoryFormat {

    CloudioFactoryNodes deserializeNodes(InputStream jsonNodesInputStream) throws Exception;
}
