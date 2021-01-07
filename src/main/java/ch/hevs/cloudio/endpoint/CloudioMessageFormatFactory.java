package ch.hevs.cloudio.endpoint;

class CloudioMessageFormatFactory {
    static CloudioMessageFormat json;
    static CloudioMessageFormat cbor;

    static CloudioMessageFormat messageFormat(int messageFormatId) {
        if (messageFormatId == '{') {
            if (json == null) json = new GenericJacksonMessageFormat.JSON();
            return json;
        } else if ((messageFormatId & 0b11100000) == 0b10100000) {
            if (cbor == null) cbor = new GenericJacksonMessageFormat.CBOR();
            return cbor;
        } else {
            return null;
        }
    }

    static CloudioMessageFormat messageFormat(String messageFormatName) {
        switch (messageFormatName) {
            case "JSON":
                if (json == null) json = new GenericJacksonMessageFormat.JSON();
                return json;

            case "CBOR":
                if (cbor == null) cbor = new GenericJacksonMessageFormat.CBOR();
                return cbor;

            default:
                return null;
        }
    }
}
