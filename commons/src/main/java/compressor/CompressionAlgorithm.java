package compressor;

/**
 * @author DearAhri520
 */
public interface CompressionAlgorithm extends Compressor {
    /**
     * 获取压缩算法唯一标识符
     *
     * @return 压缩算法唯一标识符
     */
    byte getIdentifier();

    /**
     * 获取压缩算法名称
     *
     * @return 压缩算法名称
     */
    String getName();

    /**
     * 获取消息最小压缩长度,默认为30
     *
     * @return 消息最小压缩长度
     */
    default int getMinCompressLength() {
        return 30;
    }
}
