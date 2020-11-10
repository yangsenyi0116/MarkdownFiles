1、会话共享应用背景
     因为以前我们项目之中使用的单一的SpringBoot应用，所有的业务应用、鉴权和登录等操作都是在一个单一的微服务之中进行实现的；由于项目和产品的需要；决定使用现今比较流行的SpringCloud微服务进行重新架构应用。并且把内容分拆为如下几种微服务：独立的网关(Zuul)、注册中心(eureka)、旧的应用服务服务(***Application)、新闻消息服务(news)、人才招聘服务(talent)、论坛问答服务(forum-apis)。但是在调用过程之中所有用户信息存储在会话(Session)之中；如果使用当前比较流行的技术JWT方式传递访问接口权限是不需要会话的。本文将以实际场景下使用会话共享方式来说明如何实现微服务之间的会话共享。

2、SpringCloud各个微服务 (SpringBoot)应用之间会话共享
     实现微服务之间的会话共享、我们系统之中使用的SpringBoot+Redis方式来实现会话共享的；本人分析具体如何实现话共享原理。实际上是用户登录的时候，把回话通过在每个微服务启动类**Application.java上加入会话共享注解(@EnableRedisHttpSession//增加redissession缓存支持)、同时需要在每个微服务(SpringBoot)的配置文件之中配置想用的Redis数据库，实现多个应用同时访问同一个Redis数据库。其底层实现原理是通过把会话存储在Redis之中，然后通过Spring Session实现各个业务应用之间从redis之中获得相同的会话。从而保证的普通的SpringBoot微服务之间会话共享。主要实现和关键代码：

maven引入代码：

```xml
<!-- springboot - Redis -->
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<!--spring session 与redis应用基本环境配置,需要开启redis后才可以使用，不然启动Spring boot会报错 -->
<dependency>
	<groupId>org.springframework.session</groupId>
	<artifactId>spring-session-data-redis</artifactId>
</dependency>
```
2.1、启动类或者Redis配置类加入Redis会话共享注解
1、在启动类之中加入 EnableRedisHttpSession 注解
```java
@SpringBootApplication
@EnableRedisHttpSession//增加redissession缓存支持
@EnableFeignClients//增加feign支持，引入feign注解，feign扫描路径可以单独指定(basePackages = ),默认是spring的扫描路径
public class ServiceOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOneApplication.class,args);
    }
     
    @Bean
    public FeignHystrixConcurrencyStrategy feignHystrixConcurrencyStrategy() {
        return new FeignHystrixConcurrencyStrategy();
    }
}
```
2、在RedisCacheConfig 配置类上配置也一样效果
```java
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600) // redis托管session
@Configuration
@EnableCaching // 启用缓存
public class RedisCacheConfig extends CachingConfigurerSupport {
}
​```java

以上方案其中任何一种都可以
2.2、配置Redis基本配置内容
​```properties
spring.session.store-type=none
# redis (redisConfiguration)
spring.cache.type=REDIS
spring.redis.database=11
spring.redis.port=6379
spring.redis.jedis.pool.max-idle=5
spring.redis.jedis.pool.min-idle=0
spring.redis.jedis.pool.max-active=20
spring.redis.jedis.pool.max-wait=2000
spring.redis.timeout=2000
spring.redis.host=127.0.0.1
spring.redis.password=123456
```
最后会话共享效果如下图所示：





可以看见上面两个截图可以发现不同的应用(端口不同)、其会话是相同的

参考文章：https://blog.csdn.net/zl18310999566/article/details/54290994 https://www.cnblogs.com/yingsong/p/9838198.html

3、SpringCloud之中Feign调用微服务实现会话共享
在实现项目的时候发现，微服务使用feign相互之间调用时，存在session丢失的问题。例如，使用Feign调用某个远程API，这个远程API需要传递一个用户信息，我们可以把cookie里面的session信息放到Header里面，这个Header是动态的，跟你的HttpRequest相关，我们选择编写一个拦截器来实现Header的传递，也就是需要实现RequestInterceptor接口。

```java
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 实现RequestInterceptor，用于设置feign全局请求模板
    */
    @Configuration
    @EnableFeignClients
    public class FeignRequestIntercepter implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        //通过RequestContextHolder获取本地请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        //获取本地线程绑定的请求对象
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        System.out.println("=session-id: "+request.getSession().getId());
        //给请求模板附加本地线程头部信息，主要是cookie信息
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                Enumeration<String> values = request.getHeaders(name);
                while (values.hasMoreElements()) {
                    String value = values.nextElement();
                    template.header(name, value);
                }
            }
        }
    }
    }
```
    经过测试，上面的解决方案可以正常的使用； 
    但是，当引入Hystrix熔断策略时，出现了一个新的问题；(意味熔断器如果设置false是可以使用)

获取不到request信息，从而无法传递session信息，最终发现RequestContextHolder.getRequestAttributes()该方法是从ThreadLocal变量里面取得对应信息的，这就找到问题原因了，由于Hystrix熔断机制导致的。 
Hystrix有隔离策略：THREAD以及SEMAPHORE，当隔离策略为 THREAD 时，是没办法拿到 ThreadLocal 中的值的。

为了完美解决问题建议使用自定义策略
```java
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RequestAttributeHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
    private static final Log log = LogFactory.getLog(RequestAttributeHystrixConcurrencyStrategy.class);

    private HystrixConcurrencyStrategy delegate;
     
    public RequestAttributeHystrixConcurrencyStrategy() {
        try {
            this.delegate = HystrixPlugins.getInstance().getConcurrencyStrategy();
            if (this.delegate instanceof RequestAttributeHystrixConcurrencyStrategy) {
                // Welcome to singleton hell...
                return;
            }
            HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins
                    .getInstance().getCommandExecutionHook();
            HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance()
                    .getEventNotifier();
            HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance()
                    .getMetricsPublisher();
            HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance()
                    .getPropertiesStrategy();
            this.logCurrentStateOfHystrixPlugins(eventNotifier, metricsPublisher,
                    propertiesStrategy);
            HystrixPlugins.reset();
            HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
            HystrixPlugins.getInstance()
                    .registerCommandExecutionHook(commandExecutionHook);
            HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
        }
        catch (Exception e) {
            log.error("Failed to register Sleuth Hystrix Concurrency Strategy", e);
        }
    }
     
    private void logCurrentStateOfHystrixPlugins(HystrixEventNotifier eventNotifier,
                                                 HystrixMetricsPublisher metricsPublisher,
                                                 HystrixPropertiesStrategy propertiesStrategy) {
        if (log.isDebugEnabled()) {
            log.debug("Current Hystrix plugins configuration is ["
                    + "concurrencyStrategy [" + this.delegate + "]," + "eventNotifier ["
                    + eventNotifier + "]," + "metricPublisher [" + metricsPublisher + "],"
                    + "propertiesStrategy [" + propertiesStrategy + "]," + "]");
            log.debug("Registering Sleuth Hystrix Concurrency Strategy.");
        }
    }
     
    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return new WrappedCallable<T>(callable, requestAttributes);
    }
     
    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixProperty<Integer> corePoolSize,
                                            HystrixProperty<Integer> maximumPoolSize,
                                            HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue) {
        return this.delegate.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue);
    }
     
    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return this.delegate.getBlockingQueue(maxQueueSize);
    }
     
    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(
            HystrixRequestVariableLifecycle<T> rv) {
        return this.delegate.getRequestVariable(rv);
    }
     
    static class WrappedCallable<T> implements Callable<T> {
     
        private final Callable<T> target;
        private final RequestAttributes requestAttributes;
     
        public WrappedCallable(Callable<T> target, RequestAttributes requestAttributes) {
            this.target = target;
            this.requestAttributes = requestAttributes;
        }
     
        @Override
        public T call() throws Exception {
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                return target.call();
            }
            finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }
    }
}
```
加入此代码之后，必须在启动类 注入 Bean FeignHystrixConcurrencyStrategy 

```java
import com.nmm.study.rpc.FeignHystrixConcurrencyStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author nmm 2018/6/26
 * @description
    */
    @SpringBootApplication
    @EnableRedisHttpSession//增加redissession缓存支持
    @EnableFeignClients//增加feign支持，引入feign注解，feign扫描路径可以单独指定(basePackages = ),默认是spring的扫描路径
    public class ServiceOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOneApplication.class,args);
    }

    @Bean
    public FeignHystrixConcurrencyStrategy feignHystrixConcurrencyStrategy() {
        return new FeignHystrixConcurrencyStrategy();
    }
 }
```
    至此，方可实现 feign调用session丢失的问题完美解决。

本人参与实际项目之中最后虽然使用的通过Header传递信息，还是记录一下以备后面使用。现在主流技术还是使用JWT方式不需要使用Session共享方式。