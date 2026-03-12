## 项目快速说明（由对话整理）

### 1. 技术栈概览

- **后端**：Java 17 + Spring Boot 3.2.x
  - Spring Web / MVC：页面和接口
  - Spring Data JPA：数据库访问
  - Spring Security：管理员 / 学生登录与权限
- **数据库**：
  - 正式环境：MySQL（数据库名 `campus_food`，账号名建议 `campus_food`）
  - 开发可用 H2（dev profile）
- **前端**：
  - Thymeleaf 模板 + Bootstrap 5（CDN）
  - 自定义 `theme.css`，主色调为红色 + 黄色
- **其他依赖**：
  - Apache POI `poi-ooxml`：导出学生 Excel
  - Validation：参数校验

### 2. 角色与登录方式

- **管理员**
  - 注册地址：`/admin/register`
  - 注册必须输入专用注册密码：`qurvew-dohHyx-mydwy7`
  - 登录：账号 + 密码（密码用 BCrypt 加密）
  - 权限：访问 `/admin/**`
- **学生**
  - 注册地址：`/student/register`，填写姓名、班级、手机号
  - 系统为其**随机生成 4 位小写字母账号名**，保证唯一
  - 登录方式：账号名 + 手机号后四位
  - 权限：访问 `/student/**` 和 点赞 `/foods/{id}/like`

### 3. 核心功能点

- 学生管理（管理员端）
  - 列表、分页、搜索（姓名 / 班级 / 账号 / 手机号）
  - 编辑学生余额
  - 导出 Excel：账号名 + 姓名 + 手机号
- 食品与主页
  - 管理员上传食品图片 + 名称 + 成分
  - 学生端主页展示食品列表，支持点赞，单个学生对同一食品只能点一次
- 公告栏
  - 管理员端编辑公告
  - 学生端首页展示最新一条公告

### 4. 本地运行与打包

#### 4.1 安装 Java 17

- Mac（Apple Silicon）推荐做法：
  1. 访问 Adoptium：`https://adoptium.net/zh-CN/temurin/releases/?version=17&os=mac&arch=aarch64`
  2. 下载 `.pkg` 安装包并安装
  3. 终端执行 `java -version`，确认为 17
- 详细步骤见项目根目录的 `安装Java17.md`。

#### 4.2 使用 Maven Wrapper

项目自带 Maven Wrapper，不必单独安装 Maven，命令都用：

```bash
cd "/Users/mac/食品交易网站"
./mvnw clean package -DskipTests
```

打包成功后生成：

```text
target/campus-food-0.0.1-SNAPSHOT.jar
```

本地运行示例（dev 配置，可用 H2）：

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

浏览器访问：`http://localhost:8080`

#### 4.3 Maven 镜像（解决 SSL / 下载慢）

在 `~/.m2/settings.xml` 中配置阿里云镜像（已在对话中使用）：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              https://maven.apache.org/xsd/settings-1.0.0.xsd">

  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>central</mirrorOf>
      <name>aliyun maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>

</settings>
```

如校园网 / 公司网导致 SSL 握手失败，可以：

- 优先用**手机热点 / 家庭网络 / VPN** 打一次 `./mvnw clean package`，让所有依赖缓存到本地；
- 之后可用离线模式：`./mvnw -o clean package -DskipTests`。

### 5. 上线部署（摘自《部署说明》简版）

> 更完整命令与 systemd / Nginx 示例在项目根目录的 `部署说明.md`。

大致流程：

1. 服务器安装：JDK 17、MySQL。
2. 在 MySQL 创建数据库和用户：
   ```sql
   CREATE DATABASE campus_food CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER 'campus_food'@'localhost' IDENTIFIED BY '你的强密码';
   GRANT ALL PRIVILEGES ON campus_food.* TO 'campus_food'@'localhost';
   FLUSH PRIVILEGES;
   ```
3. 将打好的 `campus-food.jar` 和 `application-prod.yml` 放到 `/opt/campus-food/`。
4. 编辑 `/opt/campus-food/application-prod.yml`：
   - 修改 `spring.datasource.password` 为真实 MySQL 密码；
   - `app.upload-dir` 指向服务器上的绝对路径（例如 `/opt/campus-food/uploads/foods`）。
5. 测试运行：
   ```bash
   cd /opt/campus-food
   java -jar campus-food.jar --spring.profiles.active=prod --spring.config.additional-location=file:/opt/campus-food/application-prod.yml
   ```
6. 需要长期运行可使用 `deploy/campus-food.service`（systemd），和 `deploy/nginx.conf`（Nginx 反向代理）。

### 6. 重要文件速查

- `pom.xml`：依赖与打包配置
- `src/main/java/com/campus/food/`：代码主入口与各模块
- `src/main/resources/templates/`：前端页面（Thymeleaf）
- `src/main/resources/application.yml`：默认配置（开发）
- `src/main/resources/application-prod.yml` / `deploy/application-prod.yml.example`：生产配置模板
- `部署说明.md`：完整上线文档
- `安装Java17.md`：在本机安装 JDK17 的详细说明

