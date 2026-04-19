package wooho.common.util.validate.rules;

import wooho.common.util.validate.ValidationContext;
import wooho.common.util.validate.ValidationRule;

import java.util.Map;

/**
 * 字符串长度范围校验规则：长度必须在指定最小值和最大值之间。
 * <p>
 * 仅适用于 {@link CharSequence} 类型。
 * 默认错误码：{@code validation.length}
 *
 * @param min 最小长度（包含）
 * @param max 最大长度（包含）
 */
public class LengthRule implements ValidationRule<CharSequence> {

    private final int min;
    private final int max;

    public LengthRule(int min, int max) {
        if (min < 0) throw new IllegalArgumentException("min cannot be negative: " + min);
        if (max < min) throw new IllegalArgumentException("max (" + max + ") must be >= min (" + min + ")");
        this.min = min;
        this.max = max;
    }

    @Override
    public Map<String, Object> validate(CharSequence value, ValidationContext context) {
        if (value == null) {
            return null; // 非空校验由 NotNullRule 负责
        }
        int len = value.length();
        if (len < min || len > max) {
            return Map.of("min", min, "max", max);
        }
        return null;
    }

    @Override
    public String defaultErrorCode() {
        return "validation.length";
    }
}
