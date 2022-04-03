package serializer;

import com.google.gson.*;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author DearAhri520
 * @date 2022/3/26
 */
public enum SerializerAlgorithm implements Serializer {
    /*Java自带的的序列化和反序列化*/
    Java {
        @Override
        public <T> byte[] serialize(T object) {
            /*序列化后的字节数组*/
            byte[] bytes = null;
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(object);
                bytes = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
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
                e.printStackTrace();
            }
            /*返回反序列化后的对象*/
            return target;
        }
    },

    /*基于gson的Json序列化和反序列化*/
    Json {
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
    },

    /*基于protostuff的序列化和反序列化*/
    Protostuff {
        private final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        @Override
        @SuppressWarnings("unchecked")
        public <T> byte[] serialize(T obj) {
            Class<T> clazz = (Class<T>) obj.getClass();
            Schema<T> schema = RuntimeSchema.getSchema(clazz);
            byte[] data;
            try {
                data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
            } finally {
                buffer.clear();
            }
            return data;
        }

        @Override
        public <T> T deserialize(Class<T> clazz, byte[] bytes) {
            Schema<T> schema = RuntimeSchema.getSchema(clazz);
            T obj = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
            return obj;
        }
    }
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

