package ch.hevs.cloudio.endpoint;

import org.junit.Test;

public class CustomTests {
    @Test
    public void customManualTest() throws DuplicateNamedItemException, CloudioAttributeInitializationException,
        CloudioAttributeConstraintException {
        CloudioAdLibNode node = new CloudioAdLibNode();
        node.declareImplementedInterface("TestInterface");

        CloudioAdLibObject object = new CloudioAdLibObject();
        object.addAttribute("testAttribute", Boolean.class, CloudioAttributeConstraint.Measure);
        object.addAttribute("staticAttribute", Integer.class, CloudioAttributeConstraint.Static, 55);

        node.addObject("testObject", object);
    }

    @Test
    public void customBuilderTest() throws DuplicateNamedItemException {
        new CloudioAdLibNode.Builder().implement("TestInterface").object("testObject",
            new CloudioAdLibObject.Builder().attribute("testAttribute", Boolean.class, CloudioAttributeConstraint.Measure).build())
            .build();
    }
}
