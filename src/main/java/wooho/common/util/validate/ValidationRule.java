package wooho.common.util.validate;

import java.util.Map;

/**
 * 校验规则函数式接口。
 * <p>
 * 每个校验规则实现此接口，定义对特定值的合法性判断逻辑。
 * 规则通过 {@code errorCode} 关联国际化消息，通过 {@code params} 传递模板参数。
 *
 * <pre>
 *     // 内置规则使用示例
 *     ValidationRule&lt;String&gt; notNull = new NotNullRule();
 *
 *     // 自定义规则
 *     ValidationRule&lt;String&gt; customRule = (value, context) -> {
 *         if (value.startsWith("admin")) {
 *             return null; // 校验通过返回null
 *         }
 *         return Map.of("field", value); // 校验失败返回参数
 *     };
 * </pre>
 *
 * @param <T> 被校验值的类型
 */
@FunctionalInterface
public interface ValidationRule<T> {

    /**
     * 执行校验逻辑。
     *
     * @param value   被校验的值（可能为null）
     * @param context 校验上下文，包含字段名、消息源等
     * @return 如果校验通过返回 null；如果校验失败返回占位符参数Map（用于格式化错误消息），
     *         通常至少包含 "field" 键
     */
    Map<String, Object> validate(T value, ValidationContext context);

    /**
     * 返回该规则默认的错误码（对应资源文件中的消息键）。
     * <p>
     * 当链式调用中未指定自定义错误码时，使用此方法返回的值。
     *
     * @return 默认错误码
     */
    default String defaultErrorCode() {
        return "validation.custom";
    }
}
