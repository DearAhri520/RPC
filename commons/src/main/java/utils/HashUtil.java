package utils;

/**
 * @author DearAhri520
 */
public class HashUtil {
    /**
     * 计算Hash值, 使用FNV1_32_HASH算法
     */
    public static int hash(Object key) {
        if (key == null) {
            return 0;
        } else {
            int h = key.hashCode();
            h ^= h >>> 16;
            h *= 0x85ebca6b;
            h ^= h >>> 13;
            h *= 0xc2b2ae35;
            h ^= h >>> 16;
            return h;
        }
    }
}

