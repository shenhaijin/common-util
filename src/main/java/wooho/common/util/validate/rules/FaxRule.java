package wooho.common.util.validate.rules;

import wooho.common.util.validate.ValidationContext;
import wooho.common.util.validate.ValidationRule;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 传真号码校验规则。
 * <p>
 * 支持校验：
 * - 中国大陆传真号（格式类似固定电话）
 * - 国际传真号码（以 + 开头）
 *
 * @author shenhaijin
 */
public class FaxRule implements ValidationRule<CharSequence> {

    private static final String DEFAULT_MESSAGE_CODE = "validation.fax";

    /**
     * 中国传真号码正则
     * 格式：区号(3-4位) + 分隔符(-/空格/无) + 号码(7-8位)
     * 或纯号码（5-15位数字）
     */
    private static final Pattern FAX_PATTERN = Pattern.compile(
            "^(\\+86[-\\s]?)?(0\\d{2,3}[-\\s]?)?[1-9]\\d{6,7}$|^\\+?[1-9]\\d{4,14}$"
    );

    /**
     * 国际传真号码正则
     * 格式：+国家码 + 号码
     */
    private static final Pattern INTERNATIONAL_FAX_PATTERN = Pattern.compile(
            "^\\+[1-9]\\d{0,2}[-\\s]?\\d{1,4}[-\\s]?\\d{3,14}$"
    );

    @Override
    public Map<String, Object> validate(CharSequence value, ValidationContext context) {
        if (value == null) {
            return null; // null 值由其他规则处理（如 NotNullRule）
        }

        String fax = value.toString().trim();
        if (!isValidFax(fax)) {
            return Map.of();
        }
        return null;
    }

    @Override
    public String defaultErrorCode() {
        return DEFAULT_MESSAGE_CODE;
    }

    /**
     * 校验是否为有效的传真号码
     *
     * @param fax 传真号码字符串
     * @return true 表示有效
     */
    public static boolean isValidFax(String fax) {
        if (fax == null || fax.isBlank()) {
            return false;
        }

        String trimmed = fax.trim();
        // 移除所有空格和连字符后检查
        String normalized = trimmed.replaceAll("[-\\s]", "");
        return normalized.matches("^\\+?[0-9]{5,15}$");
    }

    /**
     * 校验是否为有效的中国大陆传真号
     */
    public static boolean isValidChinaFax(String fax) {
        if (fax == null || fax.isBlank()) {
            return false;
        }
        return FAX_PATTERN.matcher(fax.trim()).matches();
    }

    /**
     * 校验是否为有效的国际传真号码
     */
    public static boolean isValidInternationalFax(String fax) {
        if (fax == null || fax.isBlank()) {
            return false;
        }
        return INTERNATIONAL_FAX_PATTERN.matcher(fax.trim()).matches();
    }
}
