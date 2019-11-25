package ch.hevs.cloudio.endpoint;

/**
 * Interface to be implemented by all classes that can hold attributes.
 */
interface CloudioAttributeContainer extends UniqueIdentifiable {
    /**
     * The attribute has changed local in the application.
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
     * Returns the list of attributes contained inside this object.
     *
     * @return List of attributes.
     */
    NamedItemSet<CloudioAttribute.InternalAttribute> getAttributes();

    /**
     * Returns the attribute container's parent (has to be an CloudioObjectContainer).
     *
     * @return  AttributeContainer's parent.
     */
    CloudioObjectContainer getParentObjectContainer();

    /**
     * Sets the parent object container of the attribute container. Note that attribute containers can not be moved, so
     * this method throws a runtime exception if someone tries to move the attribute container to a new parent.
     *
     * @param objectContainer   The new parent object container.
     */
    void setParentObjectContainer(final CloudioObjectContainer objectContainer);
}
