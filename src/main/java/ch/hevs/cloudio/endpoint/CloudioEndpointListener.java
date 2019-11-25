package ch.hevs.cloudio.endpoint;

/**
 * Interface used to inform another object about the actual state of the endpoint.
 */
public interface CloudioEndpointListener {
    /**
     * This method gets called whenever the endpoint successfully connects to the MQTT broker.
     *
     * @param endpoint  The endpoint which has changed his connection state.
     */
    void endpointIsOnline(CloudioEndpoint endpoint);

    /**
     * This method gets called whenever the endpoint lost connection to the MQTT broker.
     *
     * @param endpoint  The endpoint which has changed his connection state.
     */
    void endpointIsOffline(CloudioEndpoint endpoint);
}
