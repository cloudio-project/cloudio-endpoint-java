package ch.hevs.cloudio.endpoint;

/**
 * Defines the different possible attribute constraints.
 */
public enum CloudioAttributeConstraint {
    /**
     * The attribute is a static value and can't be changed during runtime.
     */
    Static,

    /**
     * The attribute is a parameter that can be configured from the cloud and its value should be saved locally on
     * the "Endpoint". Note that the cloud.iO communication library will not save the value, it is the responsibility
     * of you to actually save the configuration to a persistent location.
     */
    Parameter,

    /**
     * The attribute is a status.
     */
    Status,

    /**
     * The attribute is a set point that can be changed from the cloud. Note that there is no guarantee that the value
     * of set points are stored within the "Endpoint" and might be initialized to the default value on the next power
     * cycle.
     */
    SetPoint,

    /**
     * The attribute is a measure of any kind and can change at any time.
     */
    Measure;

    /**
     * Call this method every time an endpoint tries to change an attribute. If the attribute constraint allows the
     * endpoint to change the attribute, nothing happens, otherwise an exception is thrown.
     *
     * @throws CloudioAttributeConstraintException In the case the constraint does not allow the endpoint to change the
     *                                      attribute.
     */
    void endpointWillChange() throws CloudioAttributeConstraintException {
        if (this != CloudioAttributeConstraint.Status && this != CloudioAttributeConstraint.Measure) {
            throw new CloudioAttributeConstraintException("Can not change an attribute with constraint " + this);
        }
    }

    /**
     * Call this method every time an endpoint tries to initialize an attribute. If the attribute constraint allows the
     * endpoint to initialize or change the attribute, nothing happens, otherwise an exception is thrown.
     *
     * @throws CloudioAttributeConstraintException In the case the constraint does not allow the endpoint to initialize the
     *                                      attribute.
     */
    void endpointWillInitialize() throws CloudioAttributeConstraintException {
        if (this == CloudioAttributeConstraint.Static || this == CloudioAttributeConstraint.Parameter) {
            throw new CloudioAttributeConstraintException("Can not initialize an attribute with constraint " + this);
        }
    }

    /**
     * Call this method every time an endpoint tries to initialize an attribute for an ad lib object. If the attribute
     * constraint allows the endpoint to initialize or change the attribute, nothing happens, otherwise an exception is thrown.
     *
     * @throws CloudioAttributeConstraintException In the case the constraint does not allow the endpoint to initialize the
     *                                             attribute.
     */
    void endpointWillInitializeAdLib() throws CloudioAttributeConstraintException {
        if (this == CloudioAttributeConstraint.Parameter) {
            throw new CloudioAttributeConstraintException("Can not initialize an attribute with constraint " + this);
        }
    }

    /**
     * Call this method every time an endpoint tries to set the value of a static attribute. If the attribute
     * constraint allows the endpoint to initialize or change the attribute, nothing happens, otherwise an exception
     * is thrown.
     *
     * @throws CloudioAttributeConstraintException In the case the constraint does not allow the endpoint to set the static
     *                                      attribute.
     */
    void endpointWillChangeStatic() throws CloudioAttributeConstraintException {
        if (this != CloudioAttributeConstraint.Static) {
            throw new CloudioAttributeConstraintException("Can not set statically an attribute with constraint " + this);
        }
    }

    /**
     * Call this method every time the cloud tries to change an attribute. If the attribute constraint allows the
     * cloud to change the attribute, nothing happens, otherwise an exception is thrown.
     *
     * @throws CloudioAttributeConstraintException In the case the constraint does not allow the cloud to change the
     *                                      attribute.
     */
    void cloudWillChange() throws CloudioAttributeConstraintException {
        if (this != CloudioAttributeConstraint.Parameter && this != CloudioAttributeConstraint.SetPoint) {
            throw new CloudioAttributeConstraintException("Can not change an attribute with constraint " + this +
                    " from cloud");
        }
    }
}
