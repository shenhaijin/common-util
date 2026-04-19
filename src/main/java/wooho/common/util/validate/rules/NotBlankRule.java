package wooho.common.util.validate.rules;

import wooho.common.util.validate.ValidationContext;
import wooho.common.util.validate.ValidationRule;

import java.util.Map;

/**
 * 非空白校验规则：字符串不能为 null、空串或纯空白字符。
 * <p>
 * 仅适用于 {@link CharSequence} 类型。
 * 默认错误码：{@code validation.notBlank}
 */
public class NotBlankRule implements ValidationRule<CharSequence> {

    @Override
    public Map<String, Object> validate(CharSequence value, ValidationContext context) {
        if (value == null || value.toString().isBlank()) {
            return Map.of();
        }
        return null;
    }

    @Override
    public String defaultErrorCode() {
        return "validation.notBlank";
    }
}
