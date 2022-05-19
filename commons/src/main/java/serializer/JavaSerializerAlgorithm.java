package serializer;

import lombok.extern.slf4j.Slf4j;
import serializer.SerializerAlgorithm;

import java.io.*;

/**
 * @author DearAhri520
 * <p>
 * Java自带的的序列化和反序列化算法
 */
@Slf4j
public class JavaSerializerAlgorithm implements SerializerAlgorithm {
    @Override
    public <T> byte[] serialize(T object) {
        /*序列化后的字节数组*/
        byte[] bytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            bytes = bos.toByteArray();
        } catch (IOException e) {
            log.error("Java serialize algorithm error", e);
        }
        return bytes;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        T target = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            target = (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Java deserialize algorithm error", e);
        }
        /*返回反序列化后的对象*/
        return target;
    }

    @Override
    public byte getIdentifier() {
        return 0;
    }

    @Override
    public String getName() {
        return "Java";
    }
}