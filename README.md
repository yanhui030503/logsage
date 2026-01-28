# LogSage

登录后分析 Java/Spring 日志，输出结构化排障报告。

## 简介

LogSage 是一个日志分析工具，帮助开发者快速定位和解决 Java/Spring 应用中的问题。通过 AI 分析日志内容，生成结构化的排障报告，包括问题摘要、可能原因、验证步骤和修复建议。

> **当前状态**: MVP + 持续迭代中

## Features

### ✅ 已实现

- **用户认证**: 登录/注册功能，基于 Spring Security
- **每日配额**: 每用户每天最多分析 20 次（可配置）
- **智能缓存**: 相同日志内容自动使用缓存结果，节省配额
- **自动脱敏**: 自动识别并脱敏日志中的敏感信息（IP、邮箱、密码等）
- **分析页面**: 支持 Java/Spring 日志分析，生成结构化报告
- **历史记录**: 查看所有历史分析记录
- **报告详情**: 查看完整的分析报告详情
- **卡片式 UI**: 美观的分析结果展示，包括：
  - TL;DR 摘要卡片
  - Top Causes 卡片（带置信度进度条）
  - 可勾选验证步骤清单（状态保存到本地）
  - 一键复制命令按钮

### 🚧 计划中

- **接入真实 LLM**: 替换当前的模拟 AI 客户端，接入真实的 LLM API
- **Markdown/PDF 导出**: 支持将分析报告导出为 Markdown 或 PDF 格式
- **相似案例检索**: 根据当前日志检索相似的历史案例
- **标签筛选**: 为分析报告添加标签，支持按标签筛选和搜索
- **等等

## Tech Stack

- **后端框架**: Spring Boot 4.0.1
- **Java 版本**: 17
- **安全框架**: Spring Security
- **数据持久化**: Spring Data JPA
- **数据库**: H2 (内存数据库，开发环境)
- **模板引擎**: Thymeleaf
- **JSON 处理**: Jackson
- **前端**: 原生 HTML/CSS/JavaScript（无前端框架）

## Quick Start

### 前置要求

- Java 17 或更高版本
- Maven 3.6+（或使用项目自带的 Maven Wrapper）

### 运行步骤

1. **克隆项目**（如果从 Git 仓库）
   ```bash
   git clone <repository-url>
   cd demo
   ```

2. **启动应用**
   ```bash
   ./mvnw spring-boot:run
   ```
   或使用系统 Maven：
   ```bash
   mvn spring-boot:run
   ```

3. **访问应用**
   - 应用地址: http://localhost:8080
   - 首次访问会自动跳转到登录页面

4. **注册账号**
   - 访问注册页面创建新账号
   - 或直接访问: http://localhost:8080/register

5. **H2 数据库控制台**（可选）
   - 访问地址: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:logsage`
   - 用户名: `sa`
   - 密码: （留空）

### 配置说明

主要配置在 `src/main/resources/application.properties`：

- `logsage.daily.limit`: 每日分析配额（默认 20 次）

## Screenshots

> 📸 截图占位 - 待补充

- 登录页面
- 分析页面
- 分析结果卡片展示
- 历史记录页面
- 报告详情页面

## 项目结构

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/          # 配置类（SecurityConfig）
│   │   ├── controller/      # 控制器（Auth, LogAnalysis, Home）
│   │   ├── model/           # 数据模型（User, LogAnalysis, 等）
│   │   ├── repository/      # 数据访问层
│   │   └── service/         # 业务逻辑层
│   └── resources/
│       ├── templates/       # Thymeleaf 模板
│       ├── static/css/     # 样式文件
│       └── application.properties
└── test/                    # 测试代码
```

## License

MIT License（可选）

---

**注意**: 当前版本使用模拟 AI 客户端（`FakeAiClient`），生成的分析结果仅用于演示。生产环境需要接入真实的 LLM API。
