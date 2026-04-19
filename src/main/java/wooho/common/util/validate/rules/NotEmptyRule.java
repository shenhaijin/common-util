package wooho.common.util.validate.rules;

import wooho.common.util.validate.ValidationContext;
import wooho.common.util.validate.ValidationRule;

import java.util.Collection;
import java.util.Map;

/**
 * 非空校验规则：值不能为 null，且（如果是集合/数组/Map/字符串）大小/长度必须大于0。
 * <p>
 * 支持类型：
 * <ul>
 *     <li>{@link CharSequence} - 字符串长度 > 0</li>
 *     <li>{@link Collection} - 集合大小 > 0</li>
 *     <li>数组 - 数组长度 > 0</li>
 *     <li>{@link Map} - Map大小 > 0</li>
 * </ul>
 * <p>
 * 默认错误码：{@code validation.notEmpty}
 */
public class NotEmptyRule<T> implements ValidationRule<T> {

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> validate(T value, ValidationContext context) {
        if (value == null) {
            return Map.of();
        }
        if (value instanceof CharSequence cs && cs.isEmpty()) {
            return Map.of();
        }
        if (value instanceof Collection<?> coll && coll.isEmpty()) {
            return Map.of();
        }
        if (value instanceof Map<?, ?> map && map.isEmpty()) {
            return Map.of();
        }
        if (value instanceof Object[] arr && arr.length == 0) {
            return Map.of();
        }
        // 基本类型数组检查
        if (value.getClass().isArray() && java.lang.reflect.Array.getLength(value) == 0) {
            return Map.of();
        }
        return null;
    }

    @Override
    public String defaultErrorCode() {
        return "validation.notEmpty";
    }
}
