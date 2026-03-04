# 农副产品选购平台

## 项目介绍
本平台响应国家优先采购脱贫地区农副产品的号召、助力乡村振兴，针对学校教职工人数多、选购需求差异化大的痛点，实现了教职工在线选购、农户/商家入驻、订单处理及数据实时统计的全流程数字化管理，替代传统人工统计方式，大幅提升选购效率与数据管理精度。

### 核心特点
- **多角色权限管控**：支持教职工、工会管理员、农户/商家三类角色，权限边界清晰
- **全流程数字化**：覆盖商家入驻→商品上架→用户选购→订单处理→数据统计完整闭环
- **高可用性设计**：基于JWT认证、全局异常处理、事务管理保障系统稳定运行
- **易用性优化**：支持分页查询、关键词搜索、批量操作，降低用户使用成本
- **可扩展性强**：模块化设计，支持评价管理、物流跟踪等功能迭代

## 技术栈
### 前端技术
- 框架：Vue 3 + Element Plus
- 路由：Vue Router
- 网络请求：Axios
- 其他：JavaScript / CSS3 / HTML5

### 后端技术
- 核心框架：Spring Boot
- ORM框架：MyBatis-Plus
- 认证授权：JWT
- 数据库：MySQL 8.0
- 其他：Lombok / Hutool / Spring MVC

### 开发&部署工具
- 开发工具：IntelliJ IDEA / VS Code
- 版本控制：Git
- 部署：Nginx（前端）、Tomcat/Spring Boot内置容器（后端）

## 系统架构
### 整体架构
采用前后端分离的B/S架构：
- 前端：负责页面渲染与用户交互，通过Axios调用后端接口
- 后端：提供RESTful API，处理业务逻辑、数据持久化、权限校验
- 数据库：存储用户、商品、订单等核心业务数据

### 核心功能模块
| 模块名称       | 核心功能                                                                 |
|----------------|--------------------------------------------------------------------------|
| 认证授权模块   | 用户登录/注册、JWT令牌生成与校验、角色权限控制                           |
| 商品管理模块   | 商品增删改查、分类展示、详情查看、库存管理                               |
| 购物车模块     | 购物车商品添加/删除、分页查询、批量操作                                   |
| 订单管理模块   | 订单提交/查询/修改/删除、订单状态管理、余额校验                           |
| 用户管理模块   | 系统用户增删改查、个人信息管理、购物金余额管理                           |
| 商家入驻模块   | 入驻申请提交、审核、状态管理、角色自动切换                               |
| 通知管理模块   | 公告发布/修改/删除、公开状态控制                                         |
| 数据统计模块   | 今日/月度营业额/订单数/用户数统计、商品分类分布、数据趋势分析             |
| 系统配置模块   | 跨域配置、拦截器配置、MyBatis-Plus分页配置等系统级基础设置               |

## 快速开始
### 环境要求
- JDK 1.8+
- MySQL 8.0+
- Node.js 16+
- Maven 3.6+

# 1. 克隆代码仓库
git clone https://github.com/Kling2285/RuralFruits.git

# 2. 进入后端项目目录
cd RuralFruits/RuralFruits

# 3. 修改数据库配置（application.properties）
spring.datasource.url=jdbc:mysql://localhost:3306/rural-revitalization?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码

# 4. 编译并启动项目
mvn clean package
java -jar target/RuralFruits-0.0.1-SNAPSHOT.jar

# 5. 进入前端项目目录
cd ../vue-ruralfruits

# 6. 安装依赖
npm install

# 7. 开发环境启动
npm run dev
