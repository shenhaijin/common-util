package wooho.common.util.api.response.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加密密钥管理器。
 * <p>
 * 管理加密密钥的存储和获取，支持多密钥场景。
 * 生产环境中建议使用配置中心或 KMS 管理密钥。
 *
 * <pre>
 *     // 设置默认密钥
 *     EncryptKeyManager.setKey("default", "your-32-byte-secret-key!!");
 *
 *     // 设置多组密钥
 *     EncryptKeyManager.setKey("v1", "v1-32-byte-secret-key-here!!");
 *     EncryptKeyManager.setKey("v2", "v2-32-byte-secret-key-here!!");
 * </pre>
 */
public final class EncryptKeyManager {

    private static final Map<String, byte[]> KEY_STORE = new ConcurrentHashMap<>();
    private static volatile String defaultKeyId = "default";

    private static volatile byte[] defaultKey;
    private static final Map<String, byte[]> KEY_CACHE = new ConcurrentHashMap<>();

    private EncryptKeyManager() {
    }

    /**
     * 设置默认密钥
     * @param keyStr 密钥字符串（原始密钥或 Base64 编码）
     */
    public static void setKey(String keyStr) {
        setKey("default", keyStr);
    }

    /**
     * 设置指定标识的密钥
     * @param keyStr 密钥字符串（原始密钥或 Base64 编码）
     */
    public static void setKey(String keyId, String keyStr) {
        if (keyId == null || keyId.isBlank()) {
            keyId = "default";
        }
        byte[] keyBytes = decodeKey(keyStr);
        KEY_STORE.put(keyId, keyBytes);
        KEY_CACHE.remove(keyId);
        if ("default".equals(keyId)) {
            defaultKeyId = keyId;
        }
    }

    /**
     * 设置指定标识的密钥（字节数组）
     */
    public static void setKeyBytes(String keyId, byte[] keyBytes) {
        if (keyId == null || keyId.isBlank()) {
            keyId = "default";
        }
        KEY_STORE.put(keyId, keyBytes.clone());
        KEY_CACHE.remove(keyId);
        if ("default".equals(keyId)) {
            defaultKeyId = keyId;
        }
    }

    /**
     * 获取密钥字节数组
     */
    public static byte[] getKey(String keyId) {
        if (keyId == null || keyId.isBlank()) {
            keyId = defaultKeyId;
        }
        return KEY_CACHE.computeIfAbsent(keyId, id -> {
            byte[] key = KEY_STORE.get(id);
            if (key == null) {
                throw new IllegalStateException("Encryption key not found for keyId: " + id);
            }
            return key.clone();
        });
    }

    /**
     * 获取默认密钥字节数组
     */
    public static byte[] getDefaultKey() {
        return getKey(defaultKeyId);
    }

    /**
     * 生成随机密钥（用于初始化）
     */
    public static String generateKey(int length) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[length];
        random.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    /**
     * 生成 32 字节 AES 密钥（返回 Base64 编码）
     */
    public static String generateAesKey() {
        return generateKey(32);
    }

    /**
     * 解码密钥字符串
     * @param keyStr Base64 编码的密钥或原始密钥字符串
     */
    private static byte[] decodeKey(String keyStr) {
        if (keyStr == null || keyStr.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or blank");
        }
        try {
            // 尝试 Base64 解码
            return Base64.getDecoder().decode(keyStr);
        } catch (IllegalArgumentException e) {
            // 如果不是有效的 Base64，当作原始密钥使用
            return keyStr.getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * 设置默认密钥 ID
     */
    public static void setDefaultKeyId(String keyId) {
        if (keyId != null && KEY_STORE.containsKey(keyId)) {
            defaultKeyId = keyId;
        }
    }

    /**
     * 获取默认密钥 ID
     */
    public static String getDefaultKeyId() {
        return defaultKeyId;
    }

    /**
     * 清除所有密钥
     */
    public static void clear() {
        KEY_STORE.clear();
        KEY_CACHE.clear();
    }

    /**
     * 移除指定密钥
     */
    public static void removeKey(String keyId) {
        KEY_STORE.remove(keyId);
        KEY_CACHE.remove(keyId);
    }
}
