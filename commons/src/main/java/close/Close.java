package close;

import lombok.SneakyThrows;

import java.io.Closeable;

/**
 * @author DearAhri520
 * 工具类,关闭实现了Closeable的类
 */
public class Close {
    private Close() {

    }

    @SneakyThrows
    public static void close(Closeable... closeable) {
        for (int i = 0; i < closeable.length; i++) {
            if(closeable[i]!=null){
                closeable[i].close();
            }
        }
    }
}
