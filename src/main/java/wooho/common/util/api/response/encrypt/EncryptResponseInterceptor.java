package wooho.common.util.api.response.encrypt;

import wooho.common.util.api.response.encrypt.annotation.EncryptResponse;

import java.lang.reflect.Method;

/**
 * 响应加密拦截器（用于 Spring MVC 环境）。
 * <p>
 * 配合 Spring 的 HandlerInterceptor 或 RequestMappingHandlerAdapter 使用。
 *
 * <h3>使用方式（Spring Boot）：</h3>
 * <pre>{@code
 * @Configuration
 * public class WebMvcConfig implements WebMvcConfigurer {
 *
 *     @Autowired
 *     private EncryptResponseInterceptor encryptInterceptor;
 *
 *     @Override
 *     public void addInterceptors(InterceptorRegistry registry) {
 *         registry.addInterceptor(encryptInterceptor)
 *                 .addPathPatterns("/**");
 *     }
 * }
 * }</pre>
 */
public class EncryptResponseInterceptor {

    /**
     * 拦截响应并加密
     *
     * @param handler       处理器（Controller 方法）
     * @param result        原始返回值
     * @return 加密后的结果，如果不需要加密则返回原结果
     */
    public Object intercept(Object handler, Object result) {
        if (!(handler instanceof HandlerMethod hm)) {
            return result;
        }

        Method method = hm.getMethod();
        EncryptResponse annotation = method.getAnnotation(EncryptResponse.class);

        if (annotation == null) {
            // 方法级别没有注解，检查类级别
            annotation = method.getDeclaringClass().getAnnotation(EncryptResponse.class);
        }

        if (annotation != null) {
            return EncryptResponseAspect.process(method, result, annotation);
        }

        return result;
    }

    /**
     * Spring HandlerMethod 的简化包装（避免引入 Spring 依赖）
     */
    public static class HandlerMethod {
        private final Object bean;
        private final Method method;

        public HandlerMethod(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }

        public Object getBean() {
            return bean;
        }

        public Method getMethod() {
            return method;
        }
    }
}
