import RPC.close.Close;
import RPC.compressor.Compressor;
import RPC.compressor.CompressorAlgorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import java.util.Base64;

/**
 * @author DearAhri520
 * @date 2022/3/30
 */
public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println(CompressorAlgorithm.valueOf("Default").name());
    }
}