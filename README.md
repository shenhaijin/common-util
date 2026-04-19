# common-util

通用工具库，提供参数校验、响应加密、国际化等常用功能。

## 模块说明

```
wooho.common.util/
├── api/response/encrypt/     # 接口响应加密工具
│   ├── annotation/           # 注解定义
│   ├── EncryptKeyManager     # 密钥管理器
│   ├── Encryptor             # AES 加密工具
│   ├── EncryptResult         # 加密响应封装
│   └── ...
├── i18n/                     # 国际化工具
│   ├── MessageHolder        # 线程本地 Locale 管理
│   ├── MessageSource        # 消息源接口
│   └── ResourceBundleMessageSource
├── validate/                # 参数校验工具
│   ├── Validator            # 校验器
│   ├── ValidationContext    # 校验上下文
│   ├── ValidationRule       # 校验规则接口
│   ├── exception/           # 校验异常
│   └── rules/               # 内置校验规则
│       ├── NotNullRule
│       ├── NotBlankRule
│       ├── LengthRule
│       ├── EmailRule
│       └── ...
└── pom.xml
```

## 快速开始

### 1. 安装到本地 Maven 仓库

```bash
mvn clean install
```

### 2. 在项目中引入依赖

```xml
<dependency>
    <groupId>wooho.common</groupId>
    <artifactId>util</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 功能使用

### 参数校验

```java
ValidationResult result = Validator.of("用户名")
    .notNull()
    .notBlank()
    .length(3, 20)
    .validate("John");

if (!result.isValid()) {
    System.out.println(result.getErrors());
}
```

### 响应加密

```java
// 1. 初始化密钥（应用启动时）
EncryptKeyManager.setKey("your-32-byte-base64-key");

// 2. Controller 方法上加注解
@EncryptResponse
@GetMapping("/user/info")
public User getUserInfo() {
    return userService.getUserInfo();
}
```

### 国际化

```java
// 设置当前线程的 Locale
MessageHolder.setLocale(Locale.US);

// 获取国际化消息
String message = MessageHolder.getMessage("user.notFound", "id");
```

## License

MIT
