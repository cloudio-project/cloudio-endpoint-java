package ch.hevs.cloudio.endpoint;

/**
 * This interface enables an application object to get notified as soon as there was a new value set to an attribute.
 * If the change was set from the local application or from the cloud does not matter. This means that even on
 * attributes with a @Measure constraint, you can add listeners in order to get notified about the applications own
 * changes to the data model. This can be handy if in addition to the cloud, there is a local UI and application logic.
 *
 * <h3>Example:</h3>
 * <pre>
 * class Switch extends ch.hevs.cloudio.endpoint.CloudioObject {
 *    {@literal @}SetPoint
 *     public CloudioAttribute&lt;boolean&gt; state;
 *
 *    {@literal @}Measure
 *     public CloudioAttribute&lt;boolean&gt; stateFeedback;
 * }
 *
 * class MySwitchSim extends Switch {
 *     public MySwitchSim() {
 *         state.addListener(new CloudioAttributeListener() {
 *            {@literal @}Override
 *             void attributeHasChanged(final CloudioAttribute attribute) {
 *                 stateFeedback.setValue(attribute.getValue());
 *             }
 *         });
 *     }
 * }
 * </pre>
 */
public interface CloudioAttributeListener<T> {
    /**
     * This method is called upon an attribute has been changed.
     *
     * @param attribute Attribute that has changed.
     */
    void attributeHasChanged(final CloudioAttribute<T> attribute);
}
