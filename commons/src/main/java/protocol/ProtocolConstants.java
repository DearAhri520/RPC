package protocol;

/**
 * @author DearAhri520
 */
public class ProtocolConstants {
    /**
     * 魔数:387383298,转换成byte数组即为{23,23,0,2}
     */
    public static final int MAGIC_NUMBER = 387383298;
    public static final byte[] MAGIC_NUMBER_BYTES = new byte[]{23, 23, 0, 2};
    /**
     * 协议版本
     */
    public static final byte VERSION = 0;
}
