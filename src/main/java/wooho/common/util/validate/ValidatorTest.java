package wooho.common.util.validate;

import wooho.common.util.validate.exception.ValidateException;
import wooho.common.util.validate.rules.FaxRule;
import wooho.common.util.validate.rules.PhoneRule;
import wooho.common.util.i18n.MessageHolder;

import java.util.Locale;

/**
 * 校验器功能演示（请在 IDE 控制台查看输出）
 */
public class ValidatorTest {

    public static void main(String[] args) {
        System.out.println("========== wooho.common.util.validate 测试演示 ==========\n");

        // 1. 测试中文校验
        testChineseValidation();

        // 2. 测试英文校验
        testEnglishValidation();

        // 3. 测试收集所有错误模式
        testCollectAllErrors();

        // 4. 测试自定义规则
        testCustomRule();

        // 5. 测试电话规则
        testPhoneRule();

        // 6. 测试传真规则
        testFaxRule();

        System.out.println("\n========== 测试完成 ==========");
    }

    /**
     * 测试中文校验
     */
    private static void testChineseValidation() {
        System.out.println("【1. 中文校验】");
        MessageHolder.setLocale(Locale.CHINA);

        try {
            // 正常值校验通过
            String username = "张三";
            Validator.of(username, "用户名").notNull().notBlank().length(2, 10).validate();
            System.out.println("  ✓ 用户名 '" + username + "' 校验通过");

            // 测试非空校验
            Validator.of(null, "用户名").notNull().validate();
        } catch (ValidateException e) {
            System.out.println("  ✗ 校验失败: " + e.getMessage());
        }

        try {
            // 测试长度校验
            Validator.of("AB", "用户名").length(3, 10).validate();
        } catch (ValidateException e) {
            System.out.println("  ✗ 校验失败: " + e.getMessage());
        }

        try {
            // 测试邮箱校验
            String email = "test@example";
            Validator.of(email, "邮箱").notNull().email().validate();
        } catch (ValidateException e) {
            System.out.println("  ✗ 校验失败: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * 测试英文校验
     */
    private static void testEnglishValidation() {
        System.out.println("【2. 英文校验】");
        MessageHolder.setLocale(Locale.US);

        try {
            Validator.of(null, "username").notNull().validate();
        } catch (ValidateException e) {
            System.out.println("  ✗ Validation failed: " + e.getMessage());
        }

        try {
            Validator.of("AB", "username").length(3, 10).validate();
        } catch (ValidateException e) {
            System.out.println("  ✗ Validation failed: " + e.getMessage());
        }

        try {
            Validator.of("test@example.com", "email").notNull().email().validate();
            System.out.println("  ✓ Email 'test@example.com' passed validation");
        } catch (ValidateException e) {
            System.out.println("  ✗ Validation failed: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * 测试收集所有错误模式
     */
    private static void testCollectAllErrors() {
        System.out.println("【3. 收集所有错误（failFast=false）】");
        MessageHolder.setLocale(Locale.CHINA);

        ValidationResult result = Validator.of("", "用户名")
                .failFast(false)
                .notNull()
                .notBlank()
                .length(3, 10)
                .validateResult();

        System.out.println("  校验结果: " + (result.valid() ? "通过" : "失败"));
        if (!result.valid()) {
            for (ValidationError error : result.errors()) {
                System.out.println("    - [" + error.errorCode() + "] " + error.message());
            }
        }
        System.out.println();
    }

    /**
     * 测试自定义规则
     */
    private static void testCustomRule() {
        System.out.println("【4. 自定义规则】");
        MessageHolder.setLocale(Locale.CHINA);

        // 自定义年龄校验：必须大于0
        try {
            Integer age = -5;
            Validator.of(age, "年龄")
                    .notNull()
                    .is((value, ctx) -> value > 0 ? null : java.util.Map.of("min", 0), "custom.age.positive")
                    .validate();
        } catch (ValidateException e) {
            System.out.println("  ✗ 校验失败: " + e.getMessage());
        }

        try {
            Integer age = 25;
            Validator.of(age, "年龄")
                    .notNull()
                    .is((value, ctx) -> value > 0 ? null : java.util.Map.of("min", 0), "custom.age.positive")
                    .validate();
            System.out.println("  ✓ 年龄 25 校验通过");
        } catch (ValidateException e) {
            System.out.println("  ✗ 校验失败: " + e.getMessage());
        }
    }

    /**
     * 测试电话号码规则
     */
    private static void testPhoneRule() {
        System.out.println("【5. 电话号码校验】");
        MessageHolder.setLocale(Locale.CHINA);

        // 测试静态方法
        System.out.println("  静态方法校验:");
        System.out.println("    手机号 13812345678: " + PhoneRule.isValidMobile("13812345678"));
        System.out.println("    手机号 12345678901: " + PhoneRule.isValidMobile("12345678901"));
        System.out.println("    座机 010-12345678: " + PhoneRule.isValidLandline("010-12345678"));
        System.out.println("    座机 0755-1234567: " + PhoneRule.isValidLandline("0755-1234567"));
        System.out.println("    国际号 +86 13812345678: " + PhoneRule.isValidInternational("+86 13812345678"));

        // 测试链式调用
        System.out.println("\n  链式调用校验:");
        try {
            Validator.of("13812345678", "手机号").notBlank().phone().validate();
            System.out.println("  ✓ 手机号 '13812345678' 校验通过");
        } catch (ValidateException e) {
            System.out.println("  ✗ 校验失败: " + e.getMessage());
        }

        try {
            Validator.of("010-12345678", "座机").notBlank().phone().validate();
            System.out.println("  ✓ 座机 '010-12345678' 校验通过");
        } catch (ValidateException e) {
            System.out.println("  ✗ 校验失败: " + e.getMessage());
        }

        try {
            Validator.of("+86 13812345678", "国际号").notBlank().phone().validate();
            System.out.println("  ✓ 国际号 '+86 13812345678' 校验通过");
        } catch (ValidateException e) {
            System.out.println("  ✗ 校验失败: " + e.getMessage());
        }

        try {
            Validator.of("123456", "手机号").notBlank().phone().validate();
        } catch (ValidateException e) {
            System.out.println("  ✗ 无效手机号 '123456' 校验失败: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * 测试传真号码规则
     */
    private static void testFaxRule() {
        System.out.println("【6. 传真号码校验】");
        MessageHolder.setLocale(Locale.CHINA);

        // 测试静态方法
        System.out.println("  静态方法校验:");
        System.out.println("    传真 010-12345678: " + FaxRule.isValidFax("010-12345678"));
        System.out.println("    传真 0755-1234567: " + FaxRule.isValidFax("0755-1234567"));
        System.out.println("    国际传真 +86 10 12345678: " + FaxRule.isValidInternationalFax("+86 10 12345678"));

        // 测试链式调用
        System.out.println("\n  链式调用校验:");
        try {
            Validator.of("0755-12345678", "传真号").notBlank().fax().validate();
            System.out.println("  ✓ 传真号 '0755-12345678' 校验通过");
        } catch (ValidateException e) {
            System.out.println("  ✗ 校验失败: " + e.getMessage());
        }

        try {
            Validator.of("12345", "传真号").notBlank().fax().validate();
        } catch (ValidateException e) {
            System.out.println("  ✗ 无效传真号 '12345' 校验失败: " + e.getMessage());
        }
    }
}
