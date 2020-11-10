```properties
#mybatis
spring.datasource.url=jdbc:oracle:thin:@DeLL-speed-5577:1521:ORCL
spring.datasource.username=c##scott
spring.datasource.password=scott
spring.datasource.driver-class-name=oracle.jdbc.driver.OracleDriver
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource

#初始化大小，最小，最大
spring.datasource.initialSize=5  
spring.datasource.minIdle=5  
spring.datasource.maxActive=20  
#配置获取连接等待超时的时间
spring.datasource.maxWait=60000


mybatis.mapper-locations=classpath*:mapper/*Mapper.xml
mybatis.type-aliases-package=com.kermi.project1.pojo
```

```xml
<!-- https://mvnrepository.com/artifact/org.mybatis.spring.boot/mybatis-spring-boot-starter -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.0.1</version>
</dependency>

<!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.17</version>
</dependency>
```