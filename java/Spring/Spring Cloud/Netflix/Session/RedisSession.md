### 1. pom.xml jar包引入

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
 
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
    <version>2.0.4.RELEASE</version>
</dependency>
```

### 2. **创建redis配置文件**

jdbc-redis.properties文件内容:

```properties
redis.host = 192.168.0.111
redis.port = 6379
redis.pass = admin
redis.maxIdle = 300
redis.maxActive = 600
redis.maxWait = 100000
redis.testOnBorrow = true
redis.testOnReturn =true
#spring-session中session过期时间 单位：秒
redis.timeout = 3600
#spring-session中redis命名空间
redis.namespace = smartCampus
#父域名
redis.parentDomainName = localhost
#cookie名字
redis.cookieName = smartCampusSessionId
```

```java
@Data
@Configuration
@ConfigurationProperties(prefix = "redis")
@PropertySource(value = "classpath:jdbc-redis.properties")
public class RedisProperty {
    private String host;
    private int port;
    private String pass;
    private String maxIdle;
    private String maxActive;
    private String maxWait;
    private String testOnBorrow;
    private String testOnReturn;
    private int timeout;
    private String namespace;
    private String parentDomainName;
    private String cookieName;
}
```

### 第三步：创建Session配置文件

若使用注解@EnableRedisHttpSession，则到这一步就可以了，注意redisNamespace为"spring:session:XXXX"

```java
@Configuration
//如果需求给此注解传指定的参数，请看下一步
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1600, redisNamespace = "spring:session:smartCampus")
public class SpringSessionConfig {
 
    @Autowired
    private RedisProperty redisProperty;
 
    /**
     * springBoot 推荐的redis连接池
     * @return
     */
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(0);
        redisStandaloneConfiguration.setHostName(redisProperty.getHost());
        redisStandaloneConfiguration.setPort(redisProperty.getPort());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperty.getPass()));
        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();
        return new LettuceConnectionFactory(redisStandaloneConfiguration,
                lettuceClientConfigurationBuilder.build());
    }
 
    /**
     *  配置cookie解析sessionId
     * @return
     */
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        CookieHttpSessionIdResolver cookieHttpSessionIdResolver = new CookieHttpSessionIdResolver();
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookieName(redisProperty.getCookieName());//cookies名称
        cookieSerializer.setDomainName(redisProperty.getParentDomainName());
        cookieSerializer.setCookiePath("/");
        cookieSerializer.setUseBase64Encoding(false);
        cookieHttpSessionIdResolver.setCookieSerializer(cookieSerializer);
        return cookieHttpSessionIdResolver;
    }
}
```

### 4. 

解决为@EnableRedisHttpSession动态传参，首先在上一步不使用注解，创新新的配置文件，继承RedisHttpSessionConfiguration 类，因为@EnableRedisHttpSession用的就是这个类，这里当初想用第二步创建的RedisProperty ，但是RedisSessionConfig 这个类在RedisProperty 之前就已经注入了，拿不到RedisProperty 的属性，这里就用读取文件的方式获取了需要的参数

```java
/**
 * RedisHttpSession参数动态配置
 */
@Configuration
public class RedisSessionConfig extends RedisHttpSessionConfiguration {
    public RedisSessionConfig() {
        super();
        String redisNameSpace  = PropertiesUtil.getInstance("jdbc-redis").getPropertyAsString("redis.namespace");
        String timeout  = PropertiesUtil.getInstance("jdbc-redis").getPropertyAsString("redis.timeout");
        super.setRedisNamespace("spring:session:" + redisNameSpace);
        super.setMaxInactiveIntervalInSeconds(Integer.valueOf(timeout));
    }
}
```

