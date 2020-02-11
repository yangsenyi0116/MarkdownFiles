### RestTemplate去获取其他服务端的数据

```java
restTemplate.getForObject(url + id, User.class);
```
将json数据获取到，并且转换成User对象

### Eureka

1. 创建EurekaServer

   ```
   @EnableEurekaServer//将当前项目标记为Eurekaserver
   @EnableEurekaClient//标记为Eurekaclient去寻找Eureka服务端
   ```

```yaml
server:
  port: 9800
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://user:123@localhost:9800/eureka
#      defaultZone: http://localhost:9800/eureka
#因为当前的Eureka是单机的，所以需要做一些配置
debug: true
spring:
  devtools:
    restart:
      enabled: true  #设置开启热部署、

#开启安全配置，也就是需要密码，如果不需要则设为false
security:
  basic:
    enabled: true
  user:
    name: user
    password: 123
    #在配置了用户名和密码后我们可以修改地址的访问风格为 CURL 风格
#注意，这个参数必须放在application.yml中，不允许放在bootstarp.yml中
```

### Ribbon

#### Client 负载均衡

```java
@RibbonClient("PROVIDER-USER")//启用ribbon 并对PROVIDER-USER 进行负载均衡
```

​	Controller中修改

```java
 restTemplate.getForObject(  "http://PROVIDER-USER/user/" + id, User.class);
```

```java
@RibbonClient(name = "PROVIDER-USER",configuration = TestConfig.class)
```

```yaml
eureka:
	instance:
		prefer-ip-address: true #在EUREKA中显示ip
```



#### 负载均衡的配置类，默认不能和springboot的启动类同级，必须为高一级的包中

```java
@Configuration
public class TestConfig {

    @Autowired
    IClientConfig clientConfig;

    /**
     * 创建负载均衡算法的方法
     * @param config
     * @return
     */
    @Bean
    public IRule ribbonRule(IClientConfig config){
        return new RandomRule();
    }
}
```



#### 如果非要放在同级的话，在启动类中加上

```java
@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,value = ExcludeCommentScan.class)})
```

并且建立不扫描加上注解的@ExcludeCommentScan类

```java
public @interface ExcludeCommentScan {
}
```



#### 用配置文件的方式

```yaml
PROVIDER-USER: #写服务的名称
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.ladbalancer.RandomRule #给指定的服务配置负载均衡方法
ribbon:
  eureka:
    enabled: false #在Eureka中禁用ribbon，禁用后需要自己配置负载均衡
```



```java
@RibbonClient("PROVIDER-USER")//启用ribbon 并对PROVIDER-USER 进行负载均衡


    @Autowired
    private LoadBalancerClient loadBalancerClient;

@GetMapping("/test")
    public String test(){
        ServiceInstance serviceInstance = loadBalancerClient.choose("PROVIDER-USER");
        System.err.println("1111" + serviceInstance.getServiceId() + serviceInstance.getHost() + serviceInstance.getPort());
        return "1";
    }
```

### EurekaServer的高可用

![1560150914751](G:\onedriver\OneDrive\MarkDown\image\1560150914751.png)

![1560150930721](G:\onedriver\OneDrive\MarkDown\image\1560150930721.png)

>该两项是把Eurekaserver开启为单机模式

```yaml
spring
  application
    name EUREKA-HA
  profiles
    active peer1
server
  port 8761

eureka
  instance
    hostname peer1
  client

    serviceUrl
      defaultZone httppeer28762eureka,httppeer38763eureka
```

配置多个application-xxx.yml文件

在program arguments加上配置模式

```
--spring.profiles.active=peer2
```

为了保证高可用，不推荐使用1->2->3->1

如果一个Eureka启动失败，则后边的全部启动失败

### Feign

#### 导入依赖

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-openfeign</artifactId>
	<version>1.4.3.RELEASE</version>
</dependency>
```

##### 启动类上加上注解@EnableFeignClients



#### 编写接口

```java

@FeignClient("provider-user")
public interface FeignClient01 {
    //主要的功能是用来取代resttemplate
    
    @GetMapping("/user/{id}")
    User getOrder(Long id);
}
```



#### Controller修改

```java
@FeignClient("provider-user")
public interface FeignClient01 {
    //主要的功能是用来取代resttemplate
	@GetMapping("/user/{id}")
    //@RequestMapping(value = "/user/{id}",method = RequestMethod.GET)
    User getOrder(@PathVariable Long id);
}
```



#### provider中加入方法

```java
    @GetMapping("/get-user")
    public User getUser(User user){
           return user; //相当于我们传入了一个复杂参数，会被封装层user对象，然后我们将封装的对象返回，也就是传递什么返回什么
```

#### feign自定义配置

```java
@Configuration
public class FeignClient01Config {
    @Bean
    public Contract feignContract(){
        return new feign.Contract.Default();//默认契约，默认是springmvc的
        
    }
}
```

放在启动类上一级的包中



并在feign的接口类上加上注解

```java
@FeignClient(name = "provider-user",configuration = FeignClient01Config.class)
public interface FeignClient01 {
    //主要的功能是用来取代resttemplate

    @RequestLine("GET /user/{id}")//组合注解，第一个是请求方式，第二个是请求地址，用空格隔开
    User getOrder(@Param("id") Long id);//使用RequestLine的时候要用Param注解

    @GetMapping("/get-user")//无法访问，提供者那边必须是post方式这边才可以使用，如果非要使用get传递多个数据，只能以普通方式传递，不能使用复杂对象
    User get_user(User user);   //如果你传递的是复杂参数，那么feign不论你配置的是什么方式，都会以post方式发送出去
}
```



指定访问哪个url下的一个方法

```java
@FeignClient(name = "provider-user",url = "http://localhost:9800/",configuration = FeignClient02Config.class)
public interface FeignClient02 {
    @RequestMapping("/eureka/apps/{servicename}")
    public String getservice(@PathVariable("servicename") String servicename);
}
```

如果有springsecurity则添加配置

```java
@Configuration
public class FeignClient02Config {

    //用于创建用户名和密码的对象、
    @Bean
    public BasicAuthenticationInterceptor basicAuthenticationInterceptor(){
        return new BasicAuthenticationInterceptor("user","123");
    }

}
```





给指定的feign添加日志设置

```java
@Configuration
public class FeignClient01Config {
    @Bean
    public Contract feignContract(){
        return new feign.Contract.Default();//默认契约，默认是springmvc的

    }

    /**
     * 配置要输出的日志有哪些，必须在debug模式下在可以输出
     */
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}
```

```yaml
logging:
  level:
    com.kermi.springcloud.microservicedemo.consumerorder.feign.FeignClient02: debug #给指定的的feign设置入职输出级别，只有在debug的情况下才会答应日志
```

#### Feign超时设置



### Hystrix（断路器）

#### 添加断路器依赖

```xml
<!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-hystrix -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    <version>2.1.1.RELEASE</version>
</dependency>
```

#### 启动类上加上注解

```java
@EnableCircuitBreaker   //启用熔断
```

#### 在要使用熔断器的Controller层的方法上加上

```java
@HystrixCommand(fallbackMethod = "failedFunction")
```

#### hystrix的上下文传递

```java
@HystrixCommand(fallbackMethod = "failedFunction",commandProperties = {
            @HystrixProperty(name="execution.isolation.strategy", value = "SEMAPHORE")
    })//execution.isolation.strategy不建议修改，如果预告问题再修改
```

#### 健康监控

/health访问

/hystrix.stream显示微服务情况

#### feign使用hystrix-fallback

```yaml
feign:
  hystrix:
    enabled: true
```

```java
@Component
public class FeignClient01Hystrix implements FeignClient01 {
    @Override
    public User getOrder(Long id) {
        User user = new User();
        user.setId(-300L);
        user.setDate(new Date());
        return user;
    }

    @Override
    public User get_user(User user) {
        return user;
    }
}
```

```java
@FeignClient(name = "provider-user",fallback = FeignClient01Hystrix.class)
public interface FeignClient01 {

    @GetMapping("/user/{id}")
    User getOrder(@PathVariable Long id);

    @GetMapping("/get-user")
    User get_user(User user);
}
```

改接口里面的方法范围失败时会调用fallback类里面的同名方法

主方法名上加上注释

熔断方法

```java
@EnableCircuitBreaker
```

### zuul

#### 在其入口applicaton类加上注解@EnableZuulProxy，开启zuul的功能：

```java
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@EnableDiscoveryClient
public class ServiceZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run( ServiceZuulApplication.class, args );
    }
}
```

配置文件

```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8769
spring:
  application:
    name: service-zuul
zuul:
  routes:
    api-a:
      path: /api-a/**
      serviceId: service-ribbon
    api-b:
      path: /api-b/**
      serviceId: service-feign
```

#### zuul不仅只是路由，并且还能过滤，做一些安全验证。

```java
@Component
public class MyFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(MyFilter.class);
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info(String.format("%s >>> %s", request.getMethod(), request.getRequestURL().toString()));
        Object accessToken = request.getParameter("token");
        if(accessToken == null) {
            log.warn("token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            try {
                ctx.getResponse().getWriter().write("token is empty");
            }catch (Exception e){}

            return null;
        }
        log.info("ok");
        return null;
    }
}
```

- filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
  - pre：路由之前
  - routing：路由之时
  - post： 路由之后
  - error：发送错误调用
- filterOrder：过滤的顺序
- shouldFilter：这里可以写逻辑判断，是否要过滤，本文true,永远过滤。
- run：过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。

### Config

在程序的入口Application类加上@EnableConfigServer注解开启配置服务器的功能

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}
}
```

application.properties

```properties
spring.application.name=config-server
server.port=8888


spring.cloud.config.server.git.uri=https://github.com/forezp/SpringcloudConfig/
spring.cloud.config.server.git.searchPaths=respo
spring.cloud.config.label=master
spring.cloud.config.server.git.username=your username
spring.cloud.config.server.git.password=your password
```

- spring.cloud.config.server.git.uri：配置git仓库地址
- spring.cloud.config.server.git.searchPaths：配置仓库路径
- spring.cloud.config.label：配置仓库的分支
- spring.cloud.config.server.git.username：访问git仓库的用户名
- spring.cloud.config.server.git.password：访问git仓库的用户密码

如果Git仓库为公开仓库，可以不填写用户名和密码，如果是私有仓库需要填写，本例子是公开仓库，放心使用。



#### 构建一个config client

其配置文件：

```
spring.application.name=config-client
spring.cloud.config.label=master
spring.cloud.config.profile=dev
spring.cloud.config.uri= http://localhost:8888/
server.port=8881
```

- spring.cloud.config.label 指明远程仓库的分支

- spring.cloud.config.profile

  - dev开发环境配置文件
  - test测试环境
  - pro正式环境

- spring.cloud.config.uri= http://localhost:8888/ 指明配置服务中心的网址。

- 

  ```java
  @SpringBootApplication
  @RestController
  public class ConfigClientApplication {
  
  	public static void main(String[] args) {
  		SpringApplication.run(ConfigClientApplication.class, args);
  	}
  
  	@Value("${foo}")
  	String foo;
  	@RequestMapping(value = "/hi")
  	public String hi(){
  		return foo;
  	}
  }
  
  ```

返回从配置中心读取的foo变量的值

### 高可用的分布式配置中心

在配置文件application.yml上，指定服务端口为8889，加上作为服务注册中心的基本配置

```yaml
server:
  port: 8889

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

启动类上加上@EnableEurekaServer

```java
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}
```

#### 改造config-server

配置文件application.yml，指定服务注册地址为http://localhost:8889/eureka/，

```properties
spring.application.name=config-server
server.port=8888

spring.cloud.config.server.git.uri=https://github.com/forezp/SpringcloudConfig/
spring.cloud.config.server.git.searchPaths=respo
spring.cloud.config.label=master
spring.cloud.config.server.git.username= your username
spring.cloud.config.server.git.password= your password
eureka.client.serviceUrl.defaultZone=http://localhost:8889/eureka/
```

最后需要在程序的启动类Application加上@EnableEureka的注解。

#### config-client

配置文件bootstrap.properties，注意是bootstrap

```properties
spring.application.name=config-client
spring.cloud.config.label=master
spring.cloud.config.profile=dev
#spring.cloud.config.uri= http://localhost:8888/

eureka.client.serviceUrl.defaultZone=http://localhost:8889/eureka/
spring.cloud.config.discovery.enabled=true
spring.cloud.config.discovery.serviceId=config-server
server.port=8881
```

- spring.cloud.config.discovery.enabled 是从配置中心读取文件。
- spring.cloud.config.discovery.serviceId 配置中心的servieId，即服务名。

这时发现，在读取配置文件不再写ip地址，而是服务名，这时如果配置服务部署多份，通过负载均衡，从而高可用。

### Spring Cloud Bus

在配置文件application.properties中加上RabbitMq的配置，包括RabbitMq的地址、端口，用户名、密码，代码如下：

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
# spring.rabbitmq.username=
# spring.rabbitmq.password=
```

/bus/refresh接口可以指定服务，即使用”destination”参数，比如 “/bus/refresh?destination=customers:**” 即刷新服务名为customers的所有服务，不管ip。

### Sleuth

#### 三、术语

- Span：基本工作单元，例如，在一个新建的span中发送一个RPC等同于发送一个回应请求给RPC，span通过一个64位ID唯一标识，trace以另一个64位ID表示，span还有其他数据信息，比如摘要、时间戳事件、关键值注释(tags)、span的ID、以及进度ID(通常是IP地址) span在不断的启动和停止，同时记录了时间信息，当你创建了一个span，你必须在未来的某个时刻停止它。
- Trace：一系列spans组成的一个树状结构，例如，如果你正在跑一个分布式大数据工程，你可能需要创建一个trace。
- Annotation：用来及时记录一个事件的存在，一些核心annotations用来定义一个请求的开始和结束
  - cs - Client Sent -客户端发起一个请求，这个annotion描述了这个span的开始
  - sr - Server Received -服务端获得请求并准备开始处理它，如果将其sr减去cs时间戳便可得到网络延迟
  - ss - Server Sent -注解表明请求处理的完成(当请求返回客户端)，如果ss减去sr时间戳便可得到服务端需要的处理请求时间
  - cr - Client Received -表明span的结束，客户端成功接收到服务端的回复，如果cr减去cs时间戳便可得到客户端从服务端获取回复的所有所需时间 将Span和Trace在一个系统中使用Zipkin注解的过程图形化：

将Span和Trace在一个系统中使用Zipkin注解的过程图形化：

![Paste_Image.png](https://www.fangzhipeng.com/img/jianshu/2279594-4b865f2a2c271def.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/800)

### 高可用的服务注册中心

在eureka-server工程中resources文件夹下，创建配置文件application-peer1.yml:

```yaml
server:
  port: 8761

spring:
  profiles: peer1
eureka:
  instance:
    hostname: peer1
  client:
    serviceUrl:
      defaultZone: http://peer2:8769/eureka/
```

并且创建另外一个配置文件application-peer2.yml：

```yaml
server:
  port: 8769

spring:
  profiles: peer2
eureka:
  instance:
    hostname: peer2
  client:
    serviceUrl:
      defaultZone: http://peer1:8761/eureka/
```

windows电脑，在c:/windows/systems/drivers/etc/hosts 修改。

```hosts
127.0.0.1 peer1
127.0.0.1 peer2
```

eureka.instance.preferIpAddress=true是通过设置ip让eureka让其他服务注册它。也许能通过去改变去通过改变host的方式。

此时的架构图：

![有点丑e.png](https://www.fangzhipeng.com/img/jianshu/2279594-a052854a3084fdd6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/800)

Eureka-eserver peer1 8761,Eureka-eserver peer2 8769相互感应，当有服务注册时，两个Eureka-eserver是对等的，它们都存有相同的信息，这就是通过服务器的冗余来增加可靠性，当有一台服务器宕机了，服务并不会终止，因为另一台服务存有相同的数据。

### docker部署springcloud工程

### Hystrix Dashboard

> 在微服务架构中为例保证程序的可用性，防止程序出错导致网络阻塞，出现了断路器模型。断路器的状况反应了一个程序的可用性和健壮性，它是一个重要指标。Hystrix Dashboard是作为断路器状态的一个组件，提供了数据监控和友好的图形化界面。

### Turbine

>看单个的Hystrix Dashboard的数据并没有什么多大的价值，要想看这个系统的Hystrix Dashboard数据就需要用到Hystrix Turbine。Hystrix Turbine将每个服务Hystrix Dashboard数据进行了整合。Hystrix Turbine的使用非常简单，只需要引入相应的依赖和加上注解和配置就可以了。

### Consul

> consul 具有以下性质：
>
> - 服务发现：consul通过http 方式注册服务，并且服务与服务之间相互感应。
> - 服务健康监测
> - key/value 存储
> - 多数据中心
>
> consul可运行在mac windows linux 等机器上。