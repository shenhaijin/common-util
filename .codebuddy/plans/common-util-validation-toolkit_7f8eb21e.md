---
name: common-util-validation-toolkit
overview: 基于JDK17和Maven构建wooho.common.util工具包，实现参数校验能力，支持自定义异常、国际化和链式调用，不引入第三方依赖
todos:
  - id: init-maven-project
    content: 初始化 Maven 项目结构（pom.xml 配置 JDK17、打包方式、基础目录）
    status: completed
  - id: create-exception
    content: 创建 ValidateException 异常类和 ValidationError 错误记录
    status: completed
    dependencies:
      - init-maven-project
  - id: create-i18n-module
    content: 创建 i18n 国际化模块（MessageSource 接口、ResourceBundle 实现、Locale 上下文、中英文资源文件）
    status: completed
    dependencies:
      - init-maven-project
  - id: create-validator-core
    content: 创建校验器核心模块（ValidationRule 接口、ValidationResult、ValidationContext、Validator 链式调用入口）
    status: completed
    dependencies:
      - create-exception
      - create-i18n-module
  - id: create-validation-rules
    content: 实现所有内置校验规则（NotNull/NotBlank/NotEmpty/Length/Size/Range/Pattern/Email/Custom）
    status: completed
    dependencies:
      - create-validator-core
---

## 产品概述

一个基于 JDK 17 的 Java 工具包 Maven 项目（`wooho.common.util`），零第三方依赖，首期实现参数校验功能模块。后续可在工具包中扩展更多能力。

## 核心功能

- **参数校验引擎**：提供通用的参数值合法性校验能力，支持传入待校验值和一组校验规则列表进行批量/链式校验
- **ValidateException 自定义异常**：当参数校验失败时抛出包含详细错误信息的异常
- **国际化支持（i18n）**：校验错误消息支持多语言切换，通过 Locale 机制实现中英文等多语言错误提示
- **链式调用 API**：校验器支持流畅的链式调用风格（Builder 模式），如 `Validator.of(value).notNull().notBlank().minLength(5).validate()`
- **内置常用校验规则**：包括非空校验、非空白、长度范围、正则匹配、数值范围、集合大小等基础规则

## 技术栈

- **基础环境**：JDK 17 + Maven
- **语言**：Java 17（使用 records、var、sealed classes、switch 表达式等新特性）
- **依赖管理**：纯 JDK 标准库，禁止任何第三方 jar 包
- **构建工具**：Maven

## 实现方案

### 整体架构设计

采用分层架构设计：

- **exception 层**：自定义异常类 `ValidateException`
- **annotation 层**：校验注解定义（可选的声明式校验预留）
- **validator 层**：核心校验引擎，包含校验接口、内置规则实现、校验器入口
- **i18n 层**：国际化消息资源管理

### 核心设计决策

1. **链式校验器设计（Fluent Validator Pattern）**

- 设计一个泛型的 `Validator<T>` 类作为链式调用的核心入口
- 每个校验方法返回 `this` 以支持链式调用
- **关键：链式方法传入消息键(ErrorCode) + 字段名，而非硬编码消息文本**
- 内置规则自动绑定默认消息键（如 `notNull()` → `"validation.notNull"`）
- 支持自定义消息键覆盖默认值：`.notNull("custom.error.code")`
- 运行时根据当前 Locale 从 ResourceBundle 解析实际消息文本
- 最终通过 `validate()` 方法触发实际校验，收集所有错误或快速失败

2. **校验规则抽象（Rule-based Design）**

- 定义 `ValidationRule<T>` 函数式接口，统一校验规则契约
- **每个规则内置默认 errorCode + 支持参数模板（如 `{field}`, `{min}`, `{max}`）**
- 内置常用规则的实现类：NotNullRule, NotBlankRule, LengthRule, RangeRule, PatternRule 等
- 用户可通过实现 `ValidationRule` 接口轻松扩展自定义规则

3. **国际化机制（核心设计）**

- 使用 `java.util.ResourceBundle` 加载 properties 文件（纯 JDK 能力）
- 提供 `MessageSource` 接口封装消息获取逻辑，支持参数占位符替换
- **消息键驱动：代码中只使用错误码，不硬编码任何语言文本**
- 默认提供中文和英文两套错误消息资源文件
- 通过 ThreadLocal (`MessageHolder`) 支持 Locale 动态切换，无需重启
- 扩展新语言只需添加新的 properties 文件，零代码改动

4. **异常设计**

- `ValidateException` 继承 `RuntimeException`
- 包含字段：errorCode（错误码）、message（国际化消息）、fieldName（字段名）、rejectedValue（拒绝值）

### 关键实现细节

- 使用 `List<ValidationError>` 收集校验结果，支持 fail-fast 和 collect-all 两种模式
- **国际化消息模板支持占位符替换（如 `{field}` `{value}` `{min}`）** — 参数由规则自动填充
- 所有校验规则均为无状态对象（Stateless），可安全复用/单例化
- 通过 Service Loader（`java.util.ServiceLoader`）机制支持规则扩展发现

### API 使用示例（修正版）

```java
// ===== 基础用法：消息键驱动，自动国际化 =====
MessageHolder.setLocale(Locale.CHINA);  // 切换中文（运行时动态切换）

Validator.of(username, "用户名")       // value + fieldName
    .notNull()                          // → errorCode="validation.notNull"
    .notBlank()                         // → errorCode="validation.notBlank"  
    .length(3, 20)                      // → errorCode="validation.length", params={min:3, max:20}
    .validate();                        // 校验失败 → 抛 ValidateException

// 切换英文只需改一行配置：
MessageHolder.setLocale(Locale.US);     // 无需修改业务代码、无需重新编译部署！

// ===== 自定义消息键覆盖默认值 =====
Validator.of(email, "邮箱")
    .notNull("user.email.required")     // 使用自定义错误码
    .email()                            // → "validation.email"
    .validate();

// ===== 收集所有错误模式（非快速失败） =====
ValidationResult result = Validator.of(value, "字段")
    .failFast(false)                    // 收集全部错误而非遇到第一个就停止
    .notNull()
    .length(1, 10)
    .validateResult();                  // 返回结果而非抛异常
if (!result.isValid()) {
    result.getErrors().forEach(e -> System.out.println(e.getMessage()));
}
```

### 资源文件示例

```
# validation_messages_zh_CN.properties (中文)
validation.notNull={field}不能为空
validation.notBlank={field}不能为空白
validation.length={field}长度必须在{min}到{max}个字符之间
validation.email={field}格式不正确

# validation_messages_en_US.properties (英文)  
validation.notNull={field} must not be null
validation.notBlank={field} must not be blank
validation.length={field} length must be between {min} and {max} characters
validation.email={field} format is invalid
```

## 目录结构

```
d:\codeBuddy\common-util\
├── pom.xml                                          # [NEW] Maven 项目配置文件，JDK17，无第三方依赖
├── src/
│   ├── main/
│   │   └── java/
│   │       └── wooho/common/util/
│   │           ├── CommonUtil.java                  # [NEW] 工具包主入口/版本信息
│   │           ├── exception/
│   │           │   └── ValidateException.java       # [NEW] 校验异常类，含错误码/消息/字段名/拒绝值
│   │           ├── validator/
│   │           │   ├── ValidationRule.java          # [NEW] 校验规则函数式接口
│   │           │   ├── ValidationError.java         # [NEW] 校验错误记录（record）
│   │           │   ├── ValidationResult.java        # [NEW] 校验结果（record），包含错误列表
│   │           │   ├── Validator.java               # [NEW] 链式校验器核心入口（泛型<T>）
│   │           │   ├── ValidationContext.java       # [NEW] 校验上下文（Locale、fail-fast模式等）
│   │           │   └── rules/
│   │           │       ├── NotNullRule.java         # [NEW] 非空校验规则
│   │           │       ├── NotBlankRule.java        # [NEW] 非空白字符串校验规则
│   │           │       ├── NotEmptyRule.java        # [NEW] 非空（集合/字符串/数组）校验规则
│   │           │       ├── LengthRule.java          # [NEW] 字符串长度范围校验规则
│   │           │       ├── SizeRule.java            # [NEW] 集合/数组/Map大小范围校验规则
│   │           │       ├── RangeRule.java           # [NEW] 数值范围校验规则（Comparable）
│   │           │       ├── PatternRule.java         # [NEW] 正则表达式匹配校验规则
│   │           │       ├── EmailRule.java           # [NEW] 邮箱格式校验规则
│   │           │       └── CustomRule.java          # [NEW] 自定义 Predicate 规则适配器
│   │           └── i18n/
│   │               ├── MessageSource.java           # [NEW] 国际化消息源接口
│   │               ├── ResourceBundleMessageSource.java  # [NEW] 基于ResourceBundle的消息源实现
│   │               └── MessageHolder.java           # [NEW] 线程安全的Locale上下文持有者
│   └── resources/
│       └── wooho/common/util/i18n/
│           ├── validation_messages.properties        # [NEW] 英文默认错误消息
│           └── validation_messages_zh_CN.properties # [NEW] 中文错误消息
```