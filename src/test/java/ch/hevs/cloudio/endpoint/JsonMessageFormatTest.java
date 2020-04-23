package ch.hevs.cloudio.endpoint;

import ch.hevs.utils.ResourceLoader;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class JsonMessageFormatTest {
    private CloudioMessageFormat cbor = new GenericJacksonMessageFormat.JSON();

    @Test
    public void serializeTestNode() throws Exception {
        byte[] serialized = cbor.serializeNode(new TestNode().internal);
        JSONAssert.assertEquals(
            new String(ResourceLoader.getContent("classpath:TestNode.json", this)),
            new String(serialized), true);
    }
}
