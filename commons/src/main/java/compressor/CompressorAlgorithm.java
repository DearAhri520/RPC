package compressor;

import close.Close;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author DearAhri520
 * @date 2022/3/31
 */
public interface CompressorAlgorithm extends Compressor {
    /**
     * 获取压缩算法标识符
     *
     * @return 压缩算法标识符
     */
    byte getIdentifier();

    /**
     * 获取压缩算法名称
     *
     * @return 压缩算法名称
     */
    String getName();
}
