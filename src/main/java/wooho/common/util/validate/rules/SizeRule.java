package wooho.common.util.validate.rules;

import wooho.common.util.validate.ValidationContext;
import wooho.common.util.validate.ValidationRule;

import java.util.Collection;
import java.util.Map;

/**
 * 大小范围校验规则：集合/数组/Map的大小必须在指定范围内。
 * <p>
 * 支持类型：Collection、数组、Map。
 * 默认错误码：{@code validation.size}
 *
 * @param min 最小大小（包含）
 * @param max 最大大小（包含）
 */
public class SizeRule<T> implements ValidationRule<T> {

    private final int min;
    private final int max;

    public SizeRule(int min, int max) {
        if (min < 0) throw new IllegalArgumentException("min cannot be negative: " + min);
        if (max < min) throw new IllegalArgumentException("max (" + max + ") must be >= min (" + min + ")");
        this.min = min;
        this.max = max;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> validate(T value, ValidationContext context) {
        if (value == null) {
            return null; // 非空校验由 NotNullRule 负责
        }
        int size;
        if (value instanceof Collection<?> coll) {
            size = coll.size();
        } else if (value instanceof Map<?, ?> map) {
            size = map.size();
        } else if (value.getClass().isArray()) {
            size = java.lang.reflect.Array.getLength(value);
        } else {
            return null; // 不支持的类型跳过
        }
        if (size < min || size > max) {
            return Map.of("min", min, "max", max);
        }
        return null;
    }

    @Override
    public String defaultErrorCode() {
        return "validation.size";
    }
}
