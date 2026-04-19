package wooho.common.util.validate;

import wooho.common.util.i18n.MessageSource;
import wooho.common.util.i18n.ResourceBundleMessageSource;

import java.util.Locale;
import java.util.Map;

/**
 * 校验上下文，在一次校验过程中传递共享信息。
 * <p>
 * 包含字段名、消息源、Locale等上下文数据，
 * 在链式调用的各条规则之间共享。
 *
 * @param fieldName  被校验的字段名
 * @param messageSource 国际化消息源
 * @param locale     当前语言环境
 */
public record ValidationContext(
        String fieldName,
        MessageSource messageSource,
        Locale locale
) {
    /**
     * 默认的消息源（基于ResourceBundle）
     */
    private static final MessageSource DEFAULT_MESSAGE_SOURCE =
            new ResourceBundleMessageSource("wooho.common.util.i18n.messages");

    /**
     * 使用当前线程的默认上下文创建校验上下文
     */
    public static ValidationContext of(String fieldName) {
        return new ValidationContext(
                fieldName,
                DEFAULT_MESSAGE_SOURCE,
                wooho.common.util.i18n.MessageHolder.getLocale()
        );
    }

    /**
     * 创建自定义消息源的校验上下文
     */
    public static ValidationContext of(String fieldName, MessageSource messageSource) {
        return new ValidationContext(
                fieldName,
                messageSource != null ? messageSource : DEFAULT_MESSAGE_SOURCE,
                wooho.common.util.i18n.MessageHolder.getLocale()
        );
    }

    /**
     * 解析国际化消息
     */
    public String resolveMessage(String code, Map<String, Object> params) {
        return messageSource().getMessage(code, locale(), params);
    }
}
