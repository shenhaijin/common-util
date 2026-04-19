package wooho.common.util.validate;

import wooho.common.util.validate.exception.ValidateException;
import wooho.common.util.i18n.MessageHolder;
import wooho.common.util.validate.rules.*;

import java.util.*;

/**
 * 链式校验器核心入口。
 * <p>
 * 支持流畅的链式调用风格，内置多种常用校验规则。
 * 每个校验方法返回 {@code this} 以支持链式调用，
 * 最终通过 {@link #validate()} 或 {@link #validateResult()} 触发实际校验。
 *
 * <h3>基本用法：</h3>
 * <pre>{@code
 * Validator.of(username, "用户名")
 *       .notNull()
 *       .notBlank()
 *       .length(3, 20)
 *       .validate();  // 失败时抛出 ValidateException
 * }</pre>
 *
 * <h3>收集所有错误模式：</h3>
 * <pre>{@code
 * ValidationResult result = Validator.of(value, "字段")
 *       .failFast(false)
 *       .notNull()
 *       .length(1, 10)
 *       .validateResult();  // 返回结果而非抛异常
 * }</pre>
 *
 * <h3>自定义消息键：</h3>
 * <pre>{@code
 * Validator.of(email, "邮箱")
 *       .notNull("user.email.required")
 *       .email()
 *       .validate();
 * }</pre>
 *
 * @param <T> 被校验值的类型
 */
public class Validator<T> {

    private final T value;
    private final String fieldName;
    private final List<RuleEntry<T>> rules = new ArrayList<>();
    private boolean failFast = true;

    // ========== 构造方法 ==========

    private Validator(T value, String fieldName) {
        this.value = value;
        this.fieldName = fieldName != null ? fieldName : "unknown";
    }

    /**
     * 创建校验器实例
     *
     * @param value     待校验的值
     * @param fieldName 字段名（用于错误消息中显示）
     * @return 校验器实例
     */
    public static <T> Validator<T> of(T value, String fieldName) {
        return new Validator<>(value, fieldName);
    }

    /**
     * 创建校验器实例（不指定字段名）
     */
    public static <T> Validator<T> of(T value) {
        return new Validator<>(value, null);
    }

    // ========== 模式设置 ==========

    /**
     * 设置失败模式。true=遇到第一个错误立即抛异常（默认）；false=收集所有错误
     *
     * @return this
     */
    public Validator<T> failFast(boolean failFast) {
        this.failFast = failFast;
        return this;
    }

    // ========== 内置校验规则 - 链式方法 ==========

    /**
     * 非空校验：值不能为 null
     */
    public Validator<T> notNull() {
        return notNull(null);
    }

    /**
     * 非空校验（自定义错误码）
     */
    public Validator<T> notNull(String errorCode) {
        rules.add(new RuleEntry<>(new NotNullRule<>(), errorCode));
        return this;
    }

    /**
     * 非空白校验：字符串不能为 null、空串或纯空白（仅适用于 CharSequence）
     */
    @SuppressWarnings("unchecked")
    public Validator<T> notBlank() {
        return notBlank(null);
    }

    /**
     * 非空白校验（自定义错误码）
     */
    @SuppressWarnings("unchecked")
    public Validator<T> notBlank(String errorCode) {
        rules.add(new RuleEntry<>((ValidationRule<T>) new NotBlankRule(), errorCode));
        return this;
    }

    /**
     * 非空校验：字符串/集合/数组/Map不能为 null 且 size/length > 0
     */
    public Validator<T> notEmpty() {
        return notEmpty(null);
    }

    /**
     * 非空校验（自定义错误码）
     */
    public Validator<T> notEmpty(String errorCode) {
        rules.add(new RuleEntry<>(new NotEmptyRule<>(), errorCode));
        return this;
    }

    /**
     * 字符串长度范围校验：长度必须在 min 和 max 之间（仅适用于 CharSequence）
     */
    @SuppressWarnings("unchecked")
    public Validator<T> length(int min, int max) {
        return length(min, max, null);
    }

    /**
     * 字符串长度范围校验（自定义错误码）
     */
    @SuppressWarnings("unchecked")
    public Validator<T> length(int min, int max, String errorCode) {
        rules.add(new RuleEntry<>((ValidationRule<T>) new LengthRule(min, max), errorCode));
        return this;
    }

    /**
     * 集合/数组/Map大小范围校验：size 必须在 min 和 max 之间
     */
    public Validator<T> size(int min, int max) {
        return size(min, max, null);
    }

    /**
     * 大小范围校验（自定义错误码）
     */
    public Validator<T> size(int min, int max, String errorCode) {
        rules.add(new RuleEntry<>(new SizeRule<>(min, max), errorCode));
        return this;
    }

    /**
     * 数值范围校验：值必须在 min 和 max 之间（Comparable类型）
     */
    public <R extends Comparable<R>> Validator<T> range(R min, R max) {
        return range(min, max, null);
    }

    /**
     * 数值范围校验（自定义错误码）
     */
    @SuppressWarnings("unchecked")
    public <R extends Comparable<R>> Validator<T> range(R min, R max, String errorCode) {
        rules.add(new RuleEntry<>((ValidationRule<T>) new RangeRule<>(min, max), errorCode));
        return this;
    }

    /**
     * 正则匹配校验：值必须匹配指定的正则表达式（仅适用于 CharSequence）
     */
    @SuppressWarnings("unchecked")
    public Validator<T> pattern(String regex) {
        return pattern(regex, null);
    }

    /**
     * 正则匹配校验（自定义错误码）
     */
    @SuppressWarnings("unchecked")
    public Validator<T> pattern(String regex, String errorCode) {
        rules.add(new RuleEntry<>((ValidationRule<T>) new PatternRule(regex), errorCode));
        return this;
    }

    /**
     * 邮箱格式校验（仅适用于 CharSequence）
     */
    @SuppressWarnings("unchecked")
    public Validator<T> email() {
        return email(null);
    }

    /**
     * 邮箱格式校验（自定义错误码）
     */
    @SuppressWarnings("unchecked")
    public Validator<T> email(String errorCode) {
        rules.add(new RuleEntry<>((ValidationRule<T>) new EmailRule(), errorCode));
        return this;
    }

    /**
     * 自定义校验规则：传入一个 Predicate lambda 进行任意校验逻辑
     */
    public Validator<T> is(ValidationRule<T> rule) {
        return is(rule, null);
    }

    /**
     * 自定义校验规则（自定义错误码）
     */
    public Validator<T> is(ValidationRule<T> rule, String errorCode) {
        rules.add(new RuleEntry<>(rule, errorCode));
        return this;
    }

    // ========== 执行校验 ==========

    /**
     * 执行校验，失败时立即抛出 {@link ValidateException}
     *
     * @throws ValidateException 当存在校验错误时抛出
     */
    public void validate() throws ValidateException {
        ValidationResult result = validateResult();
        if (!result.valid()) {
            ValidationError firstError = result.errors().get(0);
            throw new ValidateException(
                    firstError.errorCode(),
                    firstError.message(),
                    firstError.fieldName(),
                    firstError.rejectedValue()
            );
        }
    }

    /**
     * 执行校验并返回结果（不抛异常）
     *
     * @return 校验结果
     */
    public ValidationResult validateResult() {
        ValidationContext context = ValidationContext.of(fieldName);
        List<ValidationError> errors = new ArrayList<>();

        for (RuleEntry<T> entry : rules) {
            Map<String, Object> ruleParams = entry.rule().validate(value, context);
            if (ruleParams != null) {
                // 校验失败
                String effectiveCode = entry.errorCode() != null
                        ? entry.errorCode()
                        : entry.rule().defaultErrorCode();

                // 合并规则参数和基础参数
                Map<String, Object> allParams = new LinkedHashMap<>();
                allParams.put("field", fieldName);
                allParams.put("value", value);
                if (ruleParams != null) {
                    allParams.putAll(ruleParams);
                }

                String message = context.resolveMessage(effectiveCode, allParams);
                errors.add(new ValidationError(effectiveCode, message, fieldName, value));

                if (failFast) {
                    break;
                }
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    // ========== 内部记录 ==========

    /**
     * 规则条目：关联一条校验规则和可选的自定义错误码
     */
    private record RuleEntry<T>(ValidationRule<T> rule, String errorCode) {}
}
