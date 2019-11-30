package ch.hevs.cloudio.endpoint;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

class CloudioMessageFormatFactory {
    static CloudioMessageFormat json;
    static CloudioMessageFormat cbor;

    static CloudioMessageFormat massageFormat(int messageFormatId) {
        switch (messageFormatId) {
            case '{':
                if (json == null) json = new JacksonMessageFormat(new JsonFactory());
                return json;

            case 'c':
                if (cbor == null) cbor = new JacksonMessageFormat(new CBORFactory());
                return cbor;

            default:
                return null;
        }
    }

    static CloudioMessageFormat messageFormat(String messageFormatName) {
        switch (messageFormatName) {
            case "json":
                if (json == null) json = new JacksonMessageFormat(new JsonFactory());
                return json;

            case "cbor":
                if (cbor == null) cbor = new JacksonMessageFormat(new CBORFactory());
                return cbor;

            default:
                return null;
        }
    }
}
