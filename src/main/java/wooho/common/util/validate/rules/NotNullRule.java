package wooho.common.util.validate.rules;

import wooho.common.util.validate.ValidationContext;
import wooho.common.util.validate.ValidationRule;

import java.util.Map;

/**
 * 非空校验规则：值不能为 null
 * <p>
 * 默认错误码：{@code validation.notNull}
 */
public class NotNullRule<T> implements ValidationRule<T> {

    @Override
    public Map<String, Object> validate(T value, ValidationContext context) {
        if (value == null) {
            return Map.of(); // 无额外参数，field和value由Validator自动填充
        }
        return null; // 校验通过
    }

    @Override
    public String defaultErrorCode() {
        return "validation.notNull";
    }
}
