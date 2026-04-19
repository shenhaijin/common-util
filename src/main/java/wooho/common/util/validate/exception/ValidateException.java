package wooho.common.util.validate.exception;

/**
 * 校验异常，当参数校验失败时抛出。
 * <p>
 * 包含错误码、国际化消息、字段名和拒绝值等详细信息。
 *
 * <pre>
 *     throw new ValidateException("validation.notNull", "用户名不能为空", "username", null);
 * </pre>
 */
public class ValidateException extends RuntimeException {

    private final String errorCode;
    private final String fieldName;
    private final Object rejectedValue;

    public ValidateException(String errorCode, String message, String fieldName, Object rejectedValue) {
        super(message);
        this.errorCode = errorCode;
        this.fieldName = fieldName;
        this.rejectedValue = rejectedValue;
    }

    public ValidateException(String errorCode, String message) {
        this(errorCode, message, null, null);
    }

    /**
     * 获取错误码（对应资源文件中的消息键）
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 获取校验失败的字段名
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * 获取被拒绝的值
     */
    public Object getRejectedValue() {
        return rejectedValue;
    }
}
