package com.myapp.util.common;


import com.myapp.model.wemedia.pojo.WmNews;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.util.StopWatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffUtil {
    // 避免每次序列化都重新申请Buffer空间
    private static final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    // 缓存Schema
    private static final Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<Class<?>, Schema<?>>();

    /**
     * 序列化
     * @param t   序列化对象
     * @param <T> 序列化对象类型
     * @return 序列化对象字节数组
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T t) {
        Class<T> tClass = (Class<T>) t.getClass();
        Schema<T> schema = getSchema(tClass);
        byte[] data = null;
        try {
            data = ProtostuffIOUtil.toByteArray(t, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;

    }

    /**
     * 反序列化
     *
     * @param data 反序列化对象的字节数组
     * @param c    反序列化对象的字节码
     * @param <T>  反序列化对象的泛型
     * @return 反序列化对象
     */
    public static <T> T deserialize(byte[] data, Class<T> tClass) {
        Schema<T> schema = getSchema(tClass);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> tClass) {
        Schema<T> schema = (Schema<T>) schemaCache.get(tClass);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(tClass);
            if (schema != null) {
                schemaCache.put(tClass, schema);
            }
        }
        return schema;
    }

    /**
     * jdk序列化与protostuff序列化对比
     *
     * @param args
     */
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch("serialize");
        stopWatch.start("jdk");
        for (int i = 0; i < 1000000; i++) {
            WmNews wmNews = new WmNews();
            JdkSerializeUtil.serialize(wmNews);
        }
        stopWatch.stop();

        stopWatch.start("protostuff");
        for (int i = 0; i < 1000000; i++) {
            WmNews wmNews = new WmNews();
            ProtostuffUtil.serialize(wmNews);
        }
        stopWatch.stop();
        System.out.println(stopWatch);

    }


}
