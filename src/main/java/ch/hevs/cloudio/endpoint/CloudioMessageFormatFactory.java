package ch.hevs.cloudio.endpoint;

class CloudioMessageFormatFactory {
    static CloudioMessageFormat json;
    static CloudioMessageFormat cbor;

    static CloudioMessageFormat massageFormat(int messageFormatId) {
        switch (messageFormatId) {
            case '{':
                if (json == null) json = new GenericJacksonMessageFormat.JSON();
                return json;

            case 'c':
                if (cbor == null) cbor = new GenericJacksonMessageFormat.CBOR();
                return cbor;

            default:
                return null;
        }
    }

    static CloudioMessageFormat messageFormat(String messageFormatName) {
        switch (messageFormatName) {
            case "json":
                if (json == null) json = new GenericJacksonMessageFormat.JSON();
                return json;

            case "cbor":
                if (cbor == null) cbor = new GenericJacksonMessageFormat.CBOR();
                return cbor;

            default:
                return null;
        }
    }
}
