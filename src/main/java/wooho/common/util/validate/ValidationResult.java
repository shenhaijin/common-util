package wooho.common.util.validate;

import java.util.List;

/**
 * 校验结果，记录一次或多次校验的最终结果。
 * <p>
 * 使用 JDK record 实现，不可变对象。
 *
 * @param valid   是否全部校验通过
 * @param errors  校验失败的错误列表（空列表表示全部通过）
 */
public record ValidationResult(boolean valid, List<ValidationError> errors) {

    /** 预定义的空成功结果实例 */
    public static final ValidationResult OK = new ValidationResult(true, List.of());

    public ValidationResult {
        errors = errors == null ? List.of() : List.copyOf(errors);
    }
}
