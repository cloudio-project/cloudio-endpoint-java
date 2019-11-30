package ch.hevs.cloudio.endpoint;

class CloudioMessageFormatFactory {
    static CloudioMessageFormat json;

    static CloudioMessageFormat massageFormat(int messageFormatId) {
        switch (messageFormatId) {
            case '{':
                if (json == null) json = new JsonMessageFormat();
                return json;

            default:
                return null;
        }
    }
}
