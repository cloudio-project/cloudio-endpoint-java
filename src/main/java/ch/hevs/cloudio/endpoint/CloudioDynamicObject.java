package ch.hevs.cloudio.endpoint;

/**
 * The CloudioAdLibObject class allows you to create the structure of a cloud.iO node at runtime in contrast to the static
 * model  used by the CloudioObject class.
 *
 * TODO: Example.
 */
public class CloudioDynamicObject extends CloudioObject {
    /*** Public API ***************************************************************************************************/
    /**
     *
     */
    public CloudioDynamicObject() {
        internal = new InternalDynamicObject();
    }

    /**
     * Returns the object with the given name or null if no object with the given name is part of the object.
     *
     * @param name  Name of the object to return.
     * @return      The object with the given name or null if the object does not exists.
     */
    public CloudioObject getObject(final String name) {
        return ((InternalDynamicObject)internal).dynamicObjects.getItem(name).getExternalObject();
    }

    /**
     * Adds an object of the given class with the given name to the custom object.
     *
     * @param name                          Name to give to the object after creation.
     * @param clazz                         The class of the object to create and add.
     * @param <T>                           Type of the object.
     * @return                              The object just created and added.
     * @throws DuplicateItemException  If there already exists an object with the given name.
     * @throws InvalidCloudioObjectException       The object class is invalid.
     */
    public <T extends CloudioObject> T addObject(final String name, final Class<T> clazz) throws DuplicateItemException,
        InvalidCloudioObjectException {
        if (internal.isNodeRegisteredWithinEndpoint()) {
            throw new CloudioModificationException(
                "A CloudioAdLibNode's structure can only be modified before it is registered within the endpoint!");
        }
        try {
            T t = clazz.newInstance();
            addObject(name, t);
            return t;
        } catch (InstantiationException e) {
            throw new InvalidCloudioObjectException(e);
        } catch (IllegalAccessException e) {
            throw new InvalidCloudioObjectException(e);
        }
    }

    /**
     * Adds the given object to this custom object under the given name.
     *
     * @param name                          Name to give to the object inside the node.
     * @param object                        The object to add.
     * @throws DuplicateItemException  If there already exists an object with the given name.
     */
    public void addObject(final String name, final CloudioObject object) throws DuplicateItemException {
        if (internal.isNodeRegisteredWithinEndpoint()) {
            throw new CloudioModificationException(
                "A CloudioAdLibNode's structure can only be modified before it is registered within the endpoint!");
        }

        object.internal.setParentObjectContainer(internal);
        object.internal.setName(name);
        ((InternalDynamicObject)internal).dynamicObjects.addItem(object.internal);
    }

    /**
     * Returns the attribute with the given name or null if no attribute with the given name is part of the object.
     *
     * @param name  Name of the attribute to return.
     * @return      The attribute with the given name or null if the attribute does not exists.
     */
    public CloudioAttribute getAttribute(final String name) {
        return ((InternalDynamicObject)internal).dynamicAttributes.getItem(name).getExternalAttribute();
    }

    /**
     * Adds a new attribute of the given type (class) and the given name to the object. Note that if there is already
     * an attribute with the given name present or the type of the attribute is invalid, an exception will be thrown.
     *
     * @param name                          Name of the attribute to add.
     * @param type                          Type of the attribute to add.
     * @param constraint                    Attribute's constraint.
     * @param <T>                           Attribute's type.
     * @return                              Reference to the newly instantiated attribute.
     * @throws DuplicateItemException  If there already exists an attribute with the given name.
     * @throws InvalidCloudioAttributeException    If the type of the attribute is not supported.
     */
    public <T> CloudioAttribute<T> addAttribute(final String name, final Class<T> type, CloudioAttributeConstraint constraint)
        throws DuplicateItemException, InvalidCloudioAttributeException {
        if (internal.isNodeRegisteredWithinEndpoint()) {
            throw new CloudioModificationException(
                "A CloudioAdLibObject's structure can only be modified before it is registered within the endpoint!");
        }

        CloudioAttribute<T> attribute = new CloudioAttribute<T>();
        attribute.internal.setParent(internal);
        attribute.internal.setName(name);
        attribute.internal.setType(type);
        attribute.internal.setConstraint(constraint);
        ((InternalDynamicObject)internal).dynamicAttributes.addItem(attribute.internal);

        return attribute;
    }

    /**
     * Adds a new attribute of the given type (class) and the given name to the object. Note that if there is already
     * an attribute with the given name present or the type of the attribute is invalid, an exception will be thrown.
     *
     * @param name                              Name of the attribute to add.
     * @param type                              Type of the attribute to add.
     * @param constraint                        Attribute's constraint.
     * @param initialValue                      Initial value for the attribute.
     * @param <T>                               Attribute's type.
     * @return                                  Reference to the newly instantiated attribute.
     * @throws DuplicateItemException      If there already exists an attribute with the given name.
     * @throws InvalidCloudioAttributeException        If the type of the attribute is not supported.
     * @throws CloudioAttributeInitializationException If the attribute could not be initialized.
     * @throws CloudioAttributeConstraintException     If the attribute can not be initialized (constraint).
     */
    public <T> CloudioAttribute<T> addAttribute(final String name, final Class<T> type, CloudioAttributeConstraint constraint,
                                                T initialValue)
        throws DuplicateItemException, InvalidCloudioAttributeException, CloudioAttributeInitializationException,
        CloudioAttributeConstraintException {
        CloudioAttribute<T> attribute = addAttribute(name, type, constraint);
        attribute.setInitialDynamicValue(initialValue);
        return attribute;
    }

    /**
     * Declares conformance of the object for a given data class. Note that a cloud.io objects can only declare
     * conformance for exactly one data class, it you try to declare a second conformance , a runtime exception will
     * be thrown. Additionally this method can only be called before the object is part of an endpoint structure.
     *
     * @param dataClass Data class the object conforms to.
     */
    public void declareConformance(final String dataClass) {
        if (internal.isNodeRegisteredWithinEndpoint()) {
            throw new CloudioModificationException(
                "An object can only declare conformance before it is part of an endpoint structure!");
        }

        internal.setConforms(dataClass);
    }

    /**
     * Builder to enable convenient custom object creation.
     */
    public static class Builder {
        private final CloudioDynamicObject object = new CloudioDynamicObject();

        /**
         * Adds an object of the given class with the given name to the custom object.
         *
         * @param name                          Name to give to the object after creation.
         * @param clazz                         The class of the object to create and add.
         * @param <T>                           Type of the object.
         * @return                              Returns a reference to the builder in order to chain method calls.
         * @throws DuplicateItemException  If there already exists an object with the given name.
         * @throws InvalidCloudioObjectException       The object class is invalid.
         */
        public <T extends CloudioObject> Builder object(final String name, final Class<T> clazz)
            throws DuplicateItemException, InvalidCloudioObjectException {
            object.addObject(name, clazz);
            return this;
        }

        /**
         * Adds the given object to this custom object under the given name.
         *
         * @param name                          Name to give to the object inside the object.
         * @param object                        The object to add.
         * @return                              Returns a reference to the builder in order to chain method calls.
         * @throws DuplicateItemException  If there already exists an object with the given name.
         */
        public Builder object(final String name, final CloudioObject object) throws DuplicateItemException {
            this.object.addObject(name, object);
            return this;
        }

        /**
         * Adds a new attribute of the given type (class) and the given name to the object. Note that if there is
         * already an attribute with the given name present or the type of the attribute is invalid, an exception will
         * be thrown.
         *
         * @param name                          Name of the attribute to add.
         * @param type                          Type of the attribute to add.
         * @param constraint                    Attribute's constraint.
         * @return                              Returns a reference to the builder in order to chain method calls.
         * @throws DuplicateItemException  If there already exists an attribute with the given name.
         */
        public Builder attribute(final String name, final Class type, CloudioAttributeConstraint constraint)
            throws DuplicateItemException {
            object.addAttribute(name, type, constraint);
            return this;
        }

        /**
         * Adds a new attribute of the given type (class) and the given name to the object and initializes the attribute
         * with the given initial value. Note that if there is already an attribute with the given name present or the
         * type of the attribute is invalid, an exception will be thrown.
         *
         * @param name                              Name of the attribute to add.
         * @param type                              Type of the attribute to add.
         * @param constraint                        Attribute's constraint.
         * @param initialValue                      Initial value for the attribute.
         * @param <T>                               Type of the attribute.
         * @return                                  Returns a reference to the builder in order to chain method calls.
         * @throws CloudioAttributeInitializationException If the attribute could not be initialized.
         * @throws CloudioAttributeConstraintException     If the attribute can not be initialized (constraint).
         * @throws DuplicateItemException      If there already exists an attribute with the given name.
         * @throws InvalidCloudioAttributeException        If the type of the attribute is not supported.
         */
        public <T> Builder attribute(final String name, final Class<T> type, CloudioAttributeConstraint constraint,
                                     final T initialValue)
            throws CloudioAttributeInitializationException, CloudioAttributeConstraintException, DuplicateItemException,
            InvalidCloudioAttributeException {
            object.addAttribute(name, type, constraint, initialValue);
            return this;
        }

        /**
         * Adds a new attribute of the given type (class) and the given name to the object and adds the given attribute
         * listener to the attribute. Note that if there is already an attribute with the given name present or the
         * type of the attribute is invalid, an exception will be thrown.
         *
         * @param name                          Name of the attribute to add.
         * @param type                          Type of the attribute to add.
         * @param constraint                    Attribute's constraint.
         * @param listener                      CloudioAttributeListener to add to the new attribute.
         * @return                              Returns a reference to the builder in order to chain method calls.
         * @throws DuplicateItemException  If there already exists an attribute with the given name.
         * @throws InvalidCloudioAttributeException    If the type of the attribute is not supported.
         */
        public Builder attribute(final String name, final Class type, CloudioAttributeConstraint constraint,
                                 final CloudioAttributeListener listener)
            throws DuplicateItemException, InvalidCloudioAttributeException {
            CloudioAttribute attribute = object.addAttribute(name, type, constraint);
            attribute.addListener(listener);
            return this;
        }

        /**
         * Adds a new attribute of the given type (class) and the given name to the object, initializes the attribute
         * with the given initial value and adds the given attribute listener to the attribute. Note that if there is
         * already an attribute with the given name present or the type of the attribute is invalid, an exception will
         * be thrown.
         *
         * @param name                              Name of the attribute to add.
         * @param type                              Type of the attribute to add.
         * @param constraint                        Attribute's constraint.
         * @param initialValue                       Initial value for the attribute.
         * @param listener                          CloudioAttributeListener to add to the new attribute.
         * @param <T>                               Type of the attribute.
         * @return                                  Returns a reference to the builder in order to chain method calls.
         * @throws CloudioAttributeInitializationException If the attribute could not be initialized.
         * @throws CloudioAttributeConstraintException     If the attribute can not be initialized (constraint).
         * @throws DuplicateItemException      If there already exists an attribute with the given name.
         * @throws InvalidCloudioAttributeException        If the type of the attribute is not supported.
         */
        public <T> Builder attribute(final String name, final Class<T> type, CloudioAttributeConstraint constraint,
                                     final T initialValue, CloudioAttributeListener<T> listener)
            throws CloudioAttributeInitializationException, CloudioAttributeConstraintException, DuplicateItemException,
            InvalidCloudioAttributeException {
            CloudioAttribute<T> attribute = object.addAttribute(name, type, constraint, initialValue);
            attribute.addListener(listener);
            return this;
        }

        /**
         * Declares conformance of the object for a given data class. Note that a cloud.io objects can only declare
         * conformance for exactly one data class, it you try to declare a second conformance , a runtime exception will
         * be thrown.
         *
         * @param dataClass     Data class the object conforms to.
         * @return              Returns a reference to the builder in order to chain method calls.
         */
        public Builder conforms(final String dataClass) {
            object.declareConformance(dataClass);
            return this;
        }

        /**
         * Finishes building the actual object and returns a reference to the object.
         *
         * @return  Object.
         */
        public CloudioDynamicObject build() {
            return object;
        }
    }

    /*** Internal API *************************************************************************************************/
    class InternalDynamicObject extends CloudioObject.InternalObject {
        private final NamedItemSet<CloudioObject.InternalObject> dynamicObjects = new NamedItemSet<CloudioObject.InternalObject>();
        private final NamedItemSet<CloudioAttribute.InternalAttribute> dynamicAttributes =
            new NamedItemSet<CloudioAttribute.InternalAttribute>();

        @Override
        public NamedItemSet<CloudioAttribute.InternalAttribute> getAttributes() {
            return dynamicAttributes;
        }

        @Override
        public NamedItemSet<InternalObject> getObjects() {
            return dynamicObjects;
        }
    }
}
