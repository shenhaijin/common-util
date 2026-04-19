package wooho.common.util.i18n;

import java.util.*;

/**
 * 基于 {@link ResourceBundle} 的 {@link MessageSource} 实现。
 * <p>
 * 从 classpath 加载 properties 资源文件，支持：
 * <ul>
 *     <li>命名占位符替换：{@code {field}}, {@code {min}} 等</li>
 *     <li>按 Locale 自动选择资源束</li>
 *     <li>回退机制：找不到指定Locale的资源时回退到默认资源</li>
 * </ul>
 */
public class ResourceBundleMessageSource implements MessageSource {

    /** 命名占位符正则：匹配 {xxx} 格式 */
    private static final java.util.regex.Pattern PLACEHOLDER_PATTERN =
            java.util.regex.Pattern.compile("\\{([^}]+)}");

    private final String baseName;
    private final Map<Locale, ResourceBundle> bundleCache = new HashMap<>();

    public ResourceBundleMessageSource(String baseName) {
        this.baseName = baseName;
    }

    @Override
    public String getMessage(String code) {
        return getMessage(code, Locale.getDefault());
    }

    @Override
    public String getMessage(String code, Object... args) {
        return getMessage(code, Locale.getDefault(), args);
    }

    @Override
    public String getMessage(String code, Locale locale, Object... args) {
        if (args == null || args.length == 0) {
            return resolveMessage(code, locale);
        }
        Map<String, Object> argMap = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            argMap.put(String.valueOf(i), args[i]);
        }
        return resolveAndFormat(code, locale, argMap);
    }

    @Override
    public String getMessage(String code, Locale locale, Map<String, Object> args) {
        if (args == null || args.isEmpty()) {
            return resolveMessage(code, locale);
        }
        return resolveAndFormat(code, locale, args);
    }

    private String resolveMessage(String code, Locale locale) {
        ResourceBundle bundle = getBundle(locale);
        if (bundle != null && bundle.containsKey(code)) {
            try {
                return bundle.getString(code);
            } catch (MissingResourceException ignored) {
            }
        }
        ResourceBundle defaultBundle = getBundle(Locale.ROOT);
        if (defaultBundle != null && defaultBundle.containsKey(code)) {
            try {
                return defaultBundle.getString(code);
            } catch (MissingResourceException ignored) {
            }
        }
        return code;
    }

    private String resolveAndFormat(String code, Locale locale, Map<String, Object> args) {
        String template = resolveMessage(code, locale);
        return formatWithNamedPlaceholders(template, args);
    }

    private String formatWithNamedPlaceholders(String template, Map<String, Object> args) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        java.util.regex.Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = args.get(key);
            String replacement = value != null ? String.valueOf(value) : matcher.group(0);
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private synchronized ResourceBundle getBundle(Locale locale) {
        return bundleCache.computeIfAbsent(locale, loc -> {
            try {
                return ResourceBundle.getBundle(baseName, loc);
            } catch (MissingResourceException e) {
                return null;
            }
        });
    }
}
