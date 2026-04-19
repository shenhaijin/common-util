package wooho.common.util.api.response.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES 加密工具类。
 * <p>
 * 支持 AES-GCM 和 AES-CBC 两种模式：
 * <ul>
 *     <li>AES-GCM（推荐）：带认证的加密，无法篡改密文</li>
 *     <li>AES-CBC：传统模式，需要额外的 IV</li>
 * </ul>
 */
public class Encryptor {

    private static final String ALGORITHM = "AES";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final int CBC_IV_LENGTH = 16;

    private Encryptor() {
    }

    /**
     * AES-GCM 加密（推荐）
     * <p>
     * 每次加密自动生成随机 IV，安全性更高
     *
     * @param data      待加密数据
     * @param keyBase64 Base64 编码的密钥
     * @return Base64 编码的密文（IV + 密文）
     */
    public static String encryptAesGcm(String data, String keyBase64) {
        byte[] key = Base64.getDecoder().decode(keyBase64);
        return encryptAesGcm(data.getBytes(StandardCharsets.UTF_8), key);
    }

    /**
     * AES-GCM 加密（字节数组密钥）
     */
    public static String encryptAesGcm(String data, byte[] key) {
        return encryptAesGcm(data.getBytes(StandardCharsets.UTF_8), key);
    }

    /**
     * AES-GCM 加密
     */
    public static String encryptAesGcm(byte[] data, byte[] key) {
        try {
            // 生成随机 IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            // 创建密钥和 GCM 参数
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            // 加密
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
            byte[] cipherText = cipher.doFinal(data);

            // 拼接 IV + 密文
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM encryption failed", e);
        }
    }

    /**
     * AES-GCM 解密
     *
     * @param encryptedData Base64 编码的密文
     * @param keyBase64     Base64 编码的密钥
     * @return 解密后的字符串
     */
    public static String decryptAesGcm(String encryptedData, String keyBase64) {
        byte[] key = Base64.getDecoder().decode(keyBase64);
        return decryptAesGcm(encryptedData, key);
    }

    /**
     * AES-GCM 解密
     */
    public static String decryptAesGcm(String encryptedData, byte[] key) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedData);

            // 分离 IV 和密文
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherText = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, cipherText, 0, cipherText.length);

            // 创建密钥和 GCM 参数
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            // 解密
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM decryption failed", e);
        }
    }

    /**
     * AES-CBC 加密
     *
     * @param data      待加密数据
     * @param keyBase64 Base64 编码的密钥（16/24/32 字节）
     * @return Base64 编码的密文（IV + 密文）
     */
    public static String encryptAesCbc(String data, String keyBase64) {
        byte[] key = Base64.getDecoder().decode(keyBase64);
        return encryptAesCbc(data.getBytes(StandardCharsets.UTF_8), key);
    }

    /**
     * AES-CBC 加密（字节数组密钥）
     */
    public static String encryptAesCbc(String data, byte[] key) {
        return encryptAesCbc(data.getBytes(StandardCharsets.UTF_8), key);
    }

    /**
     * AES-CBC 加密
     */
    public static String encryptAesCbc(byte[] data, byte[] key) {
        try {
            // 生成随机 IV
            byte[] iv = new byte[CBC_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            // 创建密钥和 IV 参数
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            javax.crypto.spec.IvParameterSpec ivSpec = new javax.crypto.spec.IvParameterSpec(iv);

            // 加密（使用 PKCS5Padding）
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] cipherText = cipher.doFinal(data);

            // 拼接 IV + 密文
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("AES-CBC encryption failed", e);
        }
    }

    /**
     * AES-CBC 解密
     *
     * @param encryptedData Base64 编码的密文
     * @param keyBase64     Base64 编码的密钥
     * @return 解密后的字符串
     */
    public static String decryptAesCbc(String encryptedData, String keyBase64) {
        byte[] key = Base64.getDecoder().decode(keyBase64);
        return decryptAesCbc(encryptedData, key);
    }

    /**
     * AES-CBC 解密
     */
    public static String decryptAesCbc(String encryptedData, byte[] key) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedData);

            // 分离 IV 和密文
            byte[] iv = new byte[CBC_IV_LENGTH];
            byte[] cipherText = new byte[combined.length - CBC_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, CBC_IV_LENGTH);
            System.arraycopy(combined, CBC_IV_LENGTH, cipherText, 0, cipherText.length);

            // 创建密钥和 IV 参数
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            javax.crypto.spec.IvParameterSpec ivSpec = new javax.crypto.spec.IvParameterSpec(iv);

            // 解密
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES-CBC decryption failed", e);
        }
    }
}
