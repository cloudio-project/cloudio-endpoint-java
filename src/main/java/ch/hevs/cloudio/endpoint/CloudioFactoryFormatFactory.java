package ch.hevs.cloudio.endpoint;

public class CloudioFactoryFormatFactory {
    static CloudioFactoryFormat json;
    static CloudioFactoryFormat cbor;


    static CloudioFactoryFormat factoryFormat(int messageFormatId) {
        if (messageFormatId == '{') {
            if (json == null) json = new GenericJacksonFactoryFormat.JSON();
            return json;
        } else if ((messageFormatId & 0b11100000) == 0b10100000) {
            if (cbor == null) cbor = new GenericJacksonFactoryFormat.CBOR();
            return cbor;
        } else {
            return null;
        }
    }

    static CloudioFactoryFormat factoryFormat(String messageFormatName) {
        switch (messageFormatName) {
            case "JSON":
                if (json == null) json = new GenericJacksonFactoryFormat.JSON();
                return json;

            case "CBOR":
                if (cbor == null) cbor = new GenericJacksonFactoryFormat.CBOR();
                return cbor;

            default:
                return null;
        }
    }
}
