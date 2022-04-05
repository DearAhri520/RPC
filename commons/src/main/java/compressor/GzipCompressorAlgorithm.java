package compressor;

import java.io.IOException;

import close.Close;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author DearAhri520
 * @date 2022/4/5
 */
public class GzipCompressorAlgorithm implements CompressorAlgorithm {

    @Override
    public byte[] compress(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(bytes);
        } finally {
            Close.close(out);
        }
        return out.toByteArray();
    }


    /**
     * @param bytes 待解压的字节数组
     * @return 通过gzip算法解压后的字节数组
     * @throws IOException 抛出的IO异常
     */
    @Override
    public byte[] unCompress(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(in)) {
            byte[] buffer = new byte[256];
            int offset;
            while ((offset = gzipInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
        } finally {
            Close.close(out, in);
        }
        return out.toByteArray();
    }

    @Override
    public byte getIdentifier() {
        return 1;
    }

    @Override
    public String getName() {
        return "Gzip";
    }
}
