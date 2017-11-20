package ch.hevs.cloudio.endpoint;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class StaticAttributesTest {

    class TestObject extends CloudioObject {
        @StaticAttribute
        public boolean booleanAttribute = true;

        @StaticAttribute
        public short shortAttribute = 1;

        @StaticAttribute
        public int intAttribute = 2;

        @StaticAttribute
        public long longAttribute = 3;

        @StaticAttribute
        public float floatAttribute = 4;

        @StaticAttribute
        public double doubleAttribute = 5;

        @StaticAttribute
        public String stringAttribute = "test";
    }

    @Test
    public void test() {
        List<CloudioAttribute.InternalAttribute> attributes = (new TestObject()).internal.getAttributes().toList();

        Assert.assertEquals(attributes.get(0).getName(), "booleanAttribute");
        Assert.assertSame(attributes.get(0).getConstraint(), CloudioAttributeConstraint.Static);
        Assert.assertEquals(attributes.get(0).getValue(), true);

        Assert.assertEquals(attributes.get(1).getName(), "shortAttribute");
        Assert.assertSame(attributes.get(1).getConstraint(), CloudioAttributeConstraint.Static);
        Assert.assertEquals(attributes.get(1).getValue(), 1L);

        Assert.assertEquals(attributes.get(3).getName(), "longAttribute");
        Assert.assertSame(attributes.get(3).getConstraint(), CloudioAttributeConstraint.Static);
        Assert.assertEquals(attributes.get(3).getValue(), 3L);

        Assert.assertEquals(attributes.get(4).getName(), "floatAttribute");
        Assert.assertSame(attributes.get(4).getConstraint(), CloudioAttributeConstraint.Static);
        Assert.assertEquals(attributes.get(4).getValue(), 4.0);

        Assert.assertEquals(attributes.get(5).getName(), "doubleAttribute");
        Assert.assertSame(attributes.get(5).getConstraint(), CloudioAttributeConstraint.Static);
        Assert.assertEquals(attributes.get(5).getValue(), 5.0);

        Assert.assertEquals(attributes.get(6).getName(), "stringAttribute");
        Assert.assertSame(attributes.get(6).getConstraint(), CloudioAttributeConstraint.Static);
        Assert.assertEquals(attributes.get(6).getValue(), "test");
    }
}
