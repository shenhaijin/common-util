package wooho.common.util.validate.rules;

import wooho.common.util.validate.ValidationContext;
import wooho.common.util.validate.ValidationRule;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 正则表达式匹配校验规则：字符串值必须匹配指定的正则表达式。
 * <p>
 * 仅适用于 {@link CharSequence} 类型。
 * 默认错误码：{@code validation.pattern}
 */
public class PatternRule implements ValidationRule<CharSequence> {

    private final Pattern pattern;
    private final String regexStr;

    /**
     * 创建正则匹配规则
     *
     * @param regex 正则表达式字符串
     */
    public PatternRule(String regex) {
        if (regex == null || regex.isEmpty()) {
            throw new IllegalArgumentException("regex cannot be null or empty");
        }
        this.regexStr = regex;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public Map<String, Object> validate(CharSequence value, ValidationContext context) {
        if (value == null) {
            return null; // 非空校验由 NotNullRule 负责
        }
        if (!pattern.matcher(value).matches()) {
            return Map.of("pattern", regexStr);
        }
        return null;
    }

    @Override
    public String defaultErrorCode() {
        return "validation.pattern";
    }
}
