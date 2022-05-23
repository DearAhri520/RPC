package compressor;

import java.io.IOException;

/**
 * @author DearAhri520
 * <p>
 * 压缩算法,该类可被用户自定义拓展
 */
public interface Compressor {
    /**
     * 压缩字节数组,并返回压缩之后的字节数组
     *
     * @param bytes 待压缩的字节数组
     * @return 压缩后的字节数组
     * @throws IOException 抛出的异常
     */
    byte[] compress(byte[] bytes) throws IOException;

    /**
     * 解压字节数组,并返回解压之后的字节数组
     *
     * @param bytes 待解压的字节数组
     * @return 解压后的字节数组
     * @throws IOException 抛出的异常
     */
    byte[] unCompress(byte[] bytes) throws IOException;
}
