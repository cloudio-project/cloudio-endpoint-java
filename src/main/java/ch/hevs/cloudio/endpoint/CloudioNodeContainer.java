package ch.hevs.cloudio.endpoint;

/**
 * Interface all internal cloud.io Java CloudioEndpoint objects have to implement in order that they can contain a
 * {@link CloudioNode}.
 */
interface CloudioNodeContainer extends UniqueIdentifiable {
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
}
