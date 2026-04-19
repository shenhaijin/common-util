package wooho.common.util.i18n;

import java.util.Locale;

/**
 * 线程安全的 Locale 上下文持有者（基于 ThreadLocal）。
 * <p>
 * 用于在运行时动态切换当前线程的国际化语言环境，
 * 无需修改代码或重新部署即可切换语言。
 *
 * <pre>
 *     MessageHolder.setLocale(Locale.CHINA);   // 切换中文
 *     MessageHolder.setLocale(Locale.US);      // 切换英文
 *     Locale current = MessageHolder.getLocale();  // 获取当前Locale
 *     MessageHolder.reset();                   // 重置为默认
 * </pre>
 */
public final class MessageHolder {

    private static final ThreadLocal<Locale> LOCALE_CONTEXT = new ThreadLocal<>();

    /** 默认Locale */
    private static volatile Locale defaultLocale = Locale.CHINA;

    private MessageHolder() {
        // 工具类禁止实例化
    }

    /**
     * 设置当前线程的Locale
     */
    public static void setLocale(Locale locale) {
        if (locale != null) {
            LOCALE_CONTEXT.set(locale);
        }
    }

    /**
     * 获取当前线程的Locale，未设置时返回全局默认Locale
     */
    public static Locale getLocale() {
        Locale locale = LOCALE_CONTEXT.get();
        return locale != null ? locale : defaultLocale;
    }

    /**
     * 重置当前线程的Locale（清除ThreadLocal）
     */
    public static void reset() {
        LOCALE_CONTEXT.remove();
    }

    /**
     * 设置全局默认Locale
     */
    public static void setDefaultLocale(Locale locale) {
        if (locale != null) {
            defaultLocale = locale;
        }
    }

    /**
     * 获取全局默认Locale
     */
    public static Locale getDefaultLocale() {
        return defaultLocale;
    }
}
