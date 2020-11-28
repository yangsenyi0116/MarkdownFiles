之前使用jdbc操作数据库，并使用事务的时候是这样操作：

```
    Connection connection = null;
		try {
			//connection = getConnection(....);//1.封装获取connection
			connection.setAutoCommit(false);   //2.设置为手动提交事务

			String execSql = "select 0";
			PreparedStatement ps =        connection.prepareStatement(execSql);//3.获取PreparedStatement
			ps.executeUpdate();//4.执行语句
			
			connection.commit(); //5.如果所有sql语句成功，则提交事务
			
			ps.close();//6.关闭
			connection.close();
		} catch (Exception e) {
			connection.rollback();//7.有错误回滚
			
			connection.close();
		}
```

呃，代码太臃肿了。不过幸亏有了spring，开发效率提升不知道多少倍。

## OK,直接引入spring

## 本文是基于全注解，没有一丝xml代码配置。

**1.数据库配置**

所有的数据库操作，首先要有一个数据库，这里使用内存数据库h2

如下，代码清单1:

```
package wang.conge.springdemo.transaction.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
public class DataSourceConfig {
	
	@Bean
	public DataSource dataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase h2db = builder
			.setType(EmbeddedDatabaseType.H2) //H2
			.addScript("db/sql/create-db.sql")
			.addScript("db/sql/insert-data.sql")
			.build();
		
		return h2db;
	}
}
```

上述创建数据库的过程中，执行了两个SQL初始文件，非常简单，一个创建表，一个插入一些简单数据。

如下，代码清单2,3：

```
CREATE TABLE users (
  id     INTEGER PRIMARY KEY,
  name   VARCHAR(30),
  email  VARCHAR(50)
);

INSERT INTO users VALUES (1, 'kaka', 'kaka@qq.com');
INSERT INTO users VALUES (2, 'alex', 'alex@163.com');
INSERT INTO users VALUES (3, 'lucy', 'lucy@gmail.com');
```

**2.开启spring事务**

如下，代码清单4：

```java
package wang.conge.springdemo.transaction.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DBCommonConfig {
	
	@Bean
	public PlatformTransactionManager transactionManager(DataSource datasource){
		return new DataSourceTransactionManager(datasource);
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource datasource){
		return new JdbcTemplate(datasource);
	}
	
}
```

- 代码使用EnableTransactionManagement注解，开启spring事务
- 使用DataSourceTransactionManager去管理事务，基于数据库连接池
- 使用JdbcTemplate 简单封装去操作数据库

**3.业务代码**

模拟一个插入表数据，查询表，即使用JdbcTemplate非常简单。

如下，代码清单5：

```java
package wang.conge.springdemo.transaction.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class UserDao {
	@Autowired JdbcTemplate jdbcTemplate;
	
	private final String insert_sql = "INSERT INTO users(id,name,email) VALUES (?, ?,?)";
	
	private final String query_sql = "select id,name,email from users";
	
	public void testInsert(int id,String name,String email){
		jdbcTemplate.update("update users set name = ? where id = ?","kakanew",1);//这句一定正常执行
		
		System.out.println("执行正常SQL后结果：===");
		queryAll();
		
		jdbcTemplate.update(insert_sql, id,name,email);//有可能因为主键冲突异常
	}
	
	public void queryAll(){
		List<Map<String, Object>> list = jdbcTemplate.queryForList(query_sql);
		
		for(Map<String, Object> map:list){
			System.out.println(map);
		}
	}
}
```

- 在类上面注解了@Transactional，意思是这个类的所有方法交给spring事务管理。而该注解也可以加到方法上，则只有该方法被spring事务管理。

**4.运行配置**

OK，上面的基本配置都有了，数据库也有了，事务管理也有了，那么只需要总的运行配置了。

如下，代码清单6：

```java
package wang.conge.springdemo.transaction.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
	DataSourceConfig.class,
	DBCommonConfig.class
})
@ComponentScan("wang.conge.springdemo.transaction.service")
public class AppConfig {
	
}
```

运行，代码清单7：

```java
package wang.conge.springdemo.transaction;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import wang.conge.springdemo.transaction.config.AppConfig;
import wang.conge.springdemo.transaction.service.UserDao;

public class AppStart {

	public static void main(String[] args) {
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

		UserDao myService = applicationContext.getBean(UserDao.class);

		try {
			System.out.println("执行SQL前结果：===");
			myService.queryAll();

			myService.testInsert(2, "wow", "wow@163.com");// 主键冲突，数据回滚
		} catch (Exception e) {
			System.out.println(e);
		}

		System.out.println("异常回滚SQL后结果：===");
		myService.queryAll();
	}

}
```

哦，还少一个pom文件，代码清单8:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
<!-- spring -->
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-beans</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-aop</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-context-support</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
</dependency>
```

**5.运行结果**

OK,基本上整个DEMO就结束了，运行结果也比较简单，但是也表达了事务性，雨果遇到异常则会回滚。

```
执行SQL前结果：===
{ID=1, NAME=kaka, EMAIL=kaka@qq.com}
{ID=2, NAME=alex, EMAIL=alex@163.com}
{ID=3, NAME=lucy, EMAIL=lucy@gmail.com}

执行正常SQL后结果：===
{ID=1, NAME=kakanew, EMAIL=kaka@qq.com}
{ID=2, NAME=alex, EMAIL=alex@163.com}
{ID=3, NAME=lucy, EMAIL=lucy@gmail.com}

信息: SQLErrorCodes loaded: [DB2, Derby, H2, HSQL, Informix, MS-SQL, MySQL, Oracle, PostgreSQL, Sybase, Hana]
org.springframework.dao.DuplicateKeyException: PreparedStatementCallback; SQL [INSERT INTO users(id,name,email) VALUES (?, ?,?)]; Unique index or primary key violation: "PRIMARY KEY ON PUBLIC.USERS(ID)"; SQL statement:
INSERT INTO users(id,name,email) VALUES (?, ?,?) [23505-191]; nested exception is org.h2.jdbc.JdbcSQLException: Unique index or primary key violation: "PRIMARY KEY ON PUBLIC.USERS(ID)"; SQL statement:
INSERT INTO users(id,name,email) VALUES (?, ?,?) [23505-191]

异常回滚SQL后结果：===
{ID=1, NAME=kaka, EMAIL=kaka@qq.com}
{ID=2, NAME=alex, EMAIL=alex@163.com}
{ID=3, NAME=lucy, EMAIL=lucy@gmail.com}
```