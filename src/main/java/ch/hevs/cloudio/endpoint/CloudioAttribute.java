package ch.hevs.cloudio.endpoint;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * The leafs of a cloud.io data model consists of attributes. Attributes carry the actual information (not the
 * structure) of a data model.
 * <br><br>
 * cloud.io supports multiple data types for attributes:
 * <ul>
 *     <li>Boolean</li>
 *     <li>Integers (Short, Integer, Long)</li>
 *     <li>Numbers (Float, Double)</li>
 *     <li>String</li>
 * </ul>
 *
 * <h3>Example:</h3>
 * <pre>
 * class Switch extends ch.hevs.cloudio.endpoint.CloudioObject {
 *    {@literal @}SetPoint
 *     public CloudioAttribute&lt;boolean&gt; state;
 *
 *    {@literal @}Status
 *     public CloudioAttribute&lt;boolean&gt; stateFeedback;
 * }
 * </pre>
 *
 * Attributes are annotated with one of these constraints:
 * <ul>
 *     <li>{@link Parameter}</li>
 *     <li>{@link Status}</li>
 *     <li>{@link SetPoint}</li>
 *     <li>{@link Measure}</li>
 * </ul>
 * <br>
 *
 * If the attribute does not have a constraint annotation, the object containing the attribute will throw a runtime
 * exception, so ensure that every attribute has a constraint annotation.
 *
 * Attributes with a {@link StaticAttribute} annotation does not have to be of type Attribute, use rather native types
 * for static attributes.
 *
 * @param <T>   Data type of the attribute, must be one of the supported data types!
 */
public class CloudioAttribute<T> {
    final InternalAttribute internal = new InternalAttribute();

    /*** Public API ***************************************************************************************************/
    /**
     * Returns the current value of the attribute.
     *
     * @return  Attributes current value.
     */
    public T getValue() {
        return internal.value;
    }

    /**
     * Tries to change the attribute to the given value. This might fail if the attribute constraint is not suitable to
     * change the value from the endpoint.
     *
     * @param value                         The new value for the attribute.
     * @throws CloudioAttributeConstraintException If the endpoint can not change the attribute.
     */
    public void setValue(final T value) throws CloudioAttributeConstraintException {
        setValue(value, Calendar.getInstance().getTimeInMillis());
    }

    /**
     * Tries to change the attribute to the given value and attributes the given timestamp to the new value. This might
     * fail if the attribute constraint is not suitable to change the value from the endpoint.
     *
     * @param value                         The new value for the attribute.
     * @param timestamp                     The timestamp in milliseconds since epoch to link with the new value.
     * @throws CloudioAttributeConstraintException If the endpoint can not change the attribute.
     */
    public void setValue(final T value, Long timestamp) throws CloudioAttributeConstraintException {
        // Check constraint.
        internal.constraint.endpointWillChange();

        // Update value.
        internal.timestamp = timestamp;
        internal.value = value;

        // Send change to cloud.
        if (internal.parent != null) {
            internal.parent.attributeHasChangedByEndpoint(this.internal);
        }

        // Inform all registered listeners.
        if (internal.listeners != null) {
            for (CloudioAttributeListener listener : internal.listeners) {
                //noinspection unchecked
                listener.attributeHasChanged(this);
            }
        }
    }

    /**
     * Tries to initialize the attribute with the given value. You can not initialize an attribute with constraints
     * Static and Parameter and you need to initialize the value only once and before the node has gone online. In all
     * other cases, an exception will be thrown.
     *
     * @param initialValue                      The value to initialize the attribute with.
     * @throws CloudioAttributeConstraintException     If the endpoint can not change the attribute.
     * @throws CloudioAttributeInitializationException If the attribute is already initialized or the node is already online.
     */
    public void setInitialValue(T initialValue) throws CloudioAttributeConstraintException, CloudioAttributeInitializationException {
        // Check constraint.
        internal.constraint.endpointWillInitialize();

        // Check that the value was not set before.
        if (internal.value != null) {
            throw new CloudioAttributeInitializationException("Attribute has already been initialized, an attribute can " +
                    "only be initialized once!");
        }

        // If the node is already registered within an endpoint, fail.
        if (internal.parent != null && internal.parent.isNodeRegisteredWithinEndpoint()) {
            throw new CloudioAttributeInitializationException("Only attributes of nodes that are not already part of an " +
                    "endpoint can be initialized!");
        }

        // Initialize value.
        internal.value = initialValue;
    }

    /**
     * Tries to initialize the attribute with the given value for ad lib objects.
     *
     * @param initialValue                              The value to initialize the attribute with.
     * @throws CloudioAttributeConstraintException      If the endpoint can not change the attribute.
     * @throws CloudioAttributeInitializationException  If the attribute is already initialized or the node is already online.
     */
    void setInitialAdlibValue(T initialValue) throws CloudioAttributeConstraintException, CloudioAttributeInitializationException {
        // Check constraint.
        internal.constraint.endpointWillInitializeAdLib();

        // Check that the value was not set before.
        if (internal.value != null) {
            throw new CloudioAttributeInitializationException("Attribute has already been initialized, an attribute can " +
                "only be initialized once!");
        }

        // If the node is already registered within an endpoint, fail.
        if (internal.parent != null && internal.parent.isNodeRegisteredWithinEndpoint()) {
            throw new CloudioAttributeInitializationException("Only attributes of nodes that are not already part of an " +
                "endpoint can be initialized!");
        }

        // Initialize value.
        internal.value = initialValue;
    }

    /**
     * Tries to parse according to the type of the attribute the given string and sets the attribute's value to the
     * result of the parsing process.
     *
     * @param value                         String representation of the value to set.
     * @throws NumberFormatException        In the case the attribute is a numeric data type and the parse failed.
     * @throws CloudioAttributeConstraintException If the endpoint can not change the attribute.
     */
    public void setStringValue(final String value) throws NumberFormatException, CloudioAttributeConstraintException {
        setStringValue(value, Calendar.getInstance().getTimeInMillis());
    }

    /**
     * Tries to parse according to the type of the attribute the given string and sets the attribute's value to the
     * result of the parsing process. Additionally this method links the parse result with the given timestamp.
     *
     * @param value                         String representation of the value to set.
     * @param timestamp                     The timestamp in milliseconds since epoch to link with the new value.
     * @throws NumberFormatException        In the case the attribute is a numeric data type and the parse failed.
     * @throws CloudioAttributeConstraintException If the endpoint can not change the attribute.
     */
    public void setStringValue(final String value, final long timestamp) throws NumberFormatException,
        CloudioAttributeConstraintException {
        if (internal.rawType == Boolean.class) {
            Boolean b = Boolean.parseBoolean(value);
            //noinspection unchecked
            setValue((T)b, timestamp);
        } else if (internal.rawType == Short.class) {
            Short s = Short.parseShort(value);
            //noinspection unchecked
            setValue((T)s, timestamp);
        } else if (internal.rawType == Integer.class) {
            Integer i = Integer.parseInt(value);
            //noinspection unchecked
            setValue((T)i, timestamp);
        } else if (internal.rawType == Long.class) {
            Long l = Long.parseLong(value);
            //noinspection unchecked
            setValue((T)l, timestamp);
        } else if (internal.rawType == Float.class) {
            Float f = Float.parseFloat(value);
            //noinspection unchecked
            setValue((T)f, timestamp);
        } else if (internal.rawType == Double.class) {
            Double d = Double.parseDouble(value);
            //noinspection unchecked
            setValue((T)d, timestamp);
        } else if (internal.rawType == String.class) {
            //noinspection unchecked
            setValue((T)value, timestamp);
        }
    }

    /**
     * Returns the actual type of the attribute.
     *
     * @return  Attribute's type.
     */
    public Type getType() {
        return internal.rawType;
    }

    /**
     * Adds the given listener to the list of listeners that will get informed about a change of the attribute.
     *
     * @param listener  Reference to the object implementing the CloudioAttributeListener interface to add.
     */
    public void addListener(final CloudioAttributeListener listener) {
        if (listener != null) {
            // Lazy initialization of the listener list.
            if (internal.listeners == null) {
                internal.listeners = new LinkedList<CloudioAttributeListener>();
            }

            // Finally add the listener.
            internal.listeners.add(listener);
        }
    }

    /**
     * Removes the given listener from the list of listeners.
     *
     * @param listener  Reference to the object implementing the CloudioAttributeListener interface to remove.
     */
    public void removeListener(final CloudioAttributeListener listener) {
        if (listener != null && internal.listeners != null) {
            internal.listeners.remove(listener);
        }
    }

    /*** Internal API *************************************************************************************************/
    CloudioAttribute() {}

    class InternalAttribute implements UniqueIdentifiable {
        /*** Attributes ***********************************************************************************************/
        private String name = null;
        private CloudioAttributeContainer parent = null;
        private TopicUuid uuid = null;
        private CloudioAttributeConstraint constraint = null;
        private Type rawType = null;
        private Long timestamp = null;
        private T value = null;
        private List<CloudioAttributeListener> listeners = null;

        /*** UniqueIdentifiable Implementation ************************************************************************/
        @Override
        public Uuid getUuid() {
            // Lazy initialisation of the UUID.
            if (uuid == null) {
                uuid = new TopicUuid(this);
            }
            return uuid;
        }

        /*** NamedItem Implementation *********************************************************************************/
        @Override
        public String getName() {
            return name;
        }

        /*** Package private methods **********************************************************************************/
        /**
         * Returns the current value of the attribute.
         *
         * @return  Attributes current value.
         */
        T getValue() {
            return value;
        }

        /**
         * Updates the value from the cloud. Note that this method should not be used by endpoints, as it guarantees
         * that only attributes with semantics compatible with cloud updates can be updated.
         *
         * @param value                             New value to set from cloud.
         * @param timestamp                         Timestamp of the value from the cloud.
         * @return                                  True if the value was updated, false if not.
         * @throws CloudioAttributeConstraintException     If the cloud can not change the attribute.
         */
        boolean setValueFromCloud(final T value, long timestamp) throws CloudioAttributeConstraintException {
            // Check if the cloud can change the attribute.
            constraint.cloudWillChange();

            // Check if the value from the cloud is older than the actual one and do nothing if that is the case.
            if (this.timestamp != null && this.timestamp >= timestamp)
                return false;

            // TODO: Maybe we should check that the timestamp is not older than a given number of seconds.

            // Update the value.
            this.timestamp = timestamp;
            this.value = value;

            // Notify the cloud.
            if (parent != null) {
                parent.attributeHasChangedByCloud(this);
            }

            // Notify all listeners.
            if (listeners != null) {
                for (CloudioAttributeListener listener : listeners) {
                    //noinspection unchecked
                    listener.attributeHasChanged(CloudioAttribute.this);
                }
            }

            return true;
        }

        /**
         * Initializes the static value, this can be only done using static attributes (@StaticAttribute or @Static).
         * The value of a static attribute can be changed as often as wanted, the only constraint is that the node
         * containing the static attribute has not been registered within the endpoint.
         *
         * @param value                         The initial value to set.
         * @throws CloudioAttributeConstraintException If the attribute is not a static attribute.
         */
        void setStaticValue(final T value) throws CloudioAttributeConstraintException {
            // Check constraint.
            constraint.endpointWillChangeStatic();

            // Set value.
            this.value = value;
        }

        /**
         * Returns the attribute's parent (is always an Object).
         *
         * @return  Attribute's parent.
         */
        CloudioAttributeContainer getParent() {
            return parent;
        }

        /**
         * Sets the parent of the attribute. Note that attributes can not be moved, so this method throws a runtime
         * exception if someone tries to move the attribute to a new parent.
         *
         * @param parent    The new parent.
         */
        void setParent(final CloudioAttributeContainer parent) {
            // If the attribute already has a parent (we are moving the attribute) then fail with a runtime exception.
            if (this.parent != null) {
                throw new CloudioModificationException("The parent of an Attribute can never be changed " +
                        "(Attributes can not be moved)!");
            }

            // Set the parent.
            this.parent = parent;
        }

        /**
         * Sets the local name of the attribute. Note that attributes can not be renamed, so this method throws a
         * runtime exception if someone tries to rename the attribute.
         *
         * @param name  The name to identify the attribute locally.
         */
        public void setName(String name) {
            // If the attribute already has a name (we are renaming the attribute) then fail with a runtime exception.
            if (this.name != null) {
                throw new CloudioModificationException(
                    "The Attribute has already a name (Renaming attributes is forbidden)!");
            }

            // Set the local name.
            this.name = name;
        }

        /**
         * Returns the constraint of the attribute.
         *
         * @return  Attribute constraint.
         */
        CloudioAttributeConstraint getConstraint() {
            return constraint;
        }

        /**
         * Sets the constraint of the attribute. Note that constraints can not change over time,so this method throws a
         * runtime exception if someone tries to change the constraint of the attribute.
         *
         * @param constraint    Attribute constraint.
         */
        void setConstraint(final CloudioAttributeConstraint constraint) {
            // If the attribute already has a constraint set up then fail with a runtime exception.
            if (this.constraint != null) {
                throw new CloudioModificationException("The Attribute has already a constraint " +
                        "(Changing constraints is not allowed)!");
            }

            // Set the constraint.
            this.constraint = constraint;
        }

        /**
         * Returns the actual type of the attribute.
         *
         * @return  Attribute's type.
         */
        CloudioAttributeType getType() {
            return CloudioAttributeType.fromRawType(rawType);
        }

        /**
         * Sets the type of the attribute, note that the type of an attribute is not allowed to change over time, so if
         * the attribute already has a type, the method fails with an runtime exception.
         *
         * @param type                          Type to set.
         * @throws InvalidCloudioAttributeException    If the type is not supported by cloud.io.
         */
        void setType(Type type) throws InvalidCloudioAttributeException {
            // If the type has already been set up fail with a runtime exception.
            if (this.rawType != null) {
                throw new CloudioModificationException(
                    "The Attribute has already a type (Changing the type is not allowed)!");
            }

            // If the type is supported, setup the type.
            if (CloudioAttributeType.fromRawType(type) == CloudioAttributeType.Invalid) {
                throw new InvalidCloudioAttributeException(type);
            }

            rawType = type;
        }

        Long getTimestamp() {
            return timestamp;
        }

        CloudioAttribute getExternalAttribute() {
            return CloudioAttribute.this;
        }

        void close() {
            parent = null;
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            close();
        }
    }
}
