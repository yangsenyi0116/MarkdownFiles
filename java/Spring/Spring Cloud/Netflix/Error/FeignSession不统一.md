需要添加feign调用时传递session信息

```java
/**
 * feign调用时传递session信息
 * @author 向振华
 * @date 2018/12/7 16:39
 */
@Component
public class FeignRequestIntercepter implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //通过RequestContextHolder获取本地请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        //获取本地线程绑定的请求对象
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        //给请求模板附加本地线程头部信息，主要是cookie信息
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement().toString();
            requestTemplate.header(name, request.getHeader(name));
        }
        if(!request.isRequestedSessionIdValid()){
            request.setAttribute(SessionRepositoryFilter.INVALID_SESSION_ID_ATTR,null);
            requestTemplate.header("cookie","SESSION="+request.getSession().getId());
        }
    }
}
```

```java
/**
 * Redis Session共享配置
 * @author 向振华
 * @date 2018/9/15 14:36
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600,redisFlushMode = RedisFlushMode.IMMEDIATE)
public class RedisSessionConfig {
}
```

<https://blog.csdn.net/my_momo_csdn/article/details/80922737>