package ch.hevs.cloudio.endpoint;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Encodes messages using JSON (JavaScript Object Notation). All messages have to start with the identifier for this
 * format 0x7B ('{' character).
 */
class JsonMessageFormat implements CloudioMessageFormat {
    private static final Logger log = LoggerFactory.getLogger(JsonMessageFormat.class);
    private final JsonFactory factory = new JsonFactory();

    @Override
    public byte[] serializeEndpoint(CloudioEndpoint.InternalEndpoint endpoint) {
        ByteArrayBuilder outputStream = new ByteArrayBuilder();
        try {
            JsonGenerator generator = factory.createGenerator(outputStream, JsonEncoding.UTF8);

            generator.writeStartObject();

            List<CloudioNode.InternalNode> nodes = endpoint.getNodes();
            generator.writeObjectFieldStart("nodes");
            for (CloudioNode.InternalNode node: nodes) {
                generator.writeFieldName(node.getName());
                serializeNode(node, generator);
            }
            generator.writeEndObject();

            generator.writeEndObject();

            generator.flush();
        } catch (IOException exception) {
            log.error("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte[] serializeNode(CloudioNode.InternalNode node) {
        ByteArrayBuilder outputStream = new ByteArrayBuilder();
        try {
            JsonGenerator generator = factory.createGenerator(outputStream, JsonEncoding.UTF8);
            serializeNode(node, generator);
            generator.flush();
        } catch (IOException exception) {
            log.error("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte[] serializeAttribute(CloudioAttribute.InternalAttribute attribute) {
        ByteArrayBuilder outputStream = new ByteArrayBuilder();
        try {
            JsonGenerator generator = factory.createGenerator(outputStream, JsonEncoding.UTF8);
            serializeAttribute(attribute, generator);
            generator.flush();
        } catch (IOException exception) {
            log.error("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserializeAttribute(byte[] data, CloudioAttribute.InternalAttribute attribute)
        throws CloudioAttributeConstraintException, NumberFormatException, IOException {

        JsonParser parser = new JsonFactory().createParser(data);
        if (parser.nextToken() == JsonToken.START_OBJECT) {
            long timestamp = 0;
            String value = null;

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();
                if ("timestamp".equals(fieldName)) {
                    timestamp = (long)(parser.getDoubleValue() * 1000);
                } else if ("value".equals(fieldName)) {
                    value = parser.getText();
                }
            }

            if (timestamp != 0 && value != null) {
                switch (attribute.getType()) {
                    case Invalid:
                        break;
                    case Boolean:
                        attribute.setValueFromCloud(Boolean.parseBoolean(value), timestamp);
                        break;

                    case Integer:
                        attribute.setValueFromCloud(Long.decode(value), timestamp);
                        break;

                    case Number:
                        attribute.setValueFromCloud(Double.parseDouble(value), timestamp);
                        break;

                    case String:
                        attribute.setValueFromCloud(value, timestamp);
                        break;

                    default:
                        throw new IOException("Attribute type not supported!");
                }
            }
        }
    }

    private void serializeNode(CloudioNode.InternalNode node, JsonGenerator generator) throws IOException {
        generator.writeStartObject();

        Set<String> interfaces = node.getInterfaces();
        generator.writeArrayFieldStart("implements");
        for (String interface_ : node.getInterfaces()) {
            generator.writeString(interface_);
        }
        generator.writeEndArray();

        List<CloudioObject.InternalObject> objects = node.getObjects().toList();
        generator.writeObjectFieldStart("objects");
        for (CloudioObject.InternalObject object: objects) {
            serializeObject(object, generator);
        }
        generator.writeEndObject();

        generator.writeEndObject();
    }

    private void serializeObject(CloudioObject.InternalObject object, JsonGenerator generator) throws IOException {
        generator.writeObjectFieldStart(object.getName());

        String conforms = object.getConforms();
        if (conforms != null) {
            generator.writeStringField("conforms", conforms);
        }

        List<CloudioObject.InternalObject> objects = object.getObjects().toList();
        generator.writeObjectFieldStart("objects");
        for (CloudioObject.InternalObject childObject: objects) {
            serializeObject(childObject, generator);
        }
        generator.writeEndObject();

        List<CloudioAttribute.InternalAttribute> attributes = object.getAttributes().toList();
        generator.writeObjectFieldStart("attributes");
        for (CloudioAttribute.InternalAttribute attribute: attributes) {
            generator.writeFieldName(attribute.getName());
            serializeAttribute(attribute, generator);
        }
        generator.writeEndObject();

        generator.writeEndObject();
    }

    private void serializeAttribute(CloudioAttribute.InternalAttribute attribute, JsonGenerator generator) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("type", attribute.getType().toString());

        generator.writeStringField("constraint", attribute.getConstraint().toString());

        if (attribute.getConstraint() != CloudioAttributeConstraint.Static) {
            Long timestamp = attribute.getTimestamp();
            if (timestamp != null) {
                generator.writeNumberField("timestamp", attribute.getTimestamp() / 1000.0);
            }
        }

        java.lang.Object value = attribute.getValue();
        if (value != null) {
            generator.writeObjectField("value", attribute.getValue());
        }

        generator.writeEndObject();
    }
}
