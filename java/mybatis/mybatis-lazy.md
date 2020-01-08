映射文件编写（使用）



这里使用高级映射resultMap，在resultMap内使用association标签自定义嵌套部门信息，其中：
property---JavaBean内成员变量名，这里就是员工实体类里定义的部门类变量名

select---指定查询方法，命名方式为对应XML文件的命名空间+查询ID（Mapper接口的包路径+方法名）

column---参数列名，指下一步查询的参数对应的列名。这里查询部门需要用到的参数为员工主键，因此此处的column应为员工主键的数据库列名

```xml
    <resultMap type="com.xuyong.entity.Employee" id="MyEmpByStep">
        <id column="id" property="id"/>
        <result column="last_name" property="lastName"/>
        <result column="gender" property="gender"/>
        <result column="email" property="email"/>
        <association property="department" select="com.xuyong.dao.DepartmentDao.getDepartmentById" column="id">
        </association>
    </resultMap>
```

resultType进行映射，开发中建议使用resultMap

二、延迟加载

什么是延迟加载？

当我们在某项业务里需要同时获取A、B两份数据，但是B这份数据又不需要立即使用（或者存在压根就不会使用的情况），当程序需要加载B时，再去请求数据库来获取B数据，而不是一次性将数据全部取出来或者重新发送一份请求，这就是延迟加载。

MyBatis默认关闭延迟加载技术，需要我们在配置文件里手动配置，配置如下：

```XML
<!-- 设置 -->
    <settings>
    	<!-- 驼峰命名映射 -->
    	<setting name="mapUnderscoreToCamelCase" value="true"/>
    	
    	<setting name="jdbcTypeForNull" value="NULL"/>
    	
    	<!-- 懒加载设置 -->
    	<setting name="lazyLoadingEnabled" value="true"/>  
    	<!-- 侵入懒加载，设置为false则按需加载，否则会全部加载 -->
    	<setting name="aggressiveLazyLoading" value="false"/>
    	
    	<!-- 标准日志输出 -->
    	<!--<setting name="logImpl" value="STDOUT_LOGGING"/>-->
    	
    	<!-- log4j日志输出 -->
    	<setting name="logImpl" value="LOG4J"/>
    	
    </settings>
```

**注意：**

1. lazyLoadingEnabled与aggressiveLazyLoading必须全部设置，且lazyLoadingEnabled为true，aggressiveLazyLoading为false才能让延迟加载真正生效

2.  toString与重载方法过滤：

     通常我们在测试时会在实体类加入toString，或者存在了一些重载方法，这些MyBatis会对其进行过滤，但是过滤会调     用cglib与asm指定包，因此要将两个包添加到buildpath。以下为两个包的maven依赖：

```xml
         <dependency>
	    	<groupId>cglib</groupId>
		<artifactId>cglib</artifactId>
		<version>3.1</version>
	 </dependency>
 
        <dependency>
            <groupId>asm</groupId>
            <artifactId>asm</artifactId>
            <version>3.3.1</version>
        </dependency>
```

3.如果想单个开启或禁用延迟加载，可以使用fetchType属性来实现

```xml
<!-- collection分布查询 -->
        <resultMap type="com.xuyong.entity.Department" id="DepartAndEmpByStep">
        <id column="id" property="id"/>
        <result column="department_name" property="departmentName"/>
        <!-- 多个值传递可封装成map如：column="{key1=column1,...}"
             fetchType="lazy" 表示使用懒加载   fetchType="eager"表示禁用懒加载
        -->
        <collection property="employee" select="com.xuyong.dao.EmployeeDaoPlus.getEmpByDid" column="{id=id}" fetchType="lazy"></collection>
        </resultMap>
```
