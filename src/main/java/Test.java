import RPC.Close.Close;
import RPC.serializer.Serializer;
import RPC.serializer.SerializerAlgorithm;
import io.netty.handler.codec.base64.Base64Decoder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import java.util.Base64;

/**
 * @author DearAhri520
 * @date 2022/3/30
 */
public class Test {
    public static void main(String[] args) {
        String tmp = compress("1234567890abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz");
        System.out.println(tmp);
        System.out.println(uncompress(tmp));
    }

    public static String compress(String primStr) {
        if (primStr == null || primStr.length() == 0) {
            return primStr;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(primStr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Close.close(out);
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    /**
     * 使用gzip进行解压缩
     */
    public static String uncompress(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed;
        String decompressed = null;
        try {
            compressed = Base64.getDecoder().decode(compressedStr);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Close.close(ginzip, in, out);
        }
        return decompressed;
    }
}