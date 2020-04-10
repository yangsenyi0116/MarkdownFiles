## JDBC

1. spring基础的jar包
2. jdbc基础的jar包

```xml
<!--JDBC的支持-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-jdbc</artifactId>
      <version>5.1.5.RELEASE</version>
    </dependency>

    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.15</version>
    </dependency>

<!--数据库连接池-->
      <dependency>
          <groupId>com.mchange</groupId>
          <artifactId>c3p0</artifactId>
          <version>0.9.5.2</version>
      </dependency>
```

### 配置连接池

```xml
<context:property-placeholder location="jdbc.properties" />

<!--配置连接池-->
<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource" destroy-method="close">
    <property name="driverClass" value="${jdbc.driverClassName}"></property>
    <property name="jdbcUrl" value="${jdbc.url}"></property>
    <property name="user" value="${jdbc.username}"></property>
    <property name="password" value="${jdbc.password}"></property>
</bean>
```

### spring中的log4j

```xml
<dependency>
	<groupId>org.slf4j</groupId>
	<artifactId>slf4j-log4j12</artifactId>
	<version>1.7.21</version>
</dependency>
```

在资源文件中导入log4j.xml 的log4j的配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">

<log4j:configuration>

    <!-- 将日志信息输出到控制台 -->
    <appender name="ConsoleAppender" class="org.apache.log4j.ConsoleAppender">
        <!-- 设置日志输出的样式 -->
        <layout class="org.apache.log4j.PatternLayout">
            <!-- 设置日志输出的格式 -->
            <param name="ConversionPattern" value="[%d{yyyy-MM-dd HH:mm:ss:SSS}] [%-5p] [method:%l]%n%m%n%n" />
        </layout>
        <!--过滤器设置输出的级别-->
        <filter class="org.apache.log4j.varia.LevelRangeFilter">
            <!-- 设置日志输出的最小级别 -->
            <param name="levelMin" value="WARN" />
            <!-- 设置日志输出的最大级别 -->
            <param name="levelMax" value="ERROR" />
            <!-- 设置日志输出的xxx，默认是false -->
            <param name="AcceptOnMatch" value="true" />
        </filter>
    </appender>

    <!-- 将日志信息输出到文件，但是当文件的大小达到某个阈值的时候，日志文件会自动回滚 -->
    <appender name="RollingFileAppender" class="org.apache.log4j.RollingFileAppender">
        <!-- 设置日志信息输出文件全路径名 -->
        <param name="File" value="D:/log4j/RollingFileAppender.log" />
        <!-- 设置是否在重新启动服务时，在原有日志的基础添加新日志 -->
        <param name="Append" value="true" />
        <!-- 设置保存备份回滚日志的最大个数 -->
        <param name="MaxBackupIndex" value="10" />
        <!-- 设置当日志文件达到此阈值的时候自动回滚，单位可以是KB，MB，GB，默认单位是KB -->
        <param name="MaxFileSize" value="10KB" />
        <!-- 设置日志输出的样式 -->
        <layout class="org.apache.log4j.PatternLayout">
            <!-- 设置日志输出的格式 -->
            <param name="ConversionPattern" value="[%d{yyyy-MM-dd HH:mm:ss:SSS}] [%-5p] [method:%l]%n%m%n%n" />
        </layout>
    </appender>

    <!-- 将日志信息输出到文件，可以配置多久产生一个新的日志信息文件 -->
    <appender name="DailyRollingFileAppender" class="org.apache.log4j.DailyRollingFileAppender">
        <!-- 设置日志信息输出文件全路径名 -->
        <param name="File" value="D:/log4j/DailyRollingFileAppender.log" />
        <!-- 设置日志每分钟回滚一次，即产生一个新的日志文件 -->
        <param name="DatePattern" value="'.'yyyy-MM-dd-HH-mm'.log'" />
        <!-- 设置日志输出的样式 -->
        <layout class="org.apache.log4j.PatternLayout">
            <!-- 设置日志输出的格式 -->
            <param name="ConversionPattern" value="[%d{yyyy-MM-dd HH:mm:ss:SSS}] [%-5p] [method:%l]%n%m%n%n" />
        </layout>
    </appender>


    <!--
     注意：
     1：当additivity="false"时，root中的配置就失灵了，不遵循缺省的继承机制
     2：logger中的name非常重要，它代表记录器的包的形式，有一定的包含关系，试验表明
        2-1：当定义的logger的name同名时，只有最后的那一个才能正确的打印日志
        2-2：当对应的logger含有包含关系时，比如：name=test.log4j.test8 和 name=test.log4j.test8.UseLog4j，则2-1的情况是一样的
        2-3：logger的name表示所有的包含在此名的所有记录器都遵循同样的配置，name的值中的包含关系是指记录器的名称哟！注意啦！
     3：logger中定义的level和appender中的filter定义的level的区间取交集
     4：如果appender中的filter定义的 levelMin > levelMax ，则打印不出日志信息
     -->

    <!-- 指定logger的设置，additivity指示是否遵循缺省的继承机制-->
    <logger name="test.log4j.test8.UseLog4j" additivity="false">
        <level value ="WARN"/>
        <appender-ref ref="DailyRollingFileAppender"/>
    </logger>

    <!--指定logger的设置，additivity指示是否遵循缺省的继承机制 -->
    <logger name="test.log4j.test8.UseLog4j_" additivity="false">
        <level value ="ERROR"/>
        <appender-ref ref="RollingFileAppender"/>
    </logger>

    <!-- 根logger的设置-->
    <root>
        <level value ="INFO"/>
        <appender-ref ref="ConsoleAppender"/>
        <!--<appender-ref ref="DailyRollingFileAppender"/>-->
    </root>

</log4j:configuration>
```

## 事务管理

为了保证数据的安全，必须在业务层中进行事务的管理。

### spring事务管理API

- PlatformTransactionManager

  spring提供的事务管理器，可以选择不同的事务平台，来进行事务管理

  - DataSourceTransactionManager:对jdbctemplate和mybatis实现事务管理
  - HibernateTransactionManager:对Hibernate实现事务管理
  - JpaTransactionManager:对JPA实现事务管理

- TransacionDefinition

  接口定义了事务的相关信息(隔离，传播，超时)

- TransactionStatus

  接口，描述事务的具体状态

  

  

### 事务的基本属性

##### 隔离性

##### 传播性

##### 超时性

##### 只读

##### 异常

Exception 自动提交

RuntimeExcetion 回滚





### 声明式事务管理

导入依赖

```xml
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-tx</artifactId>
  <version>5.1.5.RELEASE</version>
</dependency>
```

```xml
<bean class="org.springframework.jdbc.datasource.DataSourceTransactionManager" id="txManager">
    <property name="dataSource" ref="dataSource"/>
</bean>xml
```

  #### tx:advice 配置事务通知

```xml
    <!--配置通知-->
    <tx:advice id="txAdvice" transaction-manager="txManager">
        <!--配置事务的属性-->
        <tx:attributes>
            <!--
                name定义哪些方法进行事务管理
                isolation：隔离属性 默认值为isolation_default
                propagation:传播性 默认值为REQUIRED
                read-only：只读性 默认为false
                timeout：超时效
                rollback-for：需要回滚的异常类型
                no-rollback-for：不需要回滚的异常类型
            -->
            <tx:method name="save*"/>
            <tx:method name="query*" read-only="true"/>
            <!--其他方法采用默认配置-->
            <tx:method name="*" />
        </tx:attributes>
    </tx:advice>
```

### 代理类

```xml
<!--切面配置-->
<aop:config>
    <!--service层impl下的所有的类-->
    <aop:pointcut id="txPointCut" expression="within(kermi.project.maven.spring.service.impl.*)"/>
    <aop:advisor advice-ref="txAdvice" pointcut-ref="txPointCut"/>
</aop:config>	
```