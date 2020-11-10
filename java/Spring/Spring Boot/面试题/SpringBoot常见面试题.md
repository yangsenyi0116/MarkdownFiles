***spring boot知识点：\***

**概述**
1、什么是 Spring Boot？
2、Spring Boot 有哪些优点？
3、Spring Boot 的核心注解是哪个？它主要由哪几个注解组成的？
**配置**
1、什么是 JavaConfig？
2、Spring Boot 自动配置原理是什么？
3、你如何理解 Spring Boot 配置加载顺序？
**什么是 YAML？**
1、YAML 配置的优势在哪里 ?
2、Spring Boot 是否可以使用 XML 配置 ?
3、spring boot 核心配置文件是什么？bootstrap.properties 和 application.properties 有何区别 ?
4、什么是 Spring Profiles？
5、如何在自定义端口上运行 Spring Boot 应用程序？
**安全**
1、如何实现 Spring Boot 应用程序的安全性？
2、比较一下 Spring Security 和 Shiro 各自的优缺点 ?
3、Spring Boot 中如何解决跨域问题 ?
4、什么是 CSRF 攻击？
**监视器**
1、Spring Boot 中的监视器是什么？
2、如何在 Spring Boot 中禁用 Actuator 端点安全性？
3、我们如何监视所有 Spring Boot 微服务？
**整合第三方项目**
1、什么是 WebSockets？
2、什么是 Spring Data ?
3、什么是 Spring Batch？
4、什么是 FreeMarker 模板？
5、如何集成 Spring Boot 和 ActiveMQ？
6、什么是 Apache Kafka？
7、什么是 Swagger？你用 Spring Boot 实现了它吗？
8、前后端分离，如何维护接口文档 ?
**其他**
1、如何重新加载 Spring Boot 上的更改，而无需重新启动服务器？Spring Boot项目如何热部署？
2、您使用了哪些 starter maven 依赖项？
3、Spring Boot 中的 starter 到底是什么 ?
4、spring-boot-starter-parent 有什么用 ?
5、Spring Boot 打成的 jar 和普通的 jar 有什么区别 ?
6、运行 Spring Boot 有哪几种方式？
7、Spring Boot 需要独立的容器运行吗？
8、开启 Spring Boot 特性有哪几种方式？
9、如何使用 Spring Boot 实现异常处理？
10、如何使用 Spring Boot 实现分页和排序？
11、微服务中如何实现 session 共享 ?
12、Spring Boot 中如何实现定时任务 ?

### 具体答案详见：

**1、什么是 Spring Boot？**
Spring Boot 是 Spring 开源组织下的子项目，是 Spring 组件一站式解决方案，主要是简化了使用 Spring 的难度，简省了繁重的配置，提供了各种启动器，开发者能快速上手。

**2、Spring Boot 有哪些优点？**
Spring Boot 主要有如下优点：

容易上手，提升开发效率，为 Spring 开发提供一个更快、更广泛的入门体验。
开箱即用，远离繁琐的配置。
提供了一系列大型项目通用的非业务性功能，例如：内嵌服务器、安全管理、运行数据监控、运行状况检查和外部化配置等。
没有代码生成，也不需要XML配置。
避免大量的 Maven 导入和各种版本冲突。

**3、Spring Boot 的核心注解是哪个？它主要由哪几个注解组成的？**
启动类上面的注解是@SpringBootApplication，它也是 Spring Boot 的核心注解，主要组合包含了以下 3 个注解：

@SpringBootConfiguration：组合了 @Configuration 注解，实现配置文件的功能。

@EnableAutoConfiguration：打开自动配置的功能，也可以关闭某个自动配置的选项，如关闭数据源自动配置功能： @SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })。

@ComponentScan：Spring组件扫描。