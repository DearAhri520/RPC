package serializer;

import lombok.Getter;

/**
 * @author DearAhri520
 * <p>
 * 序列化算法类型枚举
 */
public enum SerializerType {
    /**
     * Java:java自带的序列化方式
     * Json:json格式的序列化方式
     * Proto:google的proto的序列化方式
     */
    Java((byte) 0),
    Json((byte) 1),
    Proto((byte) 2);

    @Getter
    private byte type;

    SerializerType(byte type) {
        this.type = type;
    }
}