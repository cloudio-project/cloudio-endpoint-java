package ch.hevs.cloudio.endpoint;

@Implements({"InterfaceA", "InterfaceB"})
public class TestNode extends CloudioNode {
    public TestNode() throws CloudioAttributeConstraintException, CloudioAttributeInitializationException {
        obj1.booleanStatus.setValue(true, 12345678L);
        obj1.booleanParameter.setInitialValue(false);
        obj1.booleanSetPoint.setInitialValue(true);
        obj1.booleanMeasure.setValue(false, 12345678L);

        obj1.longStatus.setValue(1234L, 12345678L);
        obj1.longParameter.setInitialValue(4321L);
        obj1.longSetPoint.setInitialValue(1024L);
        obj1.longMeasure.setValue(424242L, 12345678L);

        obj1.doubleStatus.setValue(1.2345, 12345678L);
        obj1.doubleParameter.setInitialValue(5.4321);
        obj1.doubleSetPoint.setInitialValue(6.789);
        obj1.doubleMeasure.setValue(9.8765, 12345678L);

        obj1.stringStatus.setValue("Lorem", 12345678L);
        obj1.stringParameter.setInitialValue("ipsum");
        obj1.stringSetPoint.setInitialValue("dolor");
        obj1.stringMeasure.setValue("sit", 12345678L);
    }

    public TestObject obj1;
}

@Conforms("TestObject")
class TestObject extends CloudioObject {
    @StaticAttribute
    public boolean staticBoolean = true;

    @Parameter
    public CloudioAttribute<Boolean> booleanParameter;

    @Status
    public CloudioAttribute<Boolean> booleanStatus;

    @SetPoint
    public CloudioAttribute<Boolean> booleanSetPoint;

    @Measure
    public CloudioAttribute<Boolean> booleanMeasure;

    @StaticAttribute
    public long staticInteger = 42;

    @Parameter
    public CloudioAttribute<Long> longParameter;

    @Status
    public CloudioAttribute<Long> longStatus;

    @SetPoint
    public CloudioAttribute<Long> longSetPoint;

    @Measure
    public CloudioAttribute<Long> longMeasure;

    @StaticAttribute
    public double staticDouble = 42.0;

    @Parameter
    public CloudioAttribute<Double> doubleParameter;

    @Status
    public CloudioAttribute<Double> doubleStatus;

    @SetPoint
    public CloudioAttribute<Double> doubleSetPoint;

    @Measure
    public CloudioAttribute<Double> doubleMeasure;

    @StaticAttribute
    public String staticString = "STATIC";

    @Parameter
    public CloudioAttribute<String> stringParameter;

    @Status
    public CloudioAttribute<String> stringStatus;

    @SetPoint
    public CloudioAttribute<String> stringSetPoint;

    @Measure
    public CloudioAttribute<String> stringMeasure;
}