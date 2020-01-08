### shiro认证
```java
public class AppTest 
{

    SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();

    @Before
    public void addUser(){
     simpleAccountRealm.addAccount("Mark","123456","admin");
    }

    @Test
    public void testAuthentication(){
        // 1.构建securityManager
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);

        //2.主题提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("Mark","123456");
        subject.login(token);

        System.out.println(subject.isAuthenticated());
    }
}
```
### shiro授权

#### inirealm

```java
public class IniRealTest {
    IniRealm iniRealm = new IniRealm("classpath:user.ini");

    @Test
    public void testAuthentication(){
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(iniRealm);

        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("Mark", "123456");
        subject.login(token);

        System.out.println(subject.isAuthenticated());

        subject.checkRole("admin");
        subject.checkPermission("user:delete");
    }
}
```

```ini
[Users]
Mark=123456,admin
[roles]
admin=user:delete
```

#### jdbcrealm

```java
public class JdbcRealmTest {
    DruidDataSource datasourcd = new DruidDataSource();
    {
        datasourcd.setUrl("jdbc:mysql://localhost:3306/shirotest");
        datasourcd.setUsername("root");
        datasourcd.setPassword("root");
    }

    @Test
    public void testAuthentication(){
        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(datasourcd);
        jdbcRealm.setPermissionsLookupEnabled(true);

        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);

        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("Mark", "123456");
        subject.login(token);

        subject.checkRole("admin");
        subject.checkRoles("admin","user");

        subject.checkPermission("user:select");
    }
}
```



```java
public class JdbcRealmTest {
    DruidDataSource datasourcd = new DruidDataSource();
    {
        datasourcd.setUrl("jdbc:mysql://localhost:3306/shirotest");
        datasourcd.setUsername("root");
        datasourcd.setPassword("root");
    }

    @Test
    public void testAuthentication(){
        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(datasourcd);
        jdbcRealm.setPermissionsLookupEnabled(true);

        String sql = "select password from test_user where username = ?";
        jdbcRealm.setAuthenticationQuery(sql);
        String roleSql = "select role_name from test_user_role where username = ?";
        jdbcRealm.setUserRolesQuery(roleSql);

        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);

        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

//        UsernamePasswordToken token = new UsernamePasswordToken("Mark", "123456");
        UsernamePasswordToken token = new UsernamePasswordToken("Jack", "654321");
        subject.login(token);

        /*subject.checkRole("admin");
        subject.checkRoles("admin","user");
        subject.checkPermission("user:select");*/
    }
}
```

#### 自定义realm

```java
public class CustomRealm extends AuthorizingRealm {
    Map<String,String> usermap = new HashMap<>(16);

    {
        usermap.put("Mark","123456");
        super.setName("customRealm");
    }

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
        Set<String> sets = new HashSet<>();
        sets.add("admin");
        sets.add("user");
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
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo("Mark",password,"customRealm");
        return authenticationInfo;
    }

    /**
     * 模拟数据库查询凭证
     * @param userName
     * @return
     */
    private String getPasswordByUsername(String userName){
        //自定义jdbc规则去数据库中获取密码

        return usermap.get(userName);
    }
}
```

#### shiro加密

```java
public void testAuthentication() {
    CustomRealm customRealm = new CustomRealm();

    DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
    defaultSecurityManager.setRealm(customRealm);

    HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
    matcher.setHashAlgorithmName("md5");
    //设置加密的次数
    matcher.setHashIterations(1);
    //向realm中加入加密对象
    customRealm.setCredentialsMatcher(matcher);

    SecurityUtils.setSecurityManager(defaultSecurityManager);
    Subject subject = SecurityUtils.getSubject();

    UsernamePasswordToken token = new UsernamePasswordToken("Mark", "123456");
    subject.login(token);

    /*subject.checkRole("admin");
    subject.checkRoles("admin","user");
    subject.checkPermission("user:select");*/
}
```

```java
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
    SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo("Mark",password,"customRealm");
    authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("Mark"));
    return authenticationInfo;
}
```

