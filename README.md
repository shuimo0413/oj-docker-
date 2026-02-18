# 校园OJ平台

一个基于 Spring Boot + Vue 3 的校园在线判题（OJ）平台，支持多语言代码提交、自动判题、题目管理等功能。

## 技术栈

### 后端
- **Spring Boot 2.6.13** - Java 应用框架
- **MyBatis** - 持久层框架
- **MySQL 8.0** - 关系型数据库
- **Redis** - 缓存和会话管理
- **Docker Java** - Docker 容器管理（用于代码沙箱）

### 前端
- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 前端构建工具
- **Element Plus** - Vue 3 组件库
- **CodeMirror** - 代码编辑器
- **Axios** - HTTP 请求库

## 功能特性

### 用户功能
- 用户注册/登录
- 题目列表浏览（支持搜索和分页）
- 题目详情查看
- 在线代码编辑（支持 Java、Python、C++、JavaScript）
- 代码提交和自动判题
- 判题结果查看（通过/失败、耗时、内存占用）
- 个人中心（查看信息、修改密码）

### 管理员功能
- 题目管理（添加、编辑、删除）
- 测试点管理
- 题目搜索和分页

### Docker判题沙箱
- 安全隔离：每个判题请求在独立的Docker容器中运行
- 资源限制：内存、CPU、执行时间限制
- 网络隔离：禁用容器网络访问
- 文件系统隔离：使用只读根文件系统和tmpfs
- 权限控制：非root用户运行，移除所有capabilities
- 多语言支持：Java、Python、C++

## 项目结构

```
oj/
├── src/main/
│   ├── java/oj/
│   │   ├── config/          # 配置类
│   │   ├── controller/      # 控制器
│   │   ├── interceptor/     # 拦截器
│   │   ├── mapper/          # MyBatis Mapper
│   │   ├── pojo/            # 数据对象
│   │   │   ├── dto/         # 数据传输对象
│   │   │   ├── vo/          # 视图对象
│   │   │   └── enums/       # 枚举类
│   │   ├── service/         # 业务逻辑
│   │   └── util/            # 工具类
│   └── resources/
│       ├── mapper/          # MyBatis XML
│       ├── static/          # 静态资源
│       └── application.yml  # 配置文件
├── sandbox/                 # 判题沙箱服务
│   ├── judge-service/       # 判题服务
│   │   ├── src/
│   │   │   └── main/java/oj/sandbox/
│   │   │       ├── controller/    # 判题控制器
│   │   │       ├── model/         # 判题模型
│   │   │       └── service/       # 判题服务
│   │   ├── Dockerfile
│   │   └── pom.xml
│   └── sandbox-image/       # 沙箱镜像
│       └── Dockerfile
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/            # API 接口
│   │   ├── layout/         # 布局组件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理
│   │   ├── utils/          # 工具函数
│   │   └── views/          # 页面组件
│   ├── package.json
│   └── vite.config.js
├── docker/
│   └── Dockerfile          # 后端 Dockerfile
├── docker-compose.yml      # Docker Compose 配置
├── deploy.sh               # Linux/Mac 部署脚本
├── deploy.bat              # Windows 部署脚本
├── DEPLOYMENT.md           # 详细部署文档
└── pom.xml                 # Maven 配置
```

## 快速开始

### 方式一：Docker Compose（推荐）

1. 克隆项目
```bash
git clone <repository-url>
cd oj
```

2. 使用快速启动脚本
```bash
# Linux/Mac
chmod +x deploy.sh
./deploy.sh

# Windows
deploy.bat
```

或者手动启动所有服务
```bash
docker-compose up -d --build
```

3. 访问应用
- 前端：http://localhost
- 后端 API：http://localhost:8080
- 判题服务：http://localhost:8081

### 方式二：本地开发

#### 后端启动

1. 配置数据库
   - 安装 MySQL 8.0
   - 创建数据库 `oj`
   - 修改 `src/main/resources/application.yml` 中的数据库配置

2. 配置 Redis
   - 安装 Redis
   - 修改 `src/main/resources/application.yml` 中的 Redis 配置

3. 配置判题服务
   - 修改 `src/main/resources/application.yml` 中的判题服务地址
   ```yaml
   judge:
     service:
       url: http://localhost:8081
   ```

4. 启动后端服务
```bash
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

#### 前端启动

1. 安装依赖
```bash
cd frontend
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

前端服务将在 `http://localhost:5173` 启动。

## API 接口文档

### 用户接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/user/login` | POST | 用户登录 |
| `/api/user/register` | POST | 用户注册 |
| `/api/user/logout` | POST | 用户登出 |
| `/api/user/changePassword` | POST | 修改密码 |
| `/api/user/info` | PUT | 更新用户信息 |

### 题目接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/user/testQuestion/getTestQuestionByPage` | GET | 分页获取题目列表 |
| `/api/user/testQuestion/getTestQuestionById` | GET | 根据ID获取题目 |
| `/api/user/testQuestion/submitTestQuestion` | POST | 提交代码 |
| `/api/user/testQuestion/getTestPointsListByQuestionId` | GET | 获取测试点 |
| `/api/user/testQuestion/getTestQuestionByName` | GET | 搜索题目 |

### 管理员接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/testQuestion/getTestQuestionByPage` | GET | 分页获取题目列表 |
| `/api/testQuestion/addTestQuestion` | POST | 添加题目 |
| `/api/testQuestion/updateTestQuestion` | POST | 更新题目 |
| `/api/testQuestion/deleteTestQuestionById` | DELETE | 删除题目 |

## 配置说明

### 后端配置（application.yml）

```yaml
server:
  port: 8080

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

### 前端配置（vite.config.js）

```javascript
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## Docker 部署

### 后端 Dockerfile

后端 Dockerfile 位于 `docker/Dockerfile`，使用多阶段构建优化镜像大小。

### 前端 Dockerfile

前端 Dockerfile 位于 `frontend/Dockerfile`，使用 Nginx 部署静态文件。

### Docker Compose

使用 `docker-compose.yml` 可以一键启动所有服务，包括：
- 后端服务（Spring Boot）
- 前端服务（Nginx）
- MySQL 数据库
- Redis 缓存

## 开发指南

### 添加新的编程语言支持

1. 后端：在 `LocalCodeRunner.java` 中添加新的语言配置
2. 前端：在 `QuestionDetail.vue` 中的 `languageExtensions` 和 `defaultCode` 添加相应配置

### 添加新的题目

1. 以管理员身份登录
2. 进入"题目管理"页面
3. 点击"添加题目"按钮
4. 填写题目信息并添加测试点
5. 点击"确定"保存

## 注意事项

1. 确保 Docker 已安装并运行
2. 首次启动需要等待数据库初始化
3. 默认管理员账号需要在数据库中手动创建
4. 代码判题使用 Docker 容器隔离，确保 Docker 服务正常运行

## 许可证

MIT

## 贡献

欢迎提交 Issue 和 Pull Request！
