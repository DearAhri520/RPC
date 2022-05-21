package compressor;

import lombok.Getter;

/**
 * @author DearAhri520
 */
public enum CompressorType {
    /**
     * Default:不做任何压缩处理
     * Gzip:采用Gzip压缩算法
     */
    Default((byte) 0),
    Gzip((byte) 1);

    @Getter
    private byte type;

    CompressorType(byte type) {
        this.type = type;
    }
}
