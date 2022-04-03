package protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DearAhri520
 * <p>
 * SequenceId计数器
 */
public class SequenceIdGenerator {
    private static final AtomicInteger ID = new AtomicInteger();

    public static int nextInt() {
        return ID.getAndIncrement();
    }
}
