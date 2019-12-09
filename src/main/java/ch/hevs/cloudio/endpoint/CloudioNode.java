package ch.hevs.cloudio.endpoint;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/**
 * A Node instance can represent a functionality of a given endpoint device or a connected device in the case the
 * endpoint device is a gateway which connects wireless or wired devices with the cloud.
 */
public abstract class CloudioNode {
    InternalNode internal = new InternalNode();

    /*** Internal API *************************************************************************************************/
    class InternalNode implements CloudioObjectContainer {
        /*** Attributes ***********************************************************************************************/
        private CloudioNodeContainer parent = null;
        private String name = null;
        private final Set<String> interfaces = new TreeSet<String>();
        private final NamedItemSet<CloudioObject.InternalObject> objects = new NamedItemSet<CloudioObject.InternalObject>();

        public InternalNode() {
            try {
                // Check each field of the node class.
                for (Field field : CloudioNode.this.getClass().getFields()) {
                    // If the field extends cloud.io CloudioObject abstract class...
                    if (CloudioObject.class.isAssignableFrom(field.getType())) {

                        // ... create an instance of the object.
                        CloudioObject object = (CloudioObject)field.getType().newInstance();

                        // Set the name and the endpoint of the object.
                        object.internal.setName(field.getName());
                        object.internal.setParentObjectContainer(this);

                        // Set the actual object's field to the new instance.
                        field.set(CloudioNode.this, object);

                        // Add the object to the list of objects.
                        try {
                            objects.addItem(object.internal);
                        } catch (DuplicateItemException e) {
                            throw new CloudioModificationException(
                                "Duplicate name for fields, your Java compiler sucks ;-)");
                        }
                    }
                }
            } catch (InstantiationException e) {
                throw new InvalidCloudioNodeException();
            } catch (IllegalAccessException e) {
                throw new InvalidCloudioNodeException();
            }

            // If the node class or any superclass of the node declares implemented interfaces, add them.
            for (Class c = CloudioNode.this.getClass(); c != CloudioNode.class; c = c.getSuperclass()) {
                Implements annotation = (Implements)c.getAnnotation(Implements.class);
                if (annotation != null) {
                    Collections.addAll(interfaces, annotation.value());
                }
            }
        }

        /*** ObjectContainer implementation ***************************************************************************/
        @Override
        public void attributeHasChangedByEndpoint(CloudioAttribute.InternalAttribute attribute) {
            if (parent != null) {
                parent.attributeHasChangedByEndpoint(attribute);
            }
        }

        @Override
        public void attributeHasChangedByCloud(CloudioAttribute.InternalAttribute attribute) {
            if (parent != null) {
                parent.attributeHasChangedByCloud(attribute);
            }
        }

        @Override
        public boolean isNodeRegisteredWithinEndpoint() {
            return parent != null;
        }

        @Override
        public NamedItemSet<CloudioObject.InternalObject> getObjects() {
            return objects;
        }

        @Override
        public CloudioObjectContainer getParentObjectContainer() {
            return null;
        }

        @Override
        public void setParentObjectContainer(CloudioObjectContainer objectContainer) {
            throw new CloudioModificationException("A node can not have an object container as parent!");
        }

        @Override
        public CloudioNodeContainer getParentNodeContainer() {
            return parent;
        }

        @Override
        public void setParentNodeContainer(CloudioNodeContainer nodeContainer) {
            // If the object already has a parent (we are moving the object) then fail with a runtime exception.
            if (this.parent != null) {
                throw new CloudioModificationException("The parent of a Node can never be changed " +
                        "(Nodes can not be moved)!");
            }

            // Set the parent.
            parent = nodeContainer;
        }

        @Override
        public CloudioAttribute.InternalAttribute findAttribute(Stack<String> location) {
            if (!location.isEmpty()) {
                CloudioObject.InternalObject object = getObjects().getItem(location.pop());
                if (object != null) {
                    return object.findAttribute(location);
                }
            }

            return null;
        }

        /*** UniqueIdentifiable Implementation ************************************************************************/
        @Override
        public Uuid getUuid() {
            return new TopicUuid(this);
        }

        /*** NamedItem Implementation *********************************************************************************/
        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            // If the node already has a name (we are renaming the node) then fail with a runtime exception.
            if (this.name != null) {
                throw new CloudioModificationException("The Node has already a name (Renaming nodes is forbidden)!");
            }

            // Set the local name.
            this.name = name;
        }

        /*** Package private methods **********************************************************************************/
        Set<String> getInterfaces() {
            return interfaces;
        }

        void close() {
            parent = null;

            for (CloudioObject.InternalObject object: objects) {
                object.close();
            }
            objects.clear();
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            close();
        }

        CloudioNode getExternalNode() {
            return CloudioNode.this;
        }
    }
}
