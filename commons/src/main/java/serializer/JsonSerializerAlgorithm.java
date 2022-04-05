package serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;

/**
 * @author DearAhri520
 * <p>
 * * 基于gson的Json序列化和反序列化
 */
class JsonSerializerAlgorithm implements SerializerAlgorithm {
    @Override
    public <T> byte[] serialize(T object) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodeC()).create();
        String s = gson.toJson(object);
        /*指定字符集，获得字节数组*/
        return s.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodeC()).create();
        String json = new String(bytes, StandardCharsets.UTF_8);
        /*此处的clazz为具体类型的Class对象，而不是父类Message的类型*/
        return gson.fromJson(json, clazz);
    }

    @Override
    public byte getIdentifier() {
        return 1;
    }

    @Override
    public String getName() {
        return "JSON";
    }
}