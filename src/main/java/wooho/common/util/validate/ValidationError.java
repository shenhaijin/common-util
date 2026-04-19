package wooho.common.util.validate;

/**
 * 校验错误记录，记录一次校验失败的详细信息。
 * <p>
 * 使用 JDK record 实现，不可变对象。
 *
 * @param errorCode   错误码（对应资源文件中的消息键）
 * @param message     国际化后的错误消息文本
 * @param fieldName   被校验的字段名
 * @param rejectedValue 被拒绝的非法值
 */
public record ValidationError(
        String errorCode,
        String message,
        String fieldName,
        Object rejectedValue
) {
}
