package wooho.common.util.api.response.encrypt;

/**
 * 加密响应封装类。
 * <p>
 * 用于包装加密后的数据，前端根据 type 判断解密方式
 *
 * @param type    加密类型标识
 * @param data    加密后的密文
 * @param keyId   密钥标识（前端据此选择对应密钥）
 * @param version 加密版本
 */
public record EncryptResult(
        String type,
        String data,
        String keyId,
        int version
) {

    public static final String DEFAULT_TYPE = "AES-GCM";
    public static final int DEFAULT_VERSION = 1;

    /**
     * 创建加密结果
     */
    public static EncryptResult of(String encryptedData, String keyId) {
        return new EncryptResult(DEFAULT_TYPE, encryptedData, keyId, DEFAULT_VERSION);
    }

    /**
     * 创建加密结果（使用默认密钥）
     */
    public static EncryptResult of(String encryptedData) {
        return of(encryptedData, EncryptKeyManager.getDefaultKeyId());
    }
}
