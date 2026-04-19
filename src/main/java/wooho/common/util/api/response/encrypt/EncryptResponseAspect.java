package wooho.common.util.api.response.encrypt;

import wooho.common.util.api.response.encrypt.annotation.EncryptResponse;
import wooho.common.util.api.response.encrypt.annotation.EncryptResponse.AlgorithmType;

import java.lang.reflect.Method;

/**
 * 响应加密 AOP 切面。
 * <p>
 * 拦截带有 {@link EncryptResponse} 注解的方法，自动加密返回值。
 */
public class EncryptResponseAspect {

    /**
     * 处理加密响应
     *
     * @param method 目标方法
     * @param result 原始返回值
     * @param annotation 注解信息
     * @return 加密后的结果
     */
    public static Object process(Method method, Object result, EncryptResponse annotation) {
        if (result == null) {
            return null;
        }

        // 序列化为 JSON
        String json = SimpleJsonSerializer.serialize(result);

        // 获取密钥
        String keyId = annotation.keyId();
        byte[] key = EncryptKeyManager.getKey(keyId);

        // 根据算法类型加密
        String encrypted;
        if (annotation.value() == AlgorithmType.AES_CBC) {
            encrypted = Encryptor.encryptAesCbc(json, key);
        } else {
            encrypted = Encryptor.encryptAesGcm(json, key);
        }

        // 返回加密结果
        return EncryptResult.of(encrypted, keyId);
    }

    /**
     * 判断方法是否需要加密
     */
    public static boolean shouldEncrypt(Method method) {
        return method.isAnnotationPresent(EncryptResponse.class);
    }
}
