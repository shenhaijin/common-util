package wooho.common.util.api.response.encrypt.annotation;

import java.lang.annotation.*;

/**
 * 接口响应加密注解。
 * <p>
 * 标记在 Controller 方法上，该方法的返回值会被加密后返回。
 * <p>
 * 前端收到的是加密字符串，需要使用对应密钥解密。
 *
 * <pre>
 *     &#64;EncryptResponse
 *     &#64;GetMapping("/user/info")
 *     public User getUserInfo() {
 *         return userService.getUserInfo();
 *     }
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptResponse {

    /**
     * 加密算法类型，默认 AES
     */
    AlgorithmType value() default AlgorithmType.AES_GCM;

    /**
     * 密钥标识，用于从密钥管理器获取对应密钥
     */
    String keyId() default "default";

    enum AlgorithmType {
        /** AES-GCM 模式（推荐，带认证，安全性高） */
        AES_GCM,
        /** AES-CBC 模式（需要 IV） */
        AES_CBC
    }
}
