package wooho.common.util.i18n;

import java.util.Locale;
import java.util.Map;

/**
 * 国际化消息源接口。
 * <p>
 * 提供根据消息键和Locale获取国际化消息的能力，
 * 支持占位符参数替换。
 */
public interface MessageSource {

    /**
     * 根据消息键和默认Locale获取消息
     *
     * @param code 消息键
     * @return 国际化后的消息文本
     */
    String getMessage(String code);

    /**
     * 根据消息键、参数数组和默认Locale获取消息
     *
     * @param code       消息键
     * @param args       占位符参数
     * @return 国际化后的消息文本
     */
    String getMessage(String code, Object... args);

    /**
     * 根据消息键、指定Locale和参数数组获取消息
     *
     * @param code   消息键
     * @param locale 目标语言环境
     * @param args   占位符参数
     * @return 国际化后的消息文本
     */
    String getMessage(String code, Locale locale, Object... args);

    /**
     * 根据消息键、指定Locale和Map类型参数获取消息
     *
     * @param code   消息键
     * @param locale 目标语言环境
     * @param args   命名参数（key为占位符名称，不含花括号）
     * @return 国际化后的消息文本
     */
    String getMessage(String code, Locale locale, Map<String, Object> args);
}
