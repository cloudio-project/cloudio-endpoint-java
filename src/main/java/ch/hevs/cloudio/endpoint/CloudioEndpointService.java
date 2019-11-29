package ch.hevs.cloudio.endpoint;

/**
 * A CloudioEndpoint is the root object of any connection of a device or a gateway to cloud.io. The CloudioEndpointService is exposed
 * by the cloud.io OSGi container in order to enable other OSGi bundles to register and remove new nodes within the
 * endpoint.
 */
public interface CloudioEndpointService {
    /**
     * Returns true if the endpoint is connected to the central message broker, otherwise false.
     *
     * @return  True if the endpoint is online, false otherwise.
     */
    boolean isOnline();

    /**
     * Adds the given listener to the endpoint service.
     *
     * @param listener  Listener to add.
     */
    void addEndpointListener(CloudioEndpointListener listener);

    /**
     * Removes the given listener from the endpoint service.
     *
     * @param listener  Listener to add.
     */
    void removeEndpointListener(CloudioEndpointListener listener);

    /**
     * Adds the given node to the endpoint. The name of the node needs to be unique, otherwise the method will fail with
     * an {@link DuplicateItemException}.
     *
     * @param nodeName                      Name to give to the node.
     * @param node                          Reference to the node instance to add.
     * @throws DuplicateItemException  If a node with the given name already exists.
     */
    void addNode(String nodeName, CloudioNode node) throws DuplicateItemException;

    /**
     * Adds a new node instance of the given node class to the endpoint. The name of the node needs to be unique,
     * otherwise the method will fail with an {@link DuplicateItemException}. Note that the node class must dispose
     * a default constructor in order to let the method create an instance of the class.
     *
     * @param nodeName                      Name to give to the node.
     * @param nodeClass                     The class of the node to instantiate.
     * @param <T>                           Type of the node class.
     * @return                              Reference to the new instance of the given node class.
     * @throws DuplicateItemException  If a node with the given name already exists.
     */
    <T extends CloudioNode> T addNode(String nodeName, Class<T> nodeClass)
        throws InvalidCloudioNodeException, DuplicateItemException;

    /**
     * Removes the given node from the endpoint if the node actually is part of the endpoint. If the node was not
     * contained in the endpoint, the method will do nothing at all.
     *
     * @param node  Node to remove.
     */
    void removeNode(CloudioNode node);

    /**
     * Removes the node with the given name from the endpoint if the node actually is part of the endpoint. If the
     * endpoint does not contain a node with the given name, the method will do nothing at all.
     *
     * @param nodeName  Name of the node to remove.
     */
    void removeNode(String nodeName);

    /**
     * Returns the node with the given name that was registered within cloud.iO or null if no such node is present.
     *
     * @param nodeName              Name of the node to return a reference to.
     * @param <T>                   Subclass of node.
     * @return                      A reference to registered node or null if no node with the given name exists.
     * @throws ClassCastException   If the node could not be casted to the target type
     */
    <T extends CloudioNode> T getNode(String nodeName) throws ClassCastException;
}
