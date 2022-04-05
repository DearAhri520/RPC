package serializer;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author DearAhri520
 * <p>
 * * 基于gson的Json序列化和反序列化
 */
public class JsonSerializerAlgorithm implements SerializerAlgorithm {
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

    /**
     * 用于GSON的编解码器
     */
    class ClassCodeC implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String str = jsonElement.getAsString();
            try {
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> aClass, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(aClass.getName());
        }
    }
}