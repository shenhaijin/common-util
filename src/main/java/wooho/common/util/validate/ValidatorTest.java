package wooho.common.util.validate;

import wooho.common.util.validate.exception.ValidateException;
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
}
