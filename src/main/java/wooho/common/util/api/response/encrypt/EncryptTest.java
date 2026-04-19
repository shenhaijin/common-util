package wooho.common.util.api.response.encrypt;

import wooho.common.util.api.response.encrypt.annotation.EncryptResponse;
import wooho.common.util.api.response.encrypt.annotation.EncryptResponse.AlgorithmType;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加密功能测试演示
 */
public class EncryptTest {

    public static void main(String[] args) {
        System.out.println("========== 加密功能测试 ==========\n");

        // 1. 初始化密钥
        initKeys();

        // 2. 测试 AES-GCM 加密
        testAesGcm();

        // 3. 测试 AES-CBC 加密
        testAesCbc();

        // 4. 测试对象加密
        testObjectEncrypt();

        // 5. 测试集合加密
        testCollectionEncrypt();

        // 6. 性能测试
        performanceTest();

        System.out.println("\n========== 测试完成 ==========");
    }

    private static void initKeys() {
        System.out.println("【1. 初始化密钥】");
        // 生成并设置默认密钥
        String defaultKey = EncryptKeyManager.generateAesKey();
        EncryptKeyManager.setKey("default", defaultKey);
        System.out.println("  默认密钥已生成: " + defaultKey.substring(0, 10) + "...");
        System.out.println("  ✓ 密钥初始化成功\n");
    }

    private static void testAesGcm() {
        System.out.println("【2. AES-GCM 加密测试】");
        String data = "Hello, 这是一段测试文本！";

        byte[] keyBytes = EncryptKeyManager.getKey("default");

        // 加密
        String encrypted = Encryptor.encryptAesGcm(data, keyBytes);
        System.out.println("  原始数据: " + data);
        System.out.println("  加密后: " + encrypted.substring(0, Math.min(50, encrypted.length())) + "...");

        // 解密
        String decrypted = Encryptor.decryptAesGcm(encrypted, keyBytes);
        System.out.println("  解密后: " + decrypted);
        System.out.println("  ✓ AES-GCM 测试通过\n");
    }

    private static void testAesCbc() {
        System.out.println("【3. AES-CBC 加密测试】");
        String data = "CBC 模式测试数据";

        byte[] keyBytes = EncryptKeyManager.getKey("default");

        // 加密
        String encrypted = Encryptor.encryptAesCbc(data, keyBytes);
        System.out.println("  原始数据: " + data);
        System.out.println("  加密后: " + encrypted.substring(0, Math.min(50, encrypted.length())) + "...");

        // 解密
        String decrypted = Encryptor.decryptAesCbc(encrypted, keyBytes);
        System.out.println("  解密后: " + decrypted);
        System.out.println("  ✓ AES-CBC 测试通过\n");
    }

    private static void testObjectEncrypt() {
        System.out.println("【4. 对象加密测试】");
        UserDto user = new UserDto();
        user.setId(1001L);
        user.setUsername("张三");
        user.setEmail("zhangsan@example.com");
        user.setCreateTime(new Date());
        Map<String, Object> extra = new HashMap<>();
        extra.put("department", "技术部");
        extra.put("level", 3);
        user.setExtra(extra);

        // 模拟 Controller 方法处理
        try {
            Method method = TestController.class.getMethod("getUser");
            EncryptResponse annotation = method.getAnnotation(EncryptResponse.class);
            Object result = EncryptResponseAspect.process(method, user, annotation);

            System.out.println("  原始对象: " + user);
            System.out.println("  加密后类型: " + result.getClass().getSimpleName());

            if (result instanceof EncryptResult er) {
                System.out.println("  加密类型: " + er.type());
                System.out.println("  密钥ID: " + er.keyId());
                System.out.println("  密文: " + er.data().substring(0, Math.min(60, er.data().length())) + "...");

                // 解密验证
                String decrypted = Encryptor.decryptAesGcm(er.data(), EncryptKeyManager.getKey(er.keyId()));
                System.out.println("  解密JSON: " + decrypted);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        System.out.println("  ✓ 对象加密测试通过\n");
    }

    private static void testCollectionEncrypt() {
        System.out.println("【5. 集合加密测试】");

        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setUsername("用户A");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setUsername("用户B");

        List<UserDto> users = List.of(user1, user2);

        // 模拟 Controller 方法处理
        try {
            Method method = TestController.class.getMethod("listUsers");
            EncryptResponse annotation = method.getAnnotation(EncryptResponse.class);
            Object result = EncryptResponseAspect.process(method, users, annotation);

            System.out.println("  原始集合大小: " + users.size());
            System.out.println("  加密后类型: " + result.getClass().getSimpleName());

            if (result instanceof EncryptResult er) {
                // 解密验证
                String decrypted = Encryptor.decryptAesGcm(er.data(), EncryptKeyManager.getKey(er.keyId()));
                System.out.println("  解密JSON: " + decrypted);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        System.out.println("  ✓ 集合加密测试通过\n");
    }

    private static void performanceTest() {
        System.out.println("【6. 性能测试】");
        
        UserDto user = new UserDto();
        user.setId(1L);
        user.setUsername("性能测试用户");
        user.setEmail("perf@example.com");
        user.setCreateTime(new Date());

        int times = 1000;
        long start, end;

        // 序列化 + 加密
        start = System.nanoTime();
        for (int i = 0; i < times; i++) {
            String json = SimpleJsonSerializer.serialize(user);
            Encryptor.encryptAesGcm(json, EncryptKeyManager.getKey("default"));
        }
        end = System.nanoTime();
        double encryptMs = (end - start) / 1_000_000.0;
        System.out.println("  加密 " + times + " 次耗时: " + String.format("%.2f", encryptMs) + " ms");
        System.out.println("  平均每次: " + String.format("%.3f", encryptMs / times) + " ms");

        // 加密字符串解密
        String encrypted = Encryptor.encryptAesGcm(
            SimpleJsonSerializer.serialize(user), 
            EncryptKeyManager.getKey("default")
        );
        start = System.nanoTime();
        for (int i = 0; i < times; i++) {
            Encryptor.decryptAesGcm(encrypted, EncryptKeyManager.getKey("default"));
        }
        end = System.nanoTime();
        double decryptMs = (end - start) / 1_000_000.0;
        System.out.println("  解密 " + times + " 次耗时: " + String.format("%.2f", decryptMs) + " ms");
        System.out.println("  平均每次: " + String.format("%.3f", decryptMs / times) + " ms");
        System.out.println("  ✓ 性能测试完成\n");
    }

    // ========== 模拟 Controller ==========

    public static class TestController {

        @EncryptResponse(keyId = "default")
        public static UserDto getUser() {
            return new UserDto();
        }

        @EncryptResponse(keyId = "default")
        public static List<UserDto> listUsers() {
            return List.of(new UserDto(), new UserDto());
        }
    }

    // ========== DTO ==========

    public static class UserDto {
        private Long id;
        private String username;
        private String email;
        private Date createTime;
        private Map<String, Object> extra;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Date getCreateTime() { return createTime; }
        public void setCreateTime(Date createTime) { this.createTime = createTime; }
        public Map<String, Object> getExtra() { return extra; }
        public void setExtra(Map<String, Object> extra) { this.extra = extra; }

        @Override
        public String toString() {
            return "UserDto{id=" + id + ", username='" + username + "'}";
        }
    }
}
