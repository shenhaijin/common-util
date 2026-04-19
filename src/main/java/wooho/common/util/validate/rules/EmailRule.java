package wooho.common.util.validate.rules;

import wooho.common.util.validate.ValidationContext;
import wooho.common.util.validate.ValidationRule;

import java.util.Map;

/**
 * 邮箱格式校验规则：校验字符串是否符合邮箱格式规范。
 * <p>
 * 仅适用于 {@link CharSequence} 类型。
 * 使用 RFC 5322 兼容的正则表达式进行基础格式校验。
 * 默认错误码：{@code validation.email}
 */
public class EmailRule implements ValidationRule<CharSequence> {

    /** 常用邮箱格式正则 */
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final java.util.regex.Pattern EMAIL_PATTERN =
            java.util.regex.Pattern.compile(EMAIL_REGEX);

    @Override
    public Map<String, Object> validate(CharSequence value, ValidationContext context) {
        if (value == null) {
            return null; // 非空校验由 NotNullRule 负责
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            return Map.of();
        }
        return null;
    }

    @Override
    public String defaultErrorCode() {
        return "validation.email";
    }
}
