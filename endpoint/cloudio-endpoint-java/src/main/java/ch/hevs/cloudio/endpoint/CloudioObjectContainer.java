package ch.hevs.cloudio.endpoint;

import java.util.Stack;

/**
 * Interface to be implemented by all classes that can hold cloud.iO objects.
 */
interface CloudioObjectContainer extends UniqueIdentifiable {
    /**
     * The attribute has changed.
     *
     * @param attribute Attribute which has changed.
     */
    void attributeHasChangedByEndpoint(final CloudioAttribute.InternalAttribute attribute);

    /**
     * The attribute has changed from the cloud.
     *
     * @param attribute Attribute which has changed.
     */
    void attributeHasChangedByCloud(final CloudioAttribute.InternalAttribute attribute);

    /**
     * Returns true if the node the attribute is part of is registered within an endpoint, false otherwise.
     *
     * @return  True if the node is registered within the endpoint, false if not.
     */
    boolean isNodeRegisteredWithinEndpoint();

    /**
     * Returns the list of child object contained inside this container.
     *
     * @return Child objects.
     */
    NamedItemSet<CloudioObject.InternalObject> getObjects();

    /**
     * Returns the object container's parent object container. Note that if the actual object container is not embedded
     * into another object controller, the method returns null.
     *
     * @return  ObjectContainer's parent.
     */
    CloudioObjectContainer getParentObjectContainer();

    /**
     * Sets the parent object container of the object container. Note that object containers can not be moved, so
     * this method throws a runtime exception if someone tries to move the object container to a new parent or in the
     * case the actual container is a node, which can not be part of another object container.
     *
     * @param objectContainer   The new parent object container.
     */
    void setParentObjectContainer(final CloudioObjectContainer objectContainer);

    /**
     * Returns the object container's parent node container. Note that if the actual object container is not a node,
     * the method returns null.
     *
     * @return  ObjectContainer's (node's) parent.
     */
    CloudioNodeContainer getParentNodeContainer();

    /**
     * Sets the parent node container of the object container (node). Note that object containers can not be moved, so
     * this method throws a runtime exception if someone tries to move the object container to a new parent or in the
     * case the actual container is not a node..
     *
     * @param nodeContainer The new parent node container.
     */
    void setParentNodeContainer(final CloudioNodeContainer nodeContainer);

    /**
     * Finds the given attribute inside the child objects using the given location path (stack). If an attribute was
     * found at the given location, a reference to that attribute is returned, otherwise null is returned.
     *
     * @param location  Location of the attribute inside the object container.
     * @return          Reference to the attribute or null if the attribute could not be found.
     */
    CloudioAttribute.InternalAttribute findAttribute(Stack<String> location);
}
