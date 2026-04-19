package wooho.common.util.validate.rules;

import wooho.common.util.validate.ValidationContext;
import wooho.common.util.validate.ValidationRule;

import java.util.Map;

/**
 * 数值范围校验规则：实现了 {@link Comparable} 的值必须在指定范围内。
 * <p>
 * 适用于 Integer、Long、Double、BigDecimal 等所有 Comparable 类型。
 * 默认错误码：{@code validation.range}
 *
 * @param <T> 必须实现 {@link Comparable}
 * @param min 最小值（包含）
 * @param max 最大值（包含）
 */
public class RangeRule<T extends Comparable<T>> implements ValidationRule<T> {

    private final T min;
    private final T max;

    public RangeRule(T min, T max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("min and max cannot be null");
        }
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("min (" + min + ") must be <= max (" + max + ")");
        }
        this.min = min;
        this.max = max;
    }

    @Override
    public Map<String, Object> validate(T value, ValidationContext context) {
        if (value == null) {
            return null; // 非空校验由 NotNullRule 负责
        }
        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            return Map.of("min", min, "max", max);
        }
        return null;
    }

    @Override
    public String defaultErrorCode() {
        return "validation.range";
    }
}
