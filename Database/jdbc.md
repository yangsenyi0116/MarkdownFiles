### jdbc.properties

```properties
jdbc.driverClass=com.mysql.jdbc.Driver
jdbc.Url=jdbc:mysql://localhost:3306/jdbctest?serverTimezone=GMT%2B8&useSSL=false
jdbc.user=root
jdbc.password=root
```



	// 生成SQL
	// 加载驱动（基于驱动创建连接）
	// 创建连接
	// 创建执行sql语句的对象
	// 执行sql
	// 获取结果并处理
	// 释放资源