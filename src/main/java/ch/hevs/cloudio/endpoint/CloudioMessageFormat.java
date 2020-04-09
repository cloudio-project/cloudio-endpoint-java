package ch.hevs.cloudio.endpoint;

/**
 * The CloudioMessageFormat interface declares the methods that are used by the {@link CloudioEndpoint} implementation in order to
 * encode and decode attribute changes into MQTT messages.
 */
interface CloudioMessageFormat {
    /**
     * A CloudioMessageFormat implementation should return the encoded payload of the serialization of the given endpoint.
     *
     * @param endpoint  Endpoint to serialize.
     * @return          Raw data representation of the endpoint.
     */
    byte[] serializeEndpoint(CloudioEndpoint.InternalEndpoint endpoint);

    /**
     * A CloudioMessageFormat implementation should return the encoded payload of the serialization of the given node.
     *
     * @param node  Node to serialize.
     * @return      Raw data representation of the node.
     */
    byte[] serializeNode(CloudioNode.InternalNode node);

    /**
     * A CloudioMessageFormat implementation should return the encoded payload of the serialization of the given attribute.
     *
     * @param attribute Attribute to serialize.
     * @return          Raw data representation of the attribute.
     */
    byte[] serializeAttribute(CloudioAttribute.InternalAttribute attribute);

    /**
     * A CloudioMessageFormat implementation should return the encoded payload of the serialization of the given
     * set attribute.
     *
     * @param attribute Attribute to serialize.
     * @param correlationID correlation ID of the @set message
     * @return          Raw data representation of the set attribute linked to its correlation ID.
     */
    byte[] serializeDidSetAttribute(CloudioAttribute.InternalAttribute attribute, String correlationID);

    /**
     * A CloudioMessageFormat implementation should return the encoded payload of the serialization of messages from the
     * messageCategories inside the given cloudioPersistence
     *
     * @param cloudioPersistence Persistence Object where to retrieve messages
     * @param messageCategories Message categories from persistence to serialize
     * @return
     */
    byte[] serializeDelayed(CloudioPersistence cloudioPersistence, String messageCategories[]);

    /**
     * A CloudioMessageFormat implementation should return the encoded payload of the serialization of the given transaction.
     *
     * @param transaction Transaction to serialize.
     * @return            Raw data representation of the transaction.
     */
    byte[] serializeTransaction(Transaction transaction);

    /**
     * A CloudioMessageFormat implementation should parse the data payload and update the given attribute according to the
     * data.
     *
     * @param data                              Data received in the MQTT message.
     * @param attribute                         Attribute to update using the raw message data.
     * @throws Exception                        If the attribute can not be updated from the cloud for any reason.
     */
    void deserializeAttribute(byte[] data, CloudioAttribute.InternalAttribute attribute)
        throws Exception;

    /**
     * A CloudioMessageFormat implementation should parse the data payload and set the given attribute according to the
     * data and return the correlation ID of the set message.
     * @param data                              Data received in the MQTT message.
     * @param attribute                         Attribute to update using the raw message data.
     * @return correlation ID contained in the MQTT message
     * @throws Exception                        If the attribute can not be updated from the cloud for any reason.
     */
    String deserializeSetAttribute(byte[] data, CloudioAttribute.InternalAttribute attribute)
            throws Exception;

    /**
     * A CloudioMessageFormat implementation should parse the data payload and update the given attribute according to the
     * data.
     *
     * @param data                              Data received in the MQTT message.
     * @param jobsParameter                     JobsParameter to update using the raw message data.
     * @throws Exception                        If the jobsParameter can not be updated from the cloud for any reason.
     */
    void deserializeJobsParameter(byte[] data, JobsParameter jobsParameter)
            throws Exception;

    /**
     * A CloudioMessageFormat implementation should parse the data payload and update the given attribute according to the
     * data.
     *
     * @param data                              Data received in the MQTT message.
     * @param logParameter                      LogParameter to update using the raw message data.
     * @throws Exception                        If the logParameter can not be updated from the cloud for any reason.
     */
    void deserializeLogParameter(byte[] data, LogParameter logParameter)
            throws Exception;

    /**
     * A CloudioMessageFormat implementation should return the encoded payload of the serialization of the given log message.
     *
     * @param cloudioLogMessage CloudioLogMessage to serialize.
     * @return                  Raw data representation of the CloudioLogMessage.
     */
    byte[] serializeCloudioLog(CloudioLogMessage cloudioLogMessage);

    /**
     * A CloudioMessageFormat implementation should return the encoded payload of the serialization of the given job line output.
     *
     * @param jobsLineOutput JobsLineOutput to serialize.
     * @return               Raw data representation of the JobsLineOutput.
     */
    byte[] serializeJobsLineOutput(JobsLineOutput jobsLineOutput);
}
