## 1. 基于注解开发事务管理

### 1.1 创建数据库表

```sql
 create table `account`(
    id int primary key not null auto_increment,
    name varchar(255) not null ,
    money double
);
-- 初始化数据
insert into account(name, money) values ('cloud', 5000);
insert into account(name, money) values ('tom', 5000);
12345678
```

### 1.2 添加依赖

```xml
<dependency>
    <!-- Spring事务管理 -->
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>5.2.4.RELEASE</version>
</dependency>
<!-- Mysql -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.47</version>
</dependency>

<!-- druid -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.10</version>
</dependency>

<!-- Spring JDBC -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.2.4.RELEASE</version>
</dependency>
<!-- Spring -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.2.10.RELEASE</version>
</dependency>
<!-- junit -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
</dependency>
<!-- Spring test -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>5.2.10.RELEASE</version>
</dependency>
1234567891011121314151617181920212223242526272829303132333435363738394041424344
```

### 1.3 实体类

```java
public class Account
{
    private Integer id;
    private String name;
    private Double money;

    public Account()
    {
    }
    public Account(String name)
    {
        this.name = name;
    }

    public Account(Integer id, String name, Double money)
    {
        this.id = id;
        this.name = name;
        this.money = money;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Double getMoney()
    {
        return money;
    }

    public void setMoney(Double money)
    {
        this.money = money;
    }

    @Override
    public String toString()
    {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                '}';
    }
}
12345678910111213141516171819202122232425262728293031323334353637383940414243444546474849505152535455565758596061
```

### 1.4 dao层接口

```java
public interface AccountDao
{
    /**
     * 转出
     * @param account 账户
     * @param money 转账多少金额
     */
    public void out(Account account, Double money);

    /**
     * 转入
     * @param account 账户
     * @param money 转账多少金额
     */
    public void in(Account account, Double money);

    /**
     * 查询所有账户
     * @return 账户集合
     */
    public List<Account> selectAllAccount();

}
1234567891011121314151617181920212223
```

### 1.5 Service层接口

```java
public interface AccountService
{
    /**
     * 转账
     * @param from  从谁的账户转
     * @param to    转到谁的账户
     * @param money 转账金额
     */
    public void transfer(Account from, Account to, Double money);

    /**
     * 查询所有账户
     * @return 账户集合
     */
    public List<Account> selectAllAccount();
}
12345678910111213141516
```

### 1.6 dao层实现类

```java
@Repository("accountDao")
public class AccountDaoImpl implements AccountDao
{

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public void out(Account account, Double money)
    {
        String sql = "update account set money=money - ? where name=?";
        jdbcTemplate.update(sql, money, account.getName());
    }

    public void in(Account account, Double money)
    {
        String sql = "update account set money=money + ? where name = ?";
        jdbcTemplate.update(sql, money, account.getName());
    }

    public List<Account> selectAllAccount()
    {
        String sql = "select id, name, money from account";
        List<Account> accountList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Account>(Account.class));
        return accountList;
    }

}
12345678910111213141516171819202122232425262728
```

### 1.7 Service层实现类

```java
/**
 * @Transactional:  加在类上表示本类所有方法都加入事务管理,
 *                  加在方法上表示当前方法,优先级大于加在类上.
 */
@Service("accountService")
@Transactional("transactionManager")
public class AccountServiceImpl implements AccountService
{

    @Resource(name = "accountDao")
    private AccountDao accountDao;


    @Transactional
    public void transfer(Account from, Account to, Double money)
    {
        accountDao.out(from, money);
        // 手动触发异常
        int i = 1/0;
        accountDao.in(to, money);
    }

    @Transactional(readOnly = true)
    public List<Account> selectAllAccount()
    {
        return accountDao.selectAllAccount();
    }
}
12345678910111213141516171819202122232425262728
```

### 1.8 jdbc配置文件

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/cloud?characterEncoding=utf-8&useSSL=false
jdbc.username=root
jdbc.password=root
1234
```

### 1.9 数据源配置类

```java
@Configuration
@PropertySource("classpath:jdbc.properties")
public class DataSourceConfiguration
{
    @Value("${jdbc.driver}")
    private String driver;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    /**
     * 获取数据源, 并存储到Spring容器中
     * @return
     */
    @Bean("dataSource")
    public DataSource getDataSource()
    {
        DruidDataSource druidDataSource = new DruidDataSource();

        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        return druidDataSource;
    }

    /**
     * 获取jdbcTemplate, 并存储到Spring容器中
     * @return
     */
    @Bean("jdbcTemplate")
    public JdbcTemplate getJdbcTemplate()
    {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(getDataSource());
        return jdbcTemplate;
    }

    /**
     * 获取事务管理器, 并存储到Spring 容器
     * @param dataSource    数据源, Spring会自动帮我们注入
     * @return
     */
    @Bean("transactionManager")
    public TransactionManager getTransactionManager(DataSource dataSource)
    {
        return new DataSourceTransactionManager(dataSource);
    }

}
1234567891011121314151617181920212223242526272829303132333435363738394041424344454647484950515253545556
```

### 1.10 Spring配置类

```java
/**
 * @EnableTransactionManagement: 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
 */
@Configuration
@ComponentScan("site.zhouyun.annotation")
@Import(DataSourceConfiguration.class)
@EnableTransactionManagement
public class SpringConfiguration
{

}
1234567891011
```

### 1.11 测试类

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class Main
{
    @Autowired
    @Qualifier("accountService")
    private AccountService accountService;

    @Test
    public void testTransfer()
    {
        Account from = new Account("cloud");
        Account to = new Account("tom");

        System.out.println("--------转账前账户信息----------");
        testSelectAllAccount();

        /**
         *  为了出现异常也能正常输出账户信息 ,所以处理下异常.
         */
        try
        {
            // 转账
            accountService.transfer(from, to, 500.0);

        }catch (Exception e)
        {
            System.err.println("程序发生异常了, 异常信息: " + e.getMessage()) ;
        }

        System.out.println("--------转账后账户信息----------");
        testSelectAllAccount();
    }

    @Test
    public void testSelectAllAccount()
    {
        // 查询所有账户
        List<Account> accounts = accountService.selectAllAccount();

        System.out.println(accounts);
    }
}
12345678910111213141516171819202122232425262728293031323334353637383940414243
```

### 1.12 测试结果

> --------转账前账户信息----------
> [Account{id=1, name=‘cloud’, money=5000.0}, Account{id=2, name=‘tom’, money=5000.0}]
> --------转账后账户信息----------
> 程序发生异常了, 异常信息: / by zero
> [Account{id=1, name=‘cloud’, money=5000.0}, Account{id=2, name=‘tom’, money=5000.0}]

## 2. 结语

- 相对于XML配置方式来说, 注解方式简化了很多。
- XML配置方式请查看： [Spring 事务, 基于XML方式.](https://blog.csdn.net/ZY4444444/article/details/109575241)