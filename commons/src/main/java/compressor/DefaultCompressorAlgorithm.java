package compressor;

import java.io.IOException;

/**
 * @author DearAhri520
 */
public class DefaultCompressorAlgorithm implements CompressionAlgorithm {
    @Override
    public byte[] compress(byte[] bytes) throws IOException {
        return bytes;
    }

    @Override
    public byte[] unCompress(byte[] bytes) throws IOException {
        return bytes;
    }

    @Override
    public byte getIdentifier() {
        return 0;
    }

    @Override
    public String getName() {
        return "Default";
    }
}
