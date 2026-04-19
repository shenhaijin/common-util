package wooho.common.util.validate.rules;

import wooho.common.util.validate.ValidationContext;
import wooho.common.util.validate.ValidationRule;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 电话号码校验规则。
 * <p>
 * 支持校验：
 * - 中国大陆手机号（13/14/15/16/17/18/19 开头，11位）
 * - 固定电话/座机号（带区号，格式：010-12345678 或 010 12345678）
 * - 国际号码（以 + 开头，如 +86 13812345678）
 *
 * @author shenhaijin
 */
public class PhoneRule implements ValidationRule<CharSequence> {

    private static final String DEFAULT_MESSAGE_CODE = "validation.phone";

    /**
     * 中国大陆手机号正则
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile(
            "^1[3-9]\\d{9}$"
    );

    /**
     * 固定电话正则（支持多种格式）
     * 格式：区号(3-4位) + 分隔符(-/空格/无) + 号码(7-8位)
     */
    private static final Pattern LANDLINE_PATTERN = Pattern.compile(
            "^(0\\d{2,3}[-\\s]?)?[1-9]\\d{6,7}$"
    );

    /**
     * 国际号码正则
     * 格式：+国家码 + 号码
     */
    private static final Pattern INTERNATIONAL_PATTERN = Pattern.compile(
            "^\\+[1-9]\\d{0,2}[-\\s]?\\d{1,4}[-\\s]?\\d{3,14}$"
    );

    @Override
    public Map<String, Object> validate(CharSequence value, ValidationContext context) {
        if (value == null) {
            return null; // null 值由其他规则处理（如 NotNullRule）
        }

        String phone = value.toString().trim();
        if (!isValidPhone(phone)) {
            return Map.of();
        }
        return null;
    }

    @Override
    public String defaultErrorCode() {
        return DEFAULT_MESSAGE_CODE;
    }

    /**
     * 校验是否为有效的电话号码
     *
     * @param phone 电话号码字符串
     * @return true 表示有效
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }

        String trimmed = phone.trim();
        return MOBILE_PATTERN.matcher(trimmed).matches()
                || LANDLINE_PATTERN.matcher(trimmed).matches()
                || INTERNATIONAL_PATTERN.matcher(trimmed).matches();
    }

    /**
     * 校验是否为有效的中国大陆手机号
     */
    public static boolean isValidMobile(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        return MOBILE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * 校验是否为有效的固定电话
     */
    public static boolean isValidLandline(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        return LANDLINE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * 校验是否为有效的国际号码
     */
    public static boolean isValidInternational(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        return INTERNATIONAL_PATTERN.matcher(phone.trim()).matches();
    }
}
