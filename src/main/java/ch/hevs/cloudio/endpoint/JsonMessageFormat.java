package ch.hevs.cloudio.endpoint;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Encodes messages using JSON (JavaScript Object Notation). All messages have to start with the identifier for this
 * format 0x7B ('{' character).
 */
class JsonMessageFormat implements CloudioMessageFormat {
    private static final Logger log = LogManager.getLogger(JsonMessageFormat.class);
    private final JsonFactory factory = new JsonFactory();

    @Override
    public byte[] serializeEndpoint(CloudioEndpoint.InternalEndpoint endpoint) {
        ByteArrayBuilder outputStream = new ByteArrayBuilder();
        try {
            JsonGenerator generator = factory.createGenerator(outputStream, JsonEncoding.UTF8);

            generator.writeStartObject();

            List<CloudioNode.InternalNode> nodes = endpoint.getNodes();
            generator.writeStringField("version", endpoint.getVersion());
            generator.writeArrayFieldStart("supportedFormat");
            for (String format : endpoint.getSupportedFormats()) {
                generator.writeString(format);
            }
            generator.writeEndArray();


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
    public byte[] serializeDelayed(CloudioPersistence cloudioPersistence, String messageCategories[]) {

        ByteArrayBuilder outputStream = new ByteArrayBuilder();
        try {
            JsonGenerator generator = factory.createGenerator(outputStream, JsonEncoding.UTF8);

            generator.writeStartObject();
            generator.writeNumberField("timestamp", Calendar.getInstance().getTimeInMillis());
            generator.writeFieldName("messages");
            generator.writeStartArray();

            for(String messageCategory: messageCategories) {
                CloudioPersistence.Message message;

                for(int i = 0; i<cloudioPersistence.messageCount(messageCategory); i++)
                {
                    generator.writeStartObject();

                    message = cloudioPersistence.getMessage(messageCategory,i);

                    // Get the pending update persistent object from store.
                    byte[] data = message.data;
                    String topic = message.topic;

                    generator.writeStringField("topic", topic);

                    generator.writeFieldName("data");

                    generator.writeStartObject();
                    generator.flush();
                    //add the data messages from saved bytes, remove the 1st and last char which are "{" and "}"
                    outputStream.write(data,1,data.length-2);

                    generator.writeEndObject();
                    generator.writeEndObject();
                    generator.flush();
                }
            }

            generator.writeEndArray();
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

    @Override
    public byte[] serializeDidSetAttribute(CloudioAttribute.InternalAttribute attribute, String correlationID) {
        ByteArrayBuilder outputStream = new ByteArrayBuilder();
        try {
            JsonGenerator generator = factory.createGenerator(outputStream, JsonEncoding.UTF8);
            serializeAttribute(attribute, generator);

            generator.writeStartObject();

            generator.writeStringField("correlationID", correlationID);
            generator.writeNumberField("timestamp", Calendar.getInstance().getTimeInMillis());

            java.lang.Object value = attribute.getValue();
            if (value != null) {
                generator.writeObjectField("value", attribute.getValue());
            }

            generator.writeEndObject();

            generator.flush();
        } catch (IOException exception) {
            log.error("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte[] serializeTransaction(Transaction transaction){
        ByteArrayBuilder outputStream = new ByteArrayBuilder();
        try {
            JsonGenerator generator = factory.createGenerator(outputStream, JsonEncoding.UTF8);
            serializeTransaction(transaction, generator);
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

    @Override
    public String deserializeSetAttribute(byte[] data, CloudioAttribute.InternalAttribute attribute)
            throws CloudioAttributeConstraintException, NumberFormatException, IOException {
        String correlationID = "";

        this.deserializeAttribute(data, attribute);

        JsonParser parser = new JsonFactory().createParser(data);
        if (parser.nextToken() == JsonToken.START_OBJECT) {

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                if ("correlationID".equals(fieldName)) {
                    correlationID = parser.getText();
                }
            }
        }
        return correlationID;
    }

    @Override
    public void deserializeJobsParameter(byte[] data, JobsParameter jobsParameter)
            throws CloudioAttributeConstraintException, NumberFormatException, IOException {
        JsonParser parser = new JsonFactory().createParser(data);
        if (parser.nextToken() == JsonToken.START_OBJECT) {
            String jobURI;
            String correlationID;
            String dataAttribute;
            Boolean sendOutput;

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();
                if ("jobURI".equals(fieldName)) {
                    jobURI = parser.getText();
                    jobsParameter.setJobURI(jobURI);
                } else if ("sendOutput".equals(fieldName)) {
                    sendOutput = parser.getBooleanValue();
                    jobsParameter.setSendOutput(sendOutput);
                } else if ("correlationID".equals(fieldName)) {
                    correlationID = parser.getText();
                    jobsParameter.setCorrelationID(correlationID);
                }else if ("data".equals(fieldName)) {
                    dataAttribute = parser.getText();
                    jobsParameter.setData(dataAttribute);
                }
            }
        }
    }

    @Override
    public void deserializeLogParameter(byte[] data, LogParameter logParameter)
            throws CloudioAttributeConstraintException, NumberFormatException, IOException {
        JsonParser parser = new JsonFactory().createParser(data);
        if (parser.nextToken() == JsonToken.START_OBJECT) {
            String level;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();
                if ("level".equals(fieldName)) {
                    level = parser.getText();
                    logParameter.setLevel(level);
                }
            }
        }
    }

    @Override
    public byte[] serializeCloudioLog(CloudioLogMessage cloudioLogMessage) {
        ByteArrayBuilder outputStream = new ByteArrayBuilder();
        try {
            JsonGenerator generator = factory.createGenerator(outputStream, JsonEncoding.UTF8);
            serializeCloudioLog(cloudioLogMessage, generator);
            generator.flush();
        } catch (IOException exception) {
            log.error("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte[] serializeJobsLineOutput(JobsLineOutput jobsLineOutput) {
        ByteArrayBuilder outputStream = new ByteArrayBuilder();
        try {
            JsonGenerator generator = factory.createGenerator(outputStream, JsonEncoding.UTF8);
            serializeJobsLineOutput(jobsLineOutput, generator);
            generator.flush();
        } catch (IOException exception) {
            log.error("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    private void serializeNode(CloudioNode.InternalNode node, JsonGenerator generator) throws IOException {
        generator.writeStartObject();

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

    private void serializeTransaction(Transaction transaction, JsonGenerator generator) throws IOException {
        generator.writeStartObject();

        List<CloudioAttribute.InternalAttribute> attributes = transaction.getAttributes();
        generator.writeObjectFieldStart("attributes");
        for (CloudioAttribute.InternalAttribute attribute: attributes) {
            generator.writeFieldName(attribute.getUuid().toString());
            serializeAttribute(attribute, generator);
        }
        generator.writeEndObject();

        generator.writeEndObject();
    }


    private void serializeCloudioLog(CloudioLogMessage cloudioLogMessage, JsonGenerator generator) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("level", cloudioLogMessage.getLevel().toString());
        generator.writeNumberField("timestamp", cloudioLogMessage.getTimestamp() / 1000.0);
        generator.writeStringField("message", cloudioLogMessage.getMessage());
        generator.writeStringField("loggerName", cloudioLogMessage.getLoggerName());
        generator.writeStringField("logSource", cloudioLogMessage.getLogSource());

        generator.writeEndObject();
    }

    private void serializeJobsLineOutput(JobsLineOutput jobsLineOutput, JsonGenerator generator) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("correlationID", jobsLineOutput.getCorrelationID());
        generator.writeStringField("data", jobsLineOutput.getData());

        generator.writeEndObject();
    }
}
