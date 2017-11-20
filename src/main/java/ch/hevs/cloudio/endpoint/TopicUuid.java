package ch.hevs.cloudio.endpoint;

/**
 * In the case of topic based MQTT communication we use directly the topic in order to identify objects.
 */
class TopicUuid implements Uuid {
    /*** Attributes ***************************************************************************************************/
    // The topic is the UUID for every object.
    private String topic = null;

    /*** Uuid Implementation ******************************************************************************************/
    @Override
    public boolean equals(final Uuid uuid) {
        // The other UUID must be of type MqttUuid and containing the same UUID in order to be equal.
        return uuid instanceof TopicUuid && topic.equals(((TopicUuid)uuid).getTopic());
    }

    @Override
    public String toString() {
        // Return the topic (which is unique).
        return topic;
    }

    @Override
    public boolean isValid() {
        return topic != null;
    }

    /*** API **********************************************************************************************************/
    /**
     * Create the UUID for a node container.
     *
     * @param nodeContainer  Node container to create the UUID for.
     */
    public TopicUuid(final CloudioNodeContainer nodeContainer) {
        try {
            topic = getNodeContainerTopic(nodeContainer);
        } catch (NullPointerException exception) {
            topic = null;
        }
    }

    /**
     * Create the UUID for a object container.
     *
     * @param objectContainer  Object container to create the UUID for.
     */
    public TopicUuid(CloudioObjectContainer objectContainer) {
        try {
            topic = getObjectContainerTopic(objectContainer);
        } catch (NullPointerException exception) {
            topic = null;
        }
    }

    /**
     * Create the UUID for an CloudioAttribute.
     *
     * @param attribute Attribute to create the UUID for.
     */
    public TopicUuid(final CloudioAttribute.InternalAttribute attribute) {
        try {
            topic = getAttributeTopic(attribute);
        } catch (NullPointerException exception) {
            topic = null;
        }
    }

    /**
     * Returns the topic of the object identified by this UUID.
     *
     * @return  Topic of the element represented by this UUID.
     */
    public String getTopic() {
        return topic;
    }

    /*** Private methods **********************************************************************************************/
    private static String getNodeContainerTopic(final CloudioNodeContainer nodeContainer) {
        // As the name of an node container is unique in cloud.io, we just take the name.
        return nodeContainer.getName();
    }

    private static String getObjectContainerTopic(final CloudioObjectContainer objectContainer) {
        CloudioObjectContainer parentObjectContainer = objectContainer.getParentObjectContainer();
        if (parentObjectContainer != null) {
            return getObjectContainerTopic(parentObjectContainer) + "/objects/" + objectContainer.getName();
        }

        CloudioNodeContainer parentNodeContainer = objectContainer.getParentNodeContainer();
        if (parentNodeContainer != null) {
            return getNodeContainerTopic(parentNodeContainer) + "/nodes/" + objectContainer.getName();
        }

        return null;
    }

    private static String getAttributeContainerTopic(final CloudioAttributeContainer attributeContainer) {
        return getObjectContainerTopic(attributeContainer.getParentObjectContainer()) + "/objects/" +
                attributeContainer.getName();
    }

    private static String getAttributeTopic(final CloudioAttribute.InternalAttribute attribute) {
        return getAttributeContainerTopic(attribute.getParent()) + "/attributes/" + attribute.getName();
    }
}
