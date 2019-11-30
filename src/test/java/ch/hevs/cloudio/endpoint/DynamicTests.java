package ch.hevs.cloudio.endpoint;

import org.junit.Test;

public class DynamicTests {
    @Test
    public void customManualTest() throws DuplicateItemException, CloudioAttributeInitializationException,
        CloudioAttributeConstraintException {
        CloudioDynamicNode node = new CloudioDynamicNode();
        node.declareImplementedInterface("TestInterface");

        CloudioDynamicObject object = new CloudioDynamicObject();
        object.addAttribute("testAttribute", Boolean.class, CloudioAttributeConstraint.Measure);
        object.addAttribute("staticAttribute", Integer.class, CloudioAttributeConstraint.Static, 55);

        node.addObject("testObject", object);
    }

    @Test
    public void customBuilderTest() throws DuplicateItemException {
        new CloudioDynamicNode.Builder().implement("TestInterface").object("testObject",
            new CloudioDynamicObject.Builder().attribute("testAttribute", Boolean.class, CloudioAttributeConstraint.Measure).build())
            .build();
    }
}
