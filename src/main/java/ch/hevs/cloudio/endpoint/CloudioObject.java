package ch.hevs.cloudio.endpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Stack;

/**
 * Base class for all cloud.iO objects. An object can either contain attributes (CloudioAttribute, @StaticAttribute) or child
 * objects. Using this it is possible to create data models with a great flexibility.
 * <br><br>
 * An object can be annotated with the @Conforms annotation to conform to a cloud.io class. A class in cloud.io is just
 * a scheme what attributes and child objects an object has to have. An object is conform to such a scheme if it
 * matches exactly the structure of the class. It can not contain more attributes or child objects, then it would be
 * not anymore conform to that class.
 *
 * Todo: Add example code.
 */
public class CloudioObject {
    private static final Logger log = LogManager.getLogger(CloudioObject.class);
    InternalObject internal = new InternalObject();

    /*** Internal API *************************************************************************************************/
    class InternalObject implements CloudioObjectContainer, CloudioAttributeContainer {
        // *** Attributes *********************************************************************************************/
        private CloudioObjectContainer parent = null;
        private String name;
        private String conforms = null;
        private final NamedItemSet<InternalObject> objects = new NamedItemSet<InternalObject>();
        private final NamedItemSet<CloudioAttribute.InternalAttribute> attributes =
            new NamedItemSet<CloudioAttribute.InternalAttribute>();
        private boolean staticAttributesAdded = false;

        public InternalObject() {
            try {
                // Check each field of the object class.
                for (Field field : CloudioObject.this.getClass().getFields()) {
                    // If the field extends cloud.io CloudioObject abstract class...
                    if (CloudioObject.class.isAssignableFrom(field.getType())) {
                        // ... create an instance of the object.
                        CloudioObject object = (CloudioObject)field.getType().newInstance();

                        // Set the name and the parent of the object.
                        object.internal.setName(field.getName());
                        object.internal.setParentObjectContainer(this);

                        // Set the actual object's field to the newly created instance.
                        field.set(CloudioObject.this, object);

                        // Add the object to the list of objects.
                        try {
                            objects.addItem(object.internal);
                        } catch (DuplicateNamedItemException e) {
                            throw new CloudioModificationException(
                                "Duplicate name for fields, your Java compiler sucks ;-)");
                        }
                    }
                    // If the field extends CloudioAttribute abstract class...
                    else if (CloudioAttribute.class.isAssignableFrom(field.getType())) {
                        // ... create an instance of the attribute.
                        CloudioAttribute attribute = (CloudioAttribute)field.getType().newInstance();

                        // Set the name and the parent of the attribute.
                        attribute.internal.setType(
                                ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
                        attribute.internal.setName(field.getName());
                        attribute.internal.setParent(this);

                        if (field.getAnnotation(StaticAttribute.class) != null) {
                            throw new InvalidCloudioAttributeException("Static attributes should not be of type Attribute, " +
                                    "use native types for static attributes.");
                        } else if (field.getAnnotation(Parameter.class) != null) {
                            attribute.internal.setConstraint(CloudioAttributeConstraint.Parameter);
                        } else if (field.getAnnotation(Status.class) != null) {
                            attribute.internal.setConstraint(CloudioAttributeConstraint.Status);
                        } else if (field.getAnnotation(SetPoint.class) != null) {
                            attribute.internal.setConstraint(CloudioAttributeConstraint.SetPoint);
                        } else if (field.getAnnotation(Measure.class) != null) {
                            attribute.internal.setConstraint(CloudioAttributeConstraint.Measure);
                        } else {
                            throw new InvalidCloudioAttributeException("Attributes must be annotated with their constraint?");
                        }

                        // Set the actual object's field to the new instance.
                        field.set(CloudioObject.this, attribute);

                        // Add the object to the list of attributes.
                        try {
                            attributes.addItem(attribute.internal);
                        } catch (DuplicateNamedItemException e) {
                            throw new CloudioModificationException(
                                "Duplicate name for fields, your Java compiler sucks ;-)");
                        }
                    }

                    // Is it a static value?
                    else if (field.getAnnotation(StaticAttribute.class) != null) {
                        if (field.getType() != boolean.class &&
                            field.getType() != short.class &&
                            field.getType() != int.class &&
                            field.getType() != long.class &&
                            field.getType() != float.class &&
                            field.getType() != double.class &&
                            field.getType() != String.class) {
                            throw new InvalidCloudioAttributeException(field.getType());
                        }
                    }
                }
            } catch (InstantiationException e) {
                throw new InvalidCloudioObjectException(e);
            } catch (IllegalAccessException e) {
                throw new InvalidCloudioObjectException(e);
            }

            // If the object class declares conformance, add it.
            Conforms annotation = CloudioObject.this.getClass().getAnnotation(Conforms.class);
            if (annotation != null) {
                conforms = annotation.value();
            }
        }

        CloudioObject getExternalObject() {
            return CloudioObject.this;
        }

        /*** AttributeContainer/ObjectContainer implementation ********************************************************/
        @Override
        public void attributeHasChangedByEndpoint(final CloudioAttribute.InternalAttribute attribute) {
            if (parent != null) {
                parent.attributeHasChangedByEndpoint(attribute);
            }
        }

        @Override
        public void attributeHasChangedByCloud(final CloudioAttribute.InternalAttribute attribute) {
            if (parent != null) {
                parent.attributeHasChangedByCloud(attribute);
            }
        }

        @Override
        public boolean isNodeRegisteredWithinEndpoint() {
            return parent != null && parent.isNodeRegisteredWithinEndpoint();
        }

        @Override
        public CloudioObjectContainer getParentObjectContainer() {
            return parent;
        }

        @Override
        public void setParentObjectContainer(final CloudioObjectContainer parent) {
            // If the object already has a parent (we are moving the object) then fail with a runtime exception.
            if (this.parent != null) {
                throw new CloudioModificationException("The parent of an Object can never be changed " +
                        "(Objects can not be moved)!");
            }

            // Set the parent.
            this.parent = parent;
        }

        /*** AttributeContainer implementation ************************************************************************/
        @Override
        public NamedItemSet<CloudioAttribute.InternalAttribute> getAttributes() {
            if (!staticAttributesAdded) {
                for (Field field : CloudioObject.this.getClass().getFields()) {
                    // If it is a static value...
                    if (field.getAnnotation(StaticAttribute.class) != null) {
                        // ...of type boolean, then create a boolean attribute on the fly.
                        if (field.getType() == boolean.class) {
                            CloudioAttribute<Boolean>.InternalAttribute attribute = new CloudioAttribute<Boolean>().internal;
                            try {
                                attribute.setConstraint(CloudioAttributeConstraint.Static);
                                attribute.setType(Boolean.class);
                                attribute.setName(field.getName());
                                attribute.setParent(this);
                                try {
                                    attribute.setStaticValue((Boolean) field.get(CloudioObject.this));
                                } catch (CloudioAttributeConstraintException exception) {
                                    log.error("Exception: " + exception.getMessage());
                                    exception.printStackTrace();
                                }
                                try {
                                    attributes.addItem(attribute);
                                } catch (DuplicateNamedItemException e) {
                                    throw new CloudioModificationException("Duplicate name for fields, " +
                                        "your Java compiler sucks ;-)");
                                }
                            } catch (InvalidCloudioAttributeException exception) {
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                            } catch (IllegalAccessException exception) {
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                            }

                            // ...of a supported integer type, then create a integer attribute on the fly.
                        } else if (field.getType() == short.class || field.getType() == int.class ||
                            field.getType() == long.class) {
                            CloudioAttribute<Long>.InternalAttribute attribute = new CloudioAttribute<Long>().internal;
                            try {
                                attribute.setConstraint(CloudioAttributeConstraint.Static);
                                attribute.setType(Long.class);
                                attribute.setName(field.getName());
                                attribute.setParent(this);
                                try {
                                    attribute.setStaticValue((
                                        (Number) field.get(CloudioObject.this)).longValue());
                                } catch (CloudioAttributeConstraintException exception) {
                                    log.error("Exception: " + exception.getMessage());
                                    exception.printStackTrace();
                                }
                                try {
                                    attributes.addItem(attribute);
                                } catch (DuplicateNamedItemException e) {
                                    throw new CloudioModificationException("Duplicate name for fields, " +
                                        "your Java compiler sucks ;-)");
                                }
                            } catch (InvalidCloudioAttributeException exception) {
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                            } catch (IllegalAccessException exception) {
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                            }

                            // ...of a supported floating point type, then create a number attribute on the fly.
                        } else if (field.getType() == float.class || field.getType() == double.class) {
                            CloudioAttribute<Double>.InternalAttribute attribute = new CloudioAttribute<Double>().internal;
                            try {
                                attribute.setConstraint(CloudioAttributeConstraint.Static);
                                attribute.setType(Double.class);
                                attribute.setName(field.getName());
                                attribute.setParent(this);
                                try {
                                    attribute.setStaticValue(
                                        ((Number) field.get(CloudioObject.this)).doubleValue());
                                } catch (CloudioAttributeConstraintException exception) {
                                    log.error("Exception: " + exception.getMessage());
                                    exception.printStackTrace();
                                }
                                try {
                                    attributes.addItem(attribute);
                                } catch (DuplicateNamedItemException exception) {
                                    throw new CloudioModificationException("Duplicate name for fields, " +
                                        "your Java compiler sucks ;-)");
                                }
                            } catch (InvalidCloudioAttributeException exception) {
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                            } catch (IllegalAccessException exception) {
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                            }

                            // ...of string type, then create a String attribute on the fly.
                        } else if (field.getType() == String.class) {
                            CloudioAttribute<String>.InternalAttribute attribute = new CloudioAttribute<String>().internal;
                            try {
                                attribute.setConstraint(CloudioAttributeConstraint.Static);
                                attribute.setType(String.class);
                                attribute.setName(field.getName());
                                attribute.setParent(this);
                                try {
                                    attribute.setStaticValue((String) field.get(CloudioObject.this));
                                } catch (CloudioAttributeConstraintException exception) {
                                    log.error("Exception: " + exception.getMessage());
                                    exception.printStackTrace();
                                }
                                try {
                                    attributes.addItem(attribute);
                                } catch (DuplicateNamedItemException exception) {
                                    throw new CloudioModificationException("Duplicate name for fields, " +
                                        "your Java compiler sucks ;-)");
                                }
                            } catch (InvalidCloudioAttributeException exception) {
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                            } catch (IllegalAccessException exception) {
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                            }
                        } else {
                            // The static attribute format is not supported.
                            throw new InvalidCloudioAttributeException(field.getType());
                        }
                    }
                }

                staticAttributesAdded = true;
            }

            return attributes;
        }

        /*** ObjectContainer implementation ***************************************************************************/
        @Override
        public NamedItemSet<InternalObject> getObjects() {
            return objects;
        }

        @Override
        public CloudioNodeContainer getParentNodeContainer() {
            return null;
        }

        @Override
        public void setParentNodeContainer(CloudioNodeContainer nodeContainer) {
            throw new CloudioModificationException(
                "As this is not a node, it can not be embedded into a node container!");
        }

        @Override
        public CloudioAttribute.InternalAttribute findAttribute(Stack<String> location) {
            if (!location.isEmpty()) {
                if ("objects".equals(location.peek())) {
                    location.pop();
                    if (!location.isEmpty()) {
                        CloudioObject.InternalObject object = getObjects().getItem(location.pop());
                        if (object != null) {
                            return object.findAttribute(location);
                        }
                    }
                } else if ("attributes".equals(location.peek())) {
                    getAttributes();
                    location.pop();
                    if (!location.isEmpty()) {
                        CloudioAttribute.InternalAttribute attribute = getAttributes().getItem(location.pop());
                        if (attribute != null && location.isEmpty()) {
                            return attribute;
                        }
                    }
                }
            }

            return null;
        }

        /*** UniqueIdentifiable implementation ************************************************************************/
        @Override
        public Uuid getUuid() {
            return new TopicUuid(this);
        }

        /*** NamedItem implementation *********************************************************************************/
        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            // If the object already has a name (we are renaming the object) then fail with a runtime exception.
            if (this.name != null) {
                throw new CloudioModificationException(
                    "The Object has already a name (Renaming objects is forbidden)!");
            }

            // Set the local name.
            this.name = name;
        }

        /*** Package private methods **********************************************************************************/
        String getConforms() {
            return conforms;
        }

        void setConforms(final String dataClass) {
            if (conforms != null) {
                throw new CloudioModificationException("The Object has already a conformance, can not be modified!");
            } else {
                conforms = dataClass;
            }
        }

        void close() {
            parent = null;

            for (CloudioObject.InternalObject object: objects) {
                object.close();
            }
            objects.clear();

            for (CloudioAttribute.InternalAttribute attribute: attributes) {
                attribute.close();
            }
            attributes.clear();
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            close();
        }
    }
}
