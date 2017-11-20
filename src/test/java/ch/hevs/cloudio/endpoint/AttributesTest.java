package ch.hevs.cloudio.endpoint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Stack;

public class AttributesTest implements CloudioAttributeContainer, CloudioAttributeListener {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private CloudioAttributeType expectedAttributeType = null;
    private CloudioAttributeConstraint expectedAttributeConstraint = null;
    private java.lang.Object expectedAttributeValue = null;
    private boolean shouldListenerBeNotified = false;
    private boolean wasListenerBeNotified = false;
    private boolean shouldContainerBeNotified = false;
    private boolean wasContainerBeNotified = false;
    private boolean shouldContainerBeNotifiedCloud = false;
    private boolean wasContainerBeNotifiedCloud = false;
    private boolean isRegistered = false;

    <T> CloudioAttribute<T> createAttribute(Class<T> clazz, CloudioAttributeConstraint constraint) throws Exception {
        CloudioAttribute<T> attribute = new CloudioAttribute<T>();
        attribute.internal.setType(clazz);
        attribute.internal.setConstraint(constraint);
        attribute.internal.setName("toto");
        attribute.internal.setParent(this);

        Assert.assertSame(attribute.internal.getConstraint(), constraint);
        Assert.assertEquals(attribute.internal.getName(), "toto");
        Assert.assertSame(attribute.internal.getParent(), this);
        Assert.assertEquals(attribute.internal.getUuid().toString(),
                "TEST/nodes/TEST/objects/TEST/attributes/toto");

        expectedAttributeType = attribute.internal.getType();
        expectedAttributeConstraint = constraint;
        return attribute;
    }

    @Before
    public void initTestVariables() {
        expectedAttributeType = null;
        expectedAttributeConstraint = null;
        expectedAttributeValue = null;
        shouldListenerBeNotified = false;
        shouldContainerBeNotified = false;
        wasListenerBeNotified = false;
        wasContainerBeNotified = false;
        shouldContainerBeNotifiedCloud = false;
        wasContainerBeNotifiedCloud = false;
        isRegistered = false;
    }

    @Test
    public void testStaticBooleanAttributeStatic() throws Exception {
        CloudioAttribute<Boolean> attribute = createAttribute(Boolean.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), Boolean.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.Boolean);

        attribute.internal.setStaticValue(true);
        Assert.assertEquals(attribute.getValue(), true);
    }

    @Test
    public void testStaticBooleanAttributeInitException() throws Exception {
        CloudioAttribute<Boolean> attribute = createAttribute(Boolean.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), Boolean.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.Boolean);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.setInitialValue(true);
    }

    @Test
    public void testStaticShortAttributeStatic() throws Exception {
        CloudioAttribute<Short> attribute = createAttribute(Short.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), Short.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.Integer);

        attribute.internal.setStaticValue((short) 42);
        Assert.assertSame(attribute.getValue(), (short) 42);
    }

    @Test
    public void testStaticShortAttributeInitException() throws Exception {
        CloudioAttribute<Short> attribute = createAttribute(Short.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), Short.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.Integer);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.setInitialValue((short) 42);
    }

    @Test
    public void testStaticIntegerAttributeStatic() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), Integer.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.Integer);

        attribute.internal.setStaticValue(42);
        Assert.assertSame(attribute.getValue(), 42);
    }

    @Test
    public void testStaticIntegerAttributeInitException() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), Integer.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.Integer);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.setInitialValue(42);
    }

    @Test
    public void testStaticLongAttributeStatic() throws Exception {
        CloudioAttribute<Long> attribute = createAttribute(Long.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), Long.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.Integer);

        attribute.internal.setStaticValue(42L);
        Assert.assertSame(attribute.getValue(), 42L);
    }

    @Test
    public void testStaticLongAttributeInitException() throws Exception {
        CloudioAttribute<Long> attribute = createAttribute(Long.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), Long.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.Integer);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.setInitialValue(42L);
    }

    @Test
    public void testStaticFloatAttributeStatic() throws Exception {
        CloudioAttribute<Float> attribute = createAttribute(Float.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), Float.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.Number);

        attribute.internal.setStaticValue(42.123f);
        Assert.assertTrue(attribute.getValue().equals(42.123f));
    }

    @Test
    public void testStaticFloatAttributeInitException() throws Exception {
        CloudioAttribute<Float> attribute = createAttribute(Float.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), Float.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.Number);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.setInitialValue(42.123f);
    }

    @Test
    public void testStaticDoubleAttributeStatic() throws Exception {
        CloudioAttribute<Double> attribute = createAttribute(Double.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), Double.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.Number);

        attribute.internal.setStaticValue(42.123);
        Assert.assertTrue(attribute.getValue().equals(42.123));
    }

    @Test
    public void testStaticDoubleAttributeInitException() throws Exception {
        CloudioAttribute<Double> attribute = createAttribute(Double.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), Double.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.Number);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.setInitialValue(42.123);
    }

    @Test
    public void testStaticStringAttributeStatic() throws Exception {
        CloudioAttribute<String> attribute = createAttribute(String.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), String.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.String);

        attribute.internal.setStaticValue("Hello world");
        Assert.assertEquals(attribute.getValue(), "Hello world");
    }

    @Test
    public void testStaticStringAttributeInitException() throws Exception {
        CloudioAttribute<String> attribute = createAttribute(String.class, CloudioAttributeConstraint.Static);
        Assert.assertSame(attribute.getType(), String.class);
        Assert.assertSame(attribute.internal.getType(), CloudioAttributeType.String);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.setInitialValue("Hello world");
    }

    @Test
    public void testInvalidAttributeInitException() throws Exception {
        exception.expect(InvalidCloudioAttributeException.class);
        createAttribute(CloudioObject.class, CloudioAttributeConstraint.Static);
    }

    @Test
    public void testStaticAttributeUpdateException() throws Exception {
        CloudioAttribute<Boolean> attribute = createAttribute(Boolean.class, CloudioAttributeConstraint.Static);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.setValue(true);
    }

    @Test
    public void testStaticAttributeUpdateFromCloudException() throws Exception {
        CloudioAttribute<Boolean> attribute = createAttribute(Boolean.class, CloudioAttributeConstraint.Static);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.internal.setValueFromCloud(true, System.currentTimeMillis());
    }

    @Test
    public void testParameterAttributeStaticException() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Parameter);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.internal.setStaticValue(55);
    }

    @Test
    public void testParameterAttributeInitException() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Parameter);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.setInitialValue(55);
    }

    @Test
    public void testParameterAttributeUpdateException() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Parameter);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.setValue(55);
    }

    @Test
    public void testParameterAttributeUpdateFromCloud() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Parameter);

        shouldContainerBeNotifiedCloud = true;
        expectedAttributeValue = 11;
        attribute.internal.setValueFromCloud(11, System.currentTimeMillis());
        Assert.assertTrue(wasContainerBeNotifiedCloud);
    }

    @Test
    public void testStatusAttributeStaticException() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Status);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.internal.setStaticValue(55);
    }

    @Test
    public void testStatusAttributeInit() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Status);

        attribute.setInitialValue(55);
        Assert.assertEquals(attribute.getValue().intValue(), 55);
        exception.expect(CloudioAttributeInitializationException.class);
        attribute.setInitialValue(55);
    }

    @Test
    public void testStatusAttributeUpdate() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Status);

        shouldContainerBeNotified = true;
        expectedAttributeValue = 88;
        attribute.setValue(88);
        Assert.assertTrue(wasContainerBeNotified);
    }

    @Test
    public void testStatusAttributeStringUpdate() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Status);

        shouldContainerBeNotified = true;
        expectedAttributeValue = 88;
        attribute.setStringValue("88");
        Assert.assertTrue(wasContainerBeNotified);
    }

    @Test
    public void testStatusAttributeStringUpdateException() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Status);

        exception.expect(NumberFormatException.class);
        attribute.setStringValue("88toto");
    }

    @Test
    public void testStatusAttributeUpdateFromCloudException() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Status);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.internal.setValueFromCloud(55, System.currentTimeMillis());
    }

    @Test
    public void testSetPointAttributeStaticException() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.SetPoint);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.internal.setStaticValue(55);
    }

    @Test
    public void testSetPointAttributeInit() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.SetPoint);

        attribute.setInitialValue(55);
        Assert.assertEquals(attribute.getValue().intValue(), 55);
        exception.expect(CloudioAttributeInitializationException.class);
        attribute.setInitialValue(55);
    }

    @Test
    public void testSetPointAttributeUpdateException() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.SetPoint);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.setValue(55);
    }

    @Test
    public void testSetPointAttributeUpdateFromCloud() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.SetPoint);

        shouldContainerBeNotifiedCloud = true;
        expectedAttributeValue = 66;
        attribute.internal.setValueFromCloud(66, System.currentTimeMillis());
        Assert.assertTrue(wasContainerBeNotifiedCloud);
    }

    @Test
    public void testMeasureAttributeStaticException() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Measure);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.internal.setStaticValue(55);
    }

    @Test
    public void testMeasureAttributeInit() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Measure);

        attribute.setInitialValue(55);
        Assert.assertEquals(attribute.getValue().intValue(), 55);
        exception.expect(CloudioAttributeInitializationException.class);
        attribute.setInitialValue(55);
    }

    @Test
    public void testMeasureAttributeUpdate() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Measure);

        shouldContainerBeNotified = true;
        expectedAttributeValue = 42;
        attribute.setValue(42);
        Assert.assertTrue(wasContainerBeNotified);
    }

    @Test
    public void testMeasureAttributeUpdateFromCloudException() throws Exception {
        CloudioAttribute<Integer> attribute = createAttribute(Integer.class, CloudioAttributeConstraint.Measure);

        exception.expect(CloudioAttributeConstraintException.class);
        attribute.internal.setValueFromCloud(55, System.currentTimeMillis());
    }

    @Test
    public void testParameterListener() throws Exception {
        CloudioAttribute<Double> attribute = createAttribute(Double.class, CloudioAttributeConstraint.Parameter);
        attribute.addListener(this);

        shouldContainerBeNotifiedCloud = true;
        shouldListenerBeNotified = true;
        expectedAttributeValue = 123.0;
        attribute.internal.setValueFromCloud(123.0, System.currentTimeMillis());
        Assert.assertTrue(wasContainerBeNotifiedCloud);
        Assert.assertTrue(wasListenerBeNotified);

        attribute.removeListener(this);
        shouldListenerBeNotified = true;
        wasListenerBeNotified = false;
        expectedAttributeValue = 153.0;
        attribute.internal.setValueFromCloud(153.0, System.currentTimeMillis());
        Assert.assertTrue(wasContainerBeNotifiedCloud);
        Assert.assertFalse(wasListenerBeNotified);
    }

    @Test
    public void testStatusListener() throws Exception {
        CloudioAttribute<Double> attribute = createAttribute(Double.class, CloudioAttributeConstraint.Status);
        attribute.addListener(this);

        shouldContainerBeNotified = true;
        shouldListenerBeNotified = true;
        expectedAttributeValue = 222.65;
        attribute.setValue(222.65);
        Assert.assertTrue(wasContainerBeNotified);
        Assert.assertTrue(wasListenerBeNotified);

        attribute.removeListener(this);
        shouldListenerBeNotified = true;
        wasListenerBeNotified = false;
        expectedAttributeValue = 434.98;
        attribute.setValue(434.98);
        Assert.assertTrue(wasContainerBeNotified);
        Assert.assertFalse(wasListenerBeNotified);
    }

    @Test
    public void testInitialisationIfRegistered() throws Exception {
        CloudioAttribute<Double> attribute = createAttribute(Double.class, CloudioAttributeConstraint.Status);

        isRegistered = true;
        exception.expect(CloudioAttributeInitializationException.class);
        attribute.setInitialValue(222.65);
    }

    @Override
    public void attributeHasChanged(CloudioAttribute attribute) {
        wasListenerBeNotified = true;
        if (!shouldListenerBeNotified) {
            throw new RuntimeException();
        } else {
            if (attribute.internal.getType() != expectedAttributeType ||
                    attribute.internal.getConstraint() != expectedAttributeConstraint ||
                    !attribute.getValue().equals(expectedAttributeValue)) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public void attributeHasChangedByEndpoint(CloudioAttribute.InternalAttribute attribute) {
        wasContainerBeNotified = true;
        if (!shouldContainerBeNotified) {
            throw new RuntimeException();
        } else {
            if (attribute.getType() != expectedAttributeType ||
                    attribute.getConstraint() != expectedAttributeConstraint ||
                    !attribute.getValue().equals(expectedAttributeValue)) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public void attributeHasChangedByCloud(CloudioAttribute.InternalAttribute attribute) {
        wasContainerBeNotifiedCloud = true;
        if (!shouldContainerBeNotifiedCloud) {
            throw new RuntimeException();
        } else {
            if (attribute.getType() != expectedAttributeType ||
                    attribute.getConstraint() != expectedAttributeConstraint ||
                    !attribute.getValue().equals(expectedAttributeValue)) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public boolean isNodeRegisteredWithinEndpoint() {
        return isRegistered;
    }

    @Override
    public NamedItemSet<CloudioAttribute.InternalAttribute> getAttributes() {
        return null;
    }

    @Override
    public Uuid getUuid() {
        return new Uuid() {
            @Override
            public boolean equals(Uuid other) {
                return false;
            }

            @Override
            public String toString() {
                return "TEST";
            }

            @Override
            public boolean isValid() {
                return true;
            }
        };
    }

    @Override
    public String getName() {
        return "TEST";
    }

    @Override
    public void setName(String name) {}

    @Override
    public CloudioObjectContainer getParentObjectContainer() {
        return new CloudioObjectContainer() {
            @Override
            public void attributeHasChangedByEndpoint(CloudioAttribute.InternalAttribute attribute) {}

            @Override
            public void attributeHasChangedByCloud(CloudioAttribute.InternalAttribute attribute) {}

            @Override
            public boolean isNodeRegisteredWithinEndpoint() {
                return false;
            }

            @Override
            public NamedItemSet<CloudioObject.InternalObject> getObjects() {
                return new NamedItemSet<CloudioObject.InternalObject>();
            }

            @Override
            public CloudioObjectContainer getParentObjectContainer() {
                return null;
            }

            @Override
            public void setParentObjectContainer(CloudioObjectContainer objectContainer) {
                // Not relevant for tests.
            }

            @Override
            public CloudioNodeContainer getParentNodeContainer() {
                return new CloudioNodeContainer() {
                    @Override
                    public void attributeHasChangedByEndpoint(CloudioAttribute.InternalAttribute attribute) {}

                    @Override
                    public void attributeHasChangedByCloud(CloudioAttribute.InternalAttribute attribute) {}

                    @Override
                    public Uuid getUuid() {
                        return null;
                    }

                    @Override
                    public String getName() {
                        return "TEST";
                    }

                    @Override
                    public void setName(String name) {
                        // Not relevant for tests.
                    }
                };
            }

            @Override
            public void setParentNodeContainer(CloudioNodeContainer nodeContainer) {

            }

            @Override
            public Uuid getUuid() {
                return null;
            }

            @Override
            public String getName() {
                return "TEST";
            }

            @Override
            public void setName(String name) {

            }

            @Override
            public CloudioAttribute.InternalAttribute findAttribute(Stack<String> location) {
                return null;
            }
        };
    }

    @Override
    public void setParentObjectContainer(CloudioObjectContainer objectContainer) {

    }

    // Example...
    @Conforms(value = "BinarySwitch")
    class Switch extends CloudioObject {
        @SetPoint
        public CloudioAttribute<Boolean> state;

        @Status
        public CloudioAttribute<Boolean> stateFeedback;
    }

    class MySwitchSim extends Switch {
        public MySwitchSim() {
            state.addListener(new CloudioAttributeListener<Boolean>() {
                @Override
                public void attributeHasChanged(CloudioAttribute<Boolean> attribute) {
                    try {
                        stateFeedback.setValue(attribute.getValue());
                    } catch (CloudioAttributeConstraintException exception) {
                        exception.printStackTrace();
                    }
                }
            });
        }
    }
}
