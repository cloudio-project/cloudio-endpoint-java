package ch.hevs.cloudio.endpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Encodes messages using Compressed JSON (JavaScript Object Notation). All messages have to start with the identifier
 * for this format 0x7A ('z' character).
 */
class JsonZipMessageFormat extends JsonMessageFormat {
    private static final Logger log = LogManager.getLogger(JsonZipMessageFormat.class);

    @Override
    public byte[] serializeEndpoint(CloudioEndpoint.InternalEndpoint endpoint) {
        return compress(super.serializeEndpoint(endpoint));
    }

    @Override
    public byte[] serializeNode(CloudioNode.InternalNode node) {
        return compress(super.serializeNode(node));
    }

    @Override
    public byte[] serializeAttribute(CloudioAttribute.InternalAttribute attribute) {
        return compress(super.serializeAttribute(attribute));
    }

    @Override
    public void deserializeAttribute(byte[] data, CloudioAttribute.InternalAttribute attribute)
        throws CloudioAttributeConstraintException, NumberFormatException, IOException {
        try {
            super.deserializeAttribute(decompress(Arrays.copyOfRange(data, 1, data.length)), attribute);
        } catch (DataFormatException exception) {
            log.error("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }
    }


    @Override
    public void deserializeJobsParameter(byte[] data, JobsParameter jobsParameter)
            throws CloudioAttributeConstraintException, NumberFormatException, IOException {
        try {
            super.deserializeJobsParameter(decompress(Arrays.copyOfRange(data, 1, data.length)), jobsParameter);
        } catch (DataFormatException exception) {
            log.error("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    @Override
    public void deserializeLogParameter(byte[] data, LogParameter logParameter)
            throws CloudioAttributeConstraintException, NumberFormatException, IOException {
        try {
            super.deserializeLogParameter(decompress(Arrays.copyOfRange(data, 1, data.length)), logParameter);
        } catch (DataFormatException exception) {
            log.error("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    @Override
    public byte[] serializeCloudioLog(CloudioLogMessage cloudioLogMessage){
        return compress(super.serializeCloudioLog(cloudioLogMessage));
    }

    @Override
    public byte[] serializeJobsLineOutput(JobsLineOutput jobsLineOutput){
        return compress(super.serializeJobsLineOutput(jobsLineOutput));
    }

    private static byte[] compress(final byte[] uncompressed) {
        byte[] compressed = new byte[uncompressed.length];
        Deflater deflater = new Deflater(9);
        deflater.setInput(uncompressed);
        deflater.finish();
        int compressedSize = deflater.deflate(compressed);
        byte[] output = new byte[compressedSize + 1];
        output[0] = 'z';
        System.arraycopy(compressed, 0, output, 1, compressedSize);
        return output;
    }

    private static byte[] decompress(final byte[] compressed) throws DataFormatException {

        Inflater inflater = new Inflater();
        inflater.setInput(compressed, 1, compressed.length - 1);

        byte[] uncompressed = new byte[64];
        int uncompressedSize = 0;
        do {
            uncompressedSize += inflater.inflate(uncompressed, uncompressedSize,
                uncompressed.length - uncompressedSize);
            if (!inflater.finished()) {
                uncompressed = Arrays.copyOf(uncompressed, uncompressed.length * 2);
            }
        } while (!inflater.finished());
        inflater.end();
        return Arrays.copyOf(uncompressed, uncompressedSize);
    }
}
