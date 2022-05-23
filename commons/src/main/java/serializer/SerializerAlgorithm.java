package serializer;

/**
 * @author DearAhri520
 * <p>
 * 用户可拓展该序列化算法
 */
public interface SerializerAlgorithm extends Serializer {
    /**
     * 获取序列化算法标识符
     *
     * @return 序列化算法标识符
     */
    byte getIdentifier();

    /**
     * 获取序列化算法名称
     *
     * @return 序列化算法名称
     */
    String getName();
}
