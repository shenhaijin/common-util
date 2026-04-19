package wooho.common.util.api.response.encrypt;

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * 简单的 JSON 序列化工具（零依赖）。
 * <p>
 * 支持常见类型：基本类型、String、Date、Collection、Map、数组。
 * 可按需扩展以支持更多类型。
 */
public class SimpleJsonSerializer {

    private SimpleJsonSerializer() {
    }

    /**
     * 将对象序列化为 JSON 字符串
     */
    public static String serialize(Object obj) {
        if (obj == null) {
            return "null";
        }
        return serializeValue(obj, 0);
    }

    private static String serializeValue(Object obj, int depth) {
        if (obj == null) {
            return "null";
        }

        Class<?> clazz = obj.getClass();

        // 基本类型和包装类
        if (clazz == boolean.class || clazz == Boolean.class) {
            return obj.toString();
        }
        if (clazz == int.class || clazz == Integer.class ||
                clazz == long.class || clazz == Long.class ||
                clazz == short.class || clazz == Short.class ||
                clazz == byte.class || clazz == Byte.class ||
                clazz == float.class || clazz == Float.class ||
                clazz == double.class || clazz == Double.class) {
            return obj.toString();
        }
        if (clazz == String.class) {
            return toJsonString((String) obj);
        }
        if (clazz == Date.class) {
            return toJsonString(DateTimeFormatter.ISO_INSTANT.format(((Date) obj).toInstant()));
        }
        if (clazz == Instant.class) {
            return toJsonString(DateTimeFormatter.ISO_INSTANT.format((Instant) obj));
        }

        // Collection
        if (Collection.class.isAssignableFrom(clazz)) {
            return serializeCollection((Collection<?>) obj, depth);
        }

        // Map
        if (Map.class.isAssignableFrom(clazz)) {
            return serializeMap((Map<?, ?>) obj, depth);
        }

        // 数组
        if (clazz.isArray()) {
            return serializeArray(obj, depth);
        }

        // 递归序列化普通对象
        return serializeObject(obj, depth);
    }

    private static String serializeCollection(Collection<?> collection, int depth) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : collection) {
            if (!first) sb.append(",");
            sb.append(serializeValue(item, depth + 1));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private static String serializeMap(Map<?, ?> map, int depth) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append(toJsonString(entry.getKey().toString()));
            sb.append(":");
            sb.append(serializeValue(entry.getValue(), depth + 1));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private static String serializeArray(Object array, int depth) {
        StringBuilder sb = new StringBuilder("[");
        int len = Array.getLength(array);
        for (int i = 0; i < len; i++) {
            if (i > 0) sb.append(",");
            sb.append(serializeValue(Array.get(array, i), depth + 1));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String serializeObject(Object obj, int depth) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        // 使用反射获取所有字段
        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (!first) sb.append(",");
                    sb.append(toJsonString(field.getName()));
                    sb.append(":");
                    sb.append(serializeValue(value, depth + 1));
                    first = false;
                } catch (IllegalAccessException ignored) {
                }
            }
            clazz = clazz.getSuperclass();
        }

        sb.append("}");
        return sb.toString();
    }

    private static String toJsonString(String str) {
        if (str == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("\"");
        for (char c : str.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
