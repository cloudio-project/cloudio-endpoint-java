package ch.hevs.cloudio.endpoint;

import java.util.Iterator;

/**
 * The CloudioDynamicNode class allows you to create the structure of a cloud.iO node at runtime in contrast to the static model used by the CloudioNode class.
 *
 * TODO: Example.
 */
public class CloudioDynamicNode extends CloudioNode {
    /**
     * Creates a new dynamic (runtime constructable) node.
     */
    public CloudioDynamicNode() {
        internal = new InternalDynamicNode();
    }

    /**
     * Returns the object with the given name or null if no object with the given name is part of the node.
     *
     * @param name  Name of the object to return.
     * @return      Object or null if no object with the given name is present.
     */
    public CloudioObject getObject(final String name) {
        return ((InternalDynamicNode)internal).dynamicObjects.getItem(name).getExternalObject();
    }

    /**
     * Adds an object of the given class with the given name to the custom node.
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
            throw new RuntimeException("A CloudioAdLibNode's structure can only be modified before it is registered within" +
                " the endpoint!");
        }
        try {
            T t = clazz.newInstance();
            addObject(name, t);
            return t;
        } catch (InstantiationException exception) {
            throw new InvalidCloudioObjectException(exception);
        } catch (IllegalAccessException exception) {
            throw new InvalidCloudioObjectException(exception);
        }
    }

    /**
     * Adds the given object to this custom node under the given name.
     *
     * @param name                          Name to give to the object inside the node.
     * @param object                        The object to add.
     * @throws DuplicateItemException  If there already exists an object with the given name.
     */
    public void addObject(final String name, final CloudioObject object) throws DuplicateItemException {
        if (internal.isNodeRegisteredWithinEndpoint()) {
            throw new RuntimeException("A CloudioAdLibNode's structure can only be modified before it is registered within" +
                " the endpoint!");
        }

        object.internal.setParentObjectContainer(internal);
        object.internal.setName(name);
        ((InternalDynamicNode)internal).dynamicObjects.addItem(object.internal);
    }

    /**
     * Declares that the node implements the given interface. Note that you have to declare all implemented
     * interfaces actually before the node is added to the endpoint, otherwise a runtime exception will be
     * thrown.
     *
     * @param interface_    Interface to be declared implemented by the node.
     */
    public void declareImplementedInterface(final String interface_) {
        if (internal.isNodeRegisteredWithinEndpoint()) {
            throw new RuntimeException("Node is already registered within an endpoint, declaring implemented" +
                " interfaces is only possible as long as the node is not online (registered within endpoint)");
        }

        if (!internal.getInterfaces().contains(interface_))
            internal.getInterfaces().add(interface_);
    }

    /**
     * Declares that the node implements the given interfaces. Note that you have to declare all implemented
     * interfaces actually before the node is added to the endpoint, otherwise a runtime exception will be
     * thrown.
     *
     * @param interfaces    Interfaces to be declared implemented by the node.
     */
    public void declareImplementedInterfaces(final String... interfaces) {
        for (String interface_: interfaces) {
            declareImplementedInterface(interface_);
        }
    }

    /**
     * Builder to enable convenient custom node instantiation.
     */
    public static class Builder {
        private final CloudioDynamicNode node = new CloudioDynamicNode();

        /**
         * Adds an object of the given class with the given name to the custom node.
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
            node.addObject(name, clazz);
            return this;
        }

        /**
         * Adds the given object to this custom node under the given name.
         *
         * @param name                          Name to give to the object inside the node.
         * @param object                        The object to add.
         * @return                              Returns a reference to the builder in order to chain method calls.
         * @throws DuplicateItemException  If there already exists an object with the given name.
         */
        public Builder object(final String name, final CloudioObject object) throws DuplicateItemException {
            node.addObject(name, object);
            return this;
        }

        /**
         * Declares the given interfaces as implemented by the node in construction.
         *
         * @param interfaces    List of interfaces to be declared as implemented by the node.
         * @return              Returns a reference to the builder in order to chain method calls.
         */
        public Builder implement(final String... interfaces) {
            node.declareImplementedInterfaces(interfaces);
            return this;
        }

        /**
         * Finishes building the actual node and returns a reference to the node.
         *
         * @return  Node.
         */
        public CloudioDynamicNode build() {
            return node;
        }
    }

    /*** Internal API *************************************************************************************************/
    private class InternalDynamicNode extends CloudioNode.InternalNode {
        private final NamedItemSet<CloudioObject.InternalObject> dynamicObjects = new NamedItemSet<CloudioObject.InternalObject>();

        public InternalDynamicNode()
        {
            super();
            NamedItemSet<CloudioObject.InternalObject> staticObjects = getStaticsObjects();
            if(!staticObjects.isEmpty()) {
                Iterator<CloudioObject.InternalObject> objectIterator = staticObjects.iterator();
                while (objectIterator.hasNext()) {
                    try {
                        CloudioObject.InternalObject newObject = objectIterator.next();
                        dynamicObjects.addItem(newObject);
                    } catch (DuplicateItemException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        @Override
        public NamedItemSet<CloudioObject.InternalObject> getObjects() {
            return dynamicObjects;
        }
    }
}
