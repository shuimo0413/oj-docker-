# OJ 在线判题系统

一个基于 Spring Boot 的在线判题（Online Judge）平台，支持多语言代码提交与自动判题。

## 技术栈

- **Java 17**
- **Spring Boot 2.6.13**
- **MyBatis Plus 3.5.3.1**
- **MySQL 8.0**
- **Redis**
- **Docker** (代码沙箱运行环境)
- **Spring Cloud Alibaba Sentinel**

## 支持的编程语言

| 语言 | 语言ID |
|------|--------|
| C | 1 |
| C++ | 2 |
| Java | 4 |
| Python | 10 |
| C# | 22 |

## 项目结构

```
oj/
├── src/main/java/oj/
│   ├── config/              # 配置类
│   │   ├── AsyncConfig.java
│   │   ├── CorsConfig.java
│   │   ├── Judge0Config.java
│   │   ├── RedisConfig.java
│   │   └── WebConfig.java
│   ├── constant/            # 常量与数据对象
│   │   ├── dto/             # 数据传输对象
│   │   ├── entity/          # 实体类
│   │   ├── pojo/            # POJO类
│   │   ├── rpc/             # RPC请求响应对象
│   │   └── vo/              # 视图对象
│   ├── controller/          # 控制器
│   │   ├── AdminTestQuestionController.java
│   │   ├── CommentController.java
│   │   ├── UserController.java
│   │   └── UserTestQuestionController.java
│   ├── interceptor/         # 拦截器
│   ├── mapper/              # MyBatis Mapper接口
│   ├── service/             # 业务逻辑层
│   └── util/                # 工具类
├── src/main/resources/
│   ├── mapper/              # MyBatis XML映射文件
│   ├── application.yml      # 主配置文件
│   └── application-docker.yml
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
└── pom.xml
```

## 功能模块

### 用户模块 (`/api/user`)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/login` | POST | 用户登录 |
| `/register` | POST | 用户注册 |
| `/logout` | POST | 用户登出 |
| `/changePassword` | POST | 修改密码 |
| `/info` | PUT | 更新用户信息 |
| `/status` | GET | 获取用户状态 |

### 题目模块 - 用户端 (`/api/user/testQuestion`)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/getTestQuestionByPage` | GET | 分页获取题目列表 |
| `/getTestQuestionById` | GET | 根据ID获取题目详情 |
| `/submitTestQuestion` | POST | 提交代码判题 |
| `/getTestQuestionByName` | GET | 按名称搜索题目 |
| `/getTestPointsListByQuestionId` | GET | 获取题目测试点 |

### 题目模块 - 管理端 (`/api/testQuestion`)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/getTestQuestionByPage` | GET | 分页获取题目列表 |
| `/getTestQuestionCount` | GET | 获取题目总数 |
| `/getTestQuestionById` | GET | 根据ID获取题目 |
| `/addTestQuestion` | POST | 添加新题目 |
| `/updateTestQuestion` | POST | 更新题目 |
| `/deleteTestQuestionById` | DELETE | 删除题目 |
| `/getTestPointsListByQuestionId` | GET | 获取测试点列表 |

### 评论模块 (`/api/comment`)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/addComment` | POST | 添加评论 |
| `/addCommentLike` | POST | 点赞评论 |
| `/getComment` | GET | 获取单条评论 |
| `/getComments` | GET | 分页获取题目评论 |
| `/deleteComment` | DELETE | 删除评论 |
| `/cancelCommentLike` | DELETE | 取消点赞 |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 本地开发

1. **克隆项目**
```bash
git clone <repository-url>
cd oj
```

2. **创建数据库**
```sql
CREATE DATABASE oj;
```

3. **修改配置文件**

编辑 `src/main/resources/application.yml`，修改数据库和Redis连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/oj
    username: root
    password: your_password
  redis:
    host: 127.0.0.1
    port: 6379
    password: your_redis_password
```

4. **启动应用**
```bash
mvn spring-boot:run
```

服务将在 `http://localhost:8080` 启动。

### Docker 部署

1. **进入docker目录**
```bash
cd docker
```

2. **启动所有服务**
```bash
docker-compose up -d --build
```

这将启动以下服务：
- **MySQL** - 端口 3306
- **Redis** - 端口 6379
- **OJ应用** - 端口 8080

## 配置说明

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/oj
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Redis配置

```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password: 123456
    database: 0
    timeout: 3000ms
```

### MyBatis Plus配置

```yaml
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: oj.pojo.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 核心功能说明

### 代码判题

系统通过 Docker 容器实现代码的安全隔离执行：
- 每个判题请求在独立容器中运行
- 支持资源限制（内存、CPU、执行时间）
- 网络隔离确保安全

### 用户认证

- 使用 Redis 存储用户会话 Token
- 通过 TokenInterceptor 拦截器验证请求

### 异步处理

- 通过 AsyncConfig 配置异步任务执行器
- 支持判题任务的异步执行

## 开发指南

### 添加新的编程语言

在 `LanguageConstants.java` 中添加新的语言常量：

```java
public static final Integer LANG_NEW = 30;

static {
    LANGUAGE_NAME_TO_ID.put("NewLanguage", LANG_NEW);
}
```

### 数据库表结构

主要数据表：
- `user` - 用户信息
- `questions` - 题目信息
- `test_point` - 测试点
- `user_submission_code` - 用户提交代码
- `user_submission_record` - 提交记录
- `comment` - 评论
- `comment_like` - 评论点赞

## 许可证

MIT License
