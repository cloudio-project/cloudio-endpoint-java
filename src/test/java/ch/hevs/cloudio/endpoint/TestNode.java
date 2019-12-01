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

        obj1.arrayTest.booleanStatus.setValue(new Boolean[] {false, true, false}, 12345678L);
        obj1.arrayTest.booleanParameter.setInitialValue(new Boolean[] {true, true, true, false});
        obj1.arrayTest.booleanSetPoint.setInitialValue(new Boolean[] {false, false, true, false});
        obj1.arrayTest.booleanMeasure.setValue(new Boolean[] {true, false, true}, 12345678L);

        obj1.arrayTest.longStatus.setValue(new Long[] {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L}, 12345678L);
        obj1.arrayTest.longParameter.setInitialValue(new Long[] {1L, 3L, 5L, 7L, 9L});
        obj1.arrayTest.longSetPoint.setInitialValue(new Long[] {99L, 98L, 96L, 95L, 94L, 93L, 92L, 91L});
        obj1.arrayTest.longMeasure.setValue(new Long[] {42L, 42L, 42L}, 12345678L);

        obj1.arrayTest.doubleStatus.setValue(new Double[] {1.2, 3.4, 5.6, 7.8, 9.0}, 123456678L);
        obj1.arrayTest.doubleParameter.setInitialValue(new Double[] {2.1, 4.3, 6.5, 8.7, 0.9});
        obj1.arrayTest.doubleSetPoint.setInitialValue(new Double[] {0.1, 0.2, 0.3, 0.4, 0.5});
        obj1.arrayTest.doubleMeasure.setValue(new Double[] {0.6, 0.7, 0.8, 0.9, 1.0}, 123456678L);

        obj1.arrayTest.stringStatus.setValue("amet, consetetur sadipscing elitr".split("\\s"), 12345678L);
        obj1.arrayTest.stringParameter.setInitialValue("ed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat".split("\\s"));
        obj1.arrayTest.stringSetPoint.setInitialValue("ed diam voluptua".split("\\s"));
        obj1.arrayTest.stringMeasure.setValue("At vero eos et accusam et justo duo dolores et ea rebum".split("\\s"), 12345678L);
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

    public ArrayTestObject arrayTest;
}

@Conforms("ArrayTestObject")
class ArrayTestObject extends CloudioObject {
    @StaticAttribute
    public boolean[] staticBoolean = {true, false, true};

    @Parameter
    public CloudioAttribute<Boolean[]> booleanParameter;

    @Status
    public CloudioAttribute<Boolean[]> booleanStatus;

    @SetPoint
    public CloudioAttribute<Boolean[]> booleanSetPoint;

    @Measure
    public CloudioAttribute<Boolean[]> booleanMeasure;

    @StaticAttribute
    public long[] staticInteger = {42, 55, 88, 123};

    @Parameter
    public CloudioAttribute<Long[]> longParameter;

    @Status
    public CloudioAttribute<Long[]> longStatus;

    @SetPoint
    public CloudioAttribute<Long[]> longSetPoint;

    @Measure
    public CloudioAttribute<Long[]> longMeasure;

    @StaticAttribute
    public double[] staticDouble = {42.0, 88.0, 12345.0, 0.321};

    @Parameter
    public CloudioAttribute<Double[]> doubleParameter;

    @Status
    public CloudioAttribute<Double[]> doubleStatus;

    @SetPoint
    public CloudioAttribute<Double[]> doubleSetPoint;

    @Measure
    public CloudioAttribute<Double[]> doubleMeasure;

    @StaticAttribute
    public String[] staticString = {"STATIC", "STRING", "TEST"};

    @Parameter
    public CloudioAttribute<String[]> stringParameter;

    @Status
    public CloudioAttribute<String[]> stringStatus;

    @SetPoint
    public CloudioAttribute<String[]> stringSetPoint;

    @Measure
    public CloudioAttribute<String[]> stringMeasure;
}
