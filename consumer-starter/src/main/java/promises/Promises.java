package promises;

import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DearAhri520
 */
public class Promises {
    /**
     * 消息序号->promise对象
     */
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();
}
