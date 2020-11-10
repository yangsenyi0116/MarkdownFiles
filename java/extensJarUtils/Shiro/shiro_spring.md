### spring-beans.xml


```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc" xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop" xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd">
    <bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
        <property name="securityManager" ref="securityManager" />
        <property name="loginUrl" value="login.html" />
        <property name="unauthorizedUrl" value="403.html" />
        <property name="filterChainDefinitions">
            <value>
                /login.html = anon
                /subLogin = anon
                /testRole = roles["admin","admin1"]
                /testRole1 = rolesOr["admin","admin1"]
                /** = authc
            </value>
        </property>
        <property name="filters">
            <util:map>
                <entry key="rolesOr" value-ref="rolesOrFilter" />
            </util:map>
        </property>
    </bean>

    <bean class="com.kermi.shiro.ssm.filter.RolesOrFilter" id="rolesOrFilter">

    </bean>
    <!--创建SecurityManager对象-->
    <bean class="org.apache.shiro.web.mgt.DefaultWebSecurityManager" id="securityManager">
        <property name="realm" ref="realm" />
        <property name="sessionManager" ref="sessionManager"/>
        <property name="cacheManager" ref="cacheManager" />
        <property name="rememberMeManager" ref="cookieRememberMeManager" />
    </bean>

    <bean class="com.kermi.shiro.ssm.shiro.realm.CustomRealm" id="realm">
        <property name="credentialsMatcher" ref="credentialsMatcher" />
    </bean>

    <bean class="org.apache.shiro.authc.credential.HashedCredentialsMatcher" id="credentialsMatcher">
        <property name="hashAlgorithmName" value="md5" />
        <property name="hashIterations" value="1"/>
    </bean>

    <bean class="com.kermi.shiro.ssm.cache.RedisCacheManager" id="cacheManager"/>

    <bean class="com.kermi.shiro.ssm.session.CustomSessionManager" id="sessionManager">
        <property name="sessionDAO" ref="redisSessionDao"/>
    </bean>

    <bean class="org.apache.shiro.web.mgt.CookieRememberMeManager" id="cookieRememberMeManager">
        <property name="cookie" ref="cookie" />
    </bean>

    <bean class="org.apache.shiro.web.servlet.SimpleCookie" id="cookie">
        <constructor-arg value="remeberMe" />
        <property name="maxAge" value="20000000" />
    </bean>

    <bean class="com.kermi.shiro.ssm.session.RedisSessionDao" id="redisSessionDao"></bean>
</beans>
```

### springmvc.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:task="http://www.springframework.org/schema/task" xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context-4.2.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc-4.2.xsd
        http://www.springframework.org/schema/task
        http://www.springframework.org/schema/task/spring-task-4.2.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">
    <context:component-scan base-package="com.kermi.shiro.ssm.controller"/>


    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <!-- 配置从项目根目录到指定目录一端路径 ,建议指定浅一点的目录-->
        <property name="prefix" value="/jsp/" />
        <!-- 文件的后缀名 -->
        <property name="suffix" value=".jsp" />
    </bean>

    <!--<bean id="commonsMultipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">-->
        <!--<property name="maxUploadSize" value="1020000000" />-->
        <!--<property name="defaultEncoding" value="UTF-8" />-->
        <!--<property name="maxUploadSizePerFile" value="2000000000" />-->
    <!--</bean>-->

    <mvc:default-servlet-handler />
    <mvc:annotation-driven/>
    <mvc:resources mapping="/*" location="/" />

    <aop:config proxy-target-class="true" />
    <bean class="org.apache.shiro.spring.LifecycleBeanPostProcessor">

    </bean>
    <bean class="org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor">
        <property name="securityManager" ref="securityManager"/>
    </bean>
</beans>
```

### spring-jedis.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc" xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">
    <bean class="redis.clients.jedis.JedisPool" id="jedisPool">
        <constructor-arg ref="jedisPoolConfig"/>
        <constructor-arg value="127.0.0.1"/>
        <constructor-arg value="6379"/>
    </bean>

    <bean class="redis.clients.jedis.JedisPoolConfig" id="jedisPoolConfig">

    </bean>
</beans>
```

### 自定义realm

```java
package com.kermi.shiro.ssm.shiro.realm;

import com.kermi.shiro.ssm.dao.UserDao;
import com.kermi.shiro.ssm.pojo.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author : Kermi
 * @Date : 2019/7/6 08:34 下午
 * @Version : 1.0
 */
public class CustomRealm extends AuthorizingRealm {

    @Resource
    private UserDao userDao;

    @Override
    /*授权*/
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String userName = (String) principalCollection.getPrimaryPrincipal();
        //从数据库或者缓存中获取角色数据
        Set<String> roles = getRolesByuserName(userName);
        //从数据库或者缓存中获取权限数据
        Set<String> permissions  = getPermissionsByuserName(userName);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    private Set<String> getPermissionsByuserName(String userName){
        Set<String> sets = new HashSet<>();
        sets.add("user:delete");
        sets.add("user:add");
        return sets;
    }

    /**
     *
     * @param userName
     * @return
     */
    private Set<String> getRolesByuserName(String userName){
        List<String> list = userDao.queryRolesByUserName(userName);
        Set<String> sets = new HashSet<>(list);
        return sets;
    }

    @Override
    /*认证*/
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1.从主体传过来的认证信息中获取用户名
        String userName = (String) authenticationToken.getPrincipal();
        //2.通过用户名到数据库中获取凭证
        String password = getPasswordByUsername(userName);
        //如果用户不存在 直接放回一个null值
        if(password == null){
            return null;
        }
        //如果存在,创建对象并返回
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName,password,"customRealm");
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(userName));
        return authenticationInfo;
    }

    /**
     * 模拟数据库查询凭证
     * @param userName
     * @return
     */
    private String getPasswordByUsername(String userName){
        //自定义jdbc规则去数据库中获取密码
        User user = userDao.getUserByUserName(userName);
        if (user != null){
            return user.getPassword();
        }
        return null;
    }

    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("1234567","Mark");
        System.out.println(md5Hash);
    }
}
```

### UserDao.java

```java
package com.kermi.shiro.ssm.dao;

import com.kermi.shiro.ssm.pojo.User;

import java.util.List;

/**
 * @Author : Kermi
 * @Date : 2019/7/21 11:28 上午
 * @Version : 1.0
 */
public interface UserDao {
    User getUserByUserName(String userName);

    List<String> queryRolesByUserName(String userName);
}
```

### UserDaoImpl.java

```java
package com.kermi.shiro.ssm.dao.impl;

import com.kermi.shiro.ssm.dao.UserDao;
import com.kermi.shiro.ssm.pojo.User;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author : Kermi
 * @Date : 2019/7/21 11:29 上午
 * @Version : 1.0
 */
@Component
public class UserDaoImpl implements UserDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public User getUserByUserName(String userName) {
        String sql = "select username,password from users where username = ?";
        List<User> list = jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }
        });

        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<String> queryRolesByUserName(String userName) {
        String sql = "select role_name from user_roles where username = ?";

        return jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("role_name");
            }
        });
    }
}
```

### 自定义拦截器

```java
package com.kermi.shiro.ssm.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @Author : Kermi
 * @Date : 2019/7/21 09:35 下午
 * @Version : 1.0
 */
public class RolesOrFilter extends AuthorizationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        Subject subject = getSubject(servletRequest,servletResponse);
        String[] roles = (String[]) o;
        if(roles == null || roles.length == 0){
            return true;
        }
        for (String role: roles){
            if(subject.hasRole(role)){
                return true;
            }
        }
        return false;
    }
}
```

### 自定义WebSessionManager

```java
package com.kermi.shiro.ssm.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
 * @Author : Kermi
 * @Date : 2019/7/21 10:14 下午
 * @Version : 1.0
 */
public class CustomSessionManager extends DefaultWebSessionManager {
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request = null;
        if(sessionKey instanceof WebSessionKey){
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }
        if(request != null && sessionId != null){
            Session session = (Session) request.getAttribute(sessionId.toString());
            if(session != null){
                return session;
            }
        }
        Session session = super.retrieveSession(sessionKey);
        if (request != null && sessionId != null)  {
            request.setAttribute(sessionId.toString(), session);
        }
        return session;
    }
}
```

### ###自定义redisDao对象

```java
package com.kermi.shiro.ssm.session;

import com.kermi.shiro.ssm.util.JedisUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author : Kermi
 * @Date : 2019/7/21 09:47 下午
 * @Version : 1.0
 */
public class RedisSessionDao extends AbstractSessionDAO {

    @Resource
    private JedisUtil jedisUtil;

    private final String SHIRO_SESSION_PREFIX = "SHIRO_SESSION:";

    private byte[] getKey(String key){
        return (SHIRO_SESSION_PREFIX + key).getBytes();
    }

    private void saveSession(Session session){
        if(session != null && session.getId() != null){
            byte[] key = getKey(session.getId().toString());
            byte[] value = SerializationUtils.serialize(session);
            jedisUtil.set(key,value);
            jedisUtil.expire(key,600);
        }
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session,sessionId);
        saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null){
            return null;
        }
        byte[] key = getKey(sessionId.toString());
        byte[] value = jedisUtil.get(key);
        return (Session) SerializationUtils.deserialize(value);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        saveSession(session);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null){
            return;
        }
        byte[] key = getKey(session.getId().toString());
        jedisUtil.del(key);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<byte[]> keys = jedisUtil.keys(SHIRO_SESSION_PREFIX);
        Set<Session> sessions = new HashSet<>();
        if(CollectionUtils.isEmpty(keys)){
            return sessions;
        }
        for(byte[] key: keys){
            Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
        }
        return null;
    }
}
```

### redis工具类

```java
package com.kermi.shiro.ssm.util;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @Author : Kermi
 * @Date : 2019/7/21 09:48 下午
 * @Version : 1.0
 */
@Component
public class JedisUtil {

    @Resource
    private JedisPool jedisPool;

    private Jedis getResource(){
     return jedisPool.getResource();
    }

    public byte[] set(byte[] key, byte[] value) {
        Jedis jedis = getResource();
        try {
            jedis.set(key,value);
            return value;
        }finally {
            jedis.close();
        }
    }

    public void expire(byte[] key, int i) {
        Jedis jedis = getResource();
        try {
            jedis.expire(key,i);
        }finally {
            jedis.close();
        }
    }

    public byte[] get(byte[] key) {
        Jedis jedis = getResource();
        try {
            return jedis.get(key);
        }finally {
            jedis.close();
        }
    }

    public void del(byte[] key) {
        Jedis jedis = getResource();
        try {
            jedis.del(key);
        }finally {
            jedis.close();
        }
    }

    public Set<byte[]> keys(String shiro_session_prefix) {
        Jedis jedis = getResource();
        try {
            return jedis.keys((shiro_session_prefix + "*").getBytes());
        }finally {
            jedis.close();
        }
    }
}
```

### Redis缓存

```java
package com.kermi.shiro.ssm.cache;

import com.kermi.shiro.ssm.util.JedisUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

/**
 * @Author : Kermi
 * @Date : 2019/7/24 12:03 下午
 * @Version : 1.0
 */
@Component
public class RedisCache<K,V> implements Cache<K,V> {

    @Resource
    private JedisUtil jedisUtil;

    private final String CACHE_PREFIX = "shiro-cache";

    private byte[] getkey(K k){
        if (k instanceof String){
            return (CACHE_PREFIX + k).getBytes();
        }
        return SerializationUtils.serialize(k);
    }

    @Override
    public V get(K k) throws CacheException {
        byte[] value = jedisUtil.get(getkey(k));
        if (value != null){
            return (V) SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        byte[] key = getkey(k);
        byte[] value = SerializationUtils.serialize(v);
        jedisUtil.set(key,value);
        jedisUtil.expire(key,600);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        byte[] key = getkey(k);
        byte[] value = jedisUtil.get(key);
        jedisUtil.del(key);
        if (value != null){
            return (V) SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public void clear() throws CacheException {
        //
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }
}
```

### Redis缓存管理类

```java
package com.kermi.shiro.ssm.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import javax.annotation.Resource;

/**
 * @Author : Kermi
 * @Date : 2019/7/24 12:02 下午
 * @Version : 1.0
 */
public class RedisCacheManager implements CacheManager {

    @Resource
    private RedisCache redisCache;

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return redisCache;
    }
}
```

### RememberMe记住登录

```java
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>登录</title>
</head>
<body>
<form action="subLogin" method="post">
    用户名：<input type="text" name="username"><br>
    密码：<input type="password" name="password"><br>
    <input type="checkbox" name="rememberMe">记住我<br>
    <input type="submit" value="登录">
</form>
</body>
</html>
```

```java
token.setRememberMe(user.isRememberMe());
```

