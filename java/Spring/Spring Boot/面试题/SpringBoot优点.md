- 快速创建独立运行的Spring项目以及主流框架集成
- 使用嵌入式的Servlet容器，应用无需打成War包
- starters自动依赖与版本控制
- 大量的自动配置，简化开发，也可修改默认配置
- 无需配置XML，无代码生成，开箱即用
- 准生产环境的运行时应用监控
- 与云计算的天然集成

# 1.Spring Boot 入门

------



## 1. Spring boot介绍

> 简化spring应用开发的一个框架
>
> 整个spring技术栈的一个大整合
>
> J2EE开发的一站式解决方案

###  优点

> - 快速创建独立运行的Spring项目以及主流框架集成
> - 使用嵌入式的Servlet容器，应用无需打成War包
> - starters自动依赖与版本控制
> - 大量的自动配置，简化开发，也可修改默认配置
> - 无需配置XML，无代码生成，开箱即用
> - 准生产环境的运行时应用监控
> - 与云计算的天然集成

## 2.微服务

> 2014，martin fowel
>
> 微服务，架构风格
>
> 一个应用应该是一组小型服务，可以通过HTTP的方式进行交互

单体应用：ALL IN ONE

每一个功能元素最终都是一个可独立替换和独立升级的软件单元



## 3.环境准备

### maven设置

```xml
<profile>
  <id>jdk-1.8</id>
  <activation>
    <activeByDefault>true</activeByDefault>
    <jdk>1.8</jdk>
  </activation>
  <properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
  </properties>
</profile>
```
# 2.spring boot Hello World

------

一个功能

浏览器发送Hello请求，服务器接受请求并处理，响应



## 1.创建一个maven工程

## 2.导入spring boot相关的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>1.5.9.RELEASE</version>
</dependency>
```

## 3.编写一个主程序：启动springboot应用

```java
/**
 * @SpringBootApplication来标注一个主程序类，说明这是一个spring boot应用
 */
@SpringBootApplication
public class HelloWorldMainApplication {

    public static void main(String[] args) {
        //spring应用启动起来
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }

}
```

## 4.编写响应的Controller和service

```java
@Controller
public class HelloController {

    @ResponseBody
    @RequestMapping("/hello")
    public String Hello(){
        return "Hello World";
    }
}
```

## 5.运行主程序测试

## 6.简化部署

```xml
<!--这个插件，可以将应用打包成一个可执行的jar包-->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

将这个应用打包成jar包，利用java -jar的命令运行



#  HelloWorld探究

-------

## 1.POM.xml

### 1.父项目

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.4.RELEASE</version>
</parent>
```

他的父项目是

```xml
<parent>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-dependencies</artifactId>
<version>2.1.4.RELEASE</version>
<relativePath>../../spring-boot-dependencies</relativePath>
</parent>
```

他是真正来管理springboot版本的依赖中心

以后我们导入依赖是不需要写版本；（没有在dependencies里面管理的依赖需要写版本号）

## 2.导入的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.1.4.RELEASE</version>
</dependency>
```

### spring-boot-starter-web

​	spring-boot-starter:spring-boot场景启动器；帮我们导入了web模块正常运行所依赖的组件

Spring boot将所有的功能场景都抽取出来，做成一个个的starter（启动器），只需要在项目里面引入这些starter相关场景的所有依赖都会被导进来。要用什么功能就导入什么starter

## 3.主程序类，主入口类

```java
/**
 * @SpringBootApplication来标注一个主程序类，说明这是一个spring boot应用
 */
@SpringBootApplication
public class HelloWorldMainApplication {

    public static void main(String[] args) {
        //spring应用启动起来
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }

}
```

@SpringBootApplication: spring Boot应用标注在某个类上说明这个类是SpringBoot的主配置类，SpringBoot就应该运行这个类的main方法来启动SpringBoot应用



```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
```

@SpringBootConfiguration:SpringBoot的配置类

​	标注在某个类上，表示这是一个SpringBoot的配置类

​	@Configuration：配置类上来标志这个注解

​		配置类 ----- 配置文件：配置类也是容器中的一个组件

@EnableAutoConfiguration：开启自动配置功能

​	以前我们需要配置的东西，spring boot帮我们自动配置

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import({AutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
```

@AutoConfigurationPackage：自动配置包

​	@Import({AutoConfigurationImportSelector.class})

​	Spring的底层注解。给容器中导入一个组件；导入的组件由AutoConfigurationPackage.Register,class

​	将主配置类(@SpringBootApplication标注的类)的所在包及下边所有子包的所有组件扫描到Spring容器

@Import({AutoConfigurationImportSelector.class})

​	EnableAutoConfigurationImportSelector.class:导入哪些组件的选择器

​	给容器中导入组件



# 使用spring initializer快速创建Spring Boot项目

-------

![1554477636333](C:\Users\vip87\AppData\Roaming\Typora\typora-user-images\1554477636333.png)

![1554477648848](C:\Users\vip87\AppData\Roaming\Typora\typora-user-images\1554477648848.png)

![1554477655508](C:\Users\vip87\AppData\Roaming\Typora\typora-user-images\1554477655508.png)

默认生成的spring boot项目

- 主程序已经生成，我们只需要编写自己的业务逻辑
- resources文件夹的目录结构
  - static----静态资源 js css image
  - templates 保存了所有的模板页面（Spring Boot默认jar包使用嵌入式的Tomcat，默认不支持jsp页面）；可以使用模板引擎(freemaker,thymeleaf)
  - application.properties：Spring Boot应用的配置文件，可以修改一些默认匹配值



# 3.配置文件

-----

## 1.配置文件

SpringBoot使用一个全局的配置文件,配置名是固定的

- application.properties
- application.yml

配置文件的作用：修改SpringBoot自动配置的默认值

SpringBoot在底层都给我们配置好了

## 2.YAML

yaml是以数据为中心，比Json，xml更适合做配置文件

```yml
server:
  port: 8080
```

	### yaml基本语法

K:空格V：表示一对键值对（空格必须有）：

以空格的缩进来控制层级关系，

只要是左对齐的一列数据，都是同一个层级的

属性和值都是大小写敏感的

#### 1.字面量：普通的值（数字·字符串·布尔）

字符串默认不用加上单引号或者双引号

#### 2.对象、Map（属性和值）（键值对）

```yml
friends:
	lastName: Jack
	age: 20
```

行内写法

```yml
friends: {lastName: Jack,age: 20}
```

#### 3.数组(List,Set)

```yaml
pets:
 - cat
 - dog
 - pig
```

行内写法

```yaml
pets: [cat,dog,pig]
```



## 将属性绑定到类中

添加依赖

```xml
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
</dependency>
```



```java
@ConfigurationProperties(prefix = "person")
public class Persion{
    private String LastName;
    private Integer age;
}
```

## @Value获取值和@ConfigurationProperties获取值比较

|                      | @ConfigurationProperties | @Value     |
| -------------------- | ------------------------ | ---------- |
| 功能                 | 批量注入文件中的属性     | 一个个指定 |
| 松散绑定（松散语法） | 支持                     | 不支持     |
| SpEL                 | 不支持                   | 支持       |
| JSR303数据校验       | 支持                     | 不支持     |
| 复杂类型封装         | 支持                     | 不支持     |

如果我们只是在某个业务逻辑中需要获取一下配置文件中的某项值，使用@Value

## @PropertySource&@ImportResource

@PropertySource:加载指定的配置文件

```java
@PropertySource(value = {"classpath:*.properties"})
```

@ImportResource：导入spring的配置文件，让配置文件里面的内容生效

不推荐使用  推荐使用全注解方式

用Bean类来创建bean对象

# 4.Profile多环境控制

-----

## 1.多Profile

我们在主配置文件编写的时候，文件名可以是：

application-{profile}.properties/yml

默认使用application.properties

## 2.yml支持多文档块方式

```yaml
server:
  port: 8080
spring:
  profiles:
    active: dev

----
server:
  port: 8081
spring:
  profiles: dev


----
server:
  port: 8082
spring:
  profiles: prod
```

## 3.激活指定profile

### 1.配置文件指定

```properties
spring.profiles.active=dev
```

### 2.命令行

```cmd
--spring.profiles.active=dev
```

### 3.虚拟机参数

-Dsping.profiles.active=dev



# 5.配置文件加载位置

----



![1554562040409](C:\Users\vip87\AppData\Roaming\Typora\typora-user-images\1554562040409.png)



![1554562482528](E:\课程文件\markdown\image\%5CUsers%5Cvip87%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1554562482528.png)

# 6.外部部配置加载顺序

----

SpringBoot也可以从以下位置加载配置；优先级从高到低；高优先级的配置覆盖低优先级的配置，所有的配置会形成互补配置

1. 命令行参数

2. 来自java:comp/env的JNDI属性

3. Java系统属性（System.getProperties（））

4. 操作系统环境变量

5. RandomValuePropertySource配置的random.*属性值

   ![1554562280201](C:\Users\vip87\AppData\Roaming\Typora\typora-user-images\1554562280201.png)

![1554562101034](E:\课程文件\markdown\image\1554562101034.png)



