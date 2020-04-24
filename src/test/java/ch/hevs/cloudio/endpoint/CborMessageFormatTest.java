package ch.hevs.cloudio.endpoint;

import ch.hevs.utils.ResourceLoader;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class CborMessageFormatTest {
    private final CloudioMessageFormat cbor = new GenericJacksonMessageFormat.CBOR();

    @Test
    public void serializeTestNode() throws CloudioAttributeInitializationException, CloudioAttributeConstraintException, IOException, URISyntaxException {
        byte[] serialized = cbor.serializeNode(new TestNode().internal);
        Assert.assertArrayEquals(ResourceLoader.getContent("classpath:TestNode.cbor", this), serialized);
    }
}
