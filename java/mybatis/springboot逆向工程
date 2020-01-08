```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <context id="testTables" targetRuntime="MyBatis3">
        <commentGenerator>
            <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
            <property name="suppressAllComments" value="true" />
        </commentGenerator>
        <!--数据库连接的信息：驱动类、连接地址、用户名、密码,这里配置的是mysql的，当然也可以配置oracle等数据库 -->
        <jdbcConnection driverClass="oracle.jdbc.driver.OracleDriver"
                        connectionURL="jdbc:oracle:thin:@DeLL-speed-5577:1521:ORCL" userId="C##SCOTT"
                        password="scott">
        </jdbcConnection>

        <!-- 默认false，把JDBC DECIMAL 和 NUMERIC 类型解析为 Integer，为 true时把JDBC DECIMAL
            和 NUMERIC 类型解析为java.math.BigDecimal -->
        <javaTypeResolver>
            <property name="forceBigDecimals" value="false" />
        </javaTypeResolver>

        <!-- targetProject:生成PO类的位置 -->
        <javaModelGenerator targetPackage="com.kermi.project1.pojo"
                            targetProject=".\src">
            <!-- enableSubPackages:是否让schema作为包的后缀 -->
            <property name="enableSubPackages" value="false" />
            <!-- 从数据库返回的值被清理前后的空格 -->
            <property name="trimStrings" value="true" />
        </javaModelGenerator>
        <!-- targetProject:mapper映射文件生成的位置 -->
        <sqlMapGenerator targetPackage="com.kermi.project1.mapper"
                         targetProject=".\src">
            <!-- enableSubPackages:是否让schema作为包的后缀 -->
            <property name="enableSubPackages" value="false" />
        </sqlMapGenerator>
        <!-- targetPackage：mapper接口生成的位置 -->
        <javaClientGenerator type="XMLMAPPER"
                             targetPackage="com.kermi.project1.mapper" targetProject=".\src">
            <!-- enableSubPackages:是否让schema作为包的后缀 -->
            <property name="enableSubPackages" value="false" />
        </javaClientGenerator>
        <!-- 指定数据库表 -->
        <table tableName="BONUS" schema="C##SCOTT"></table>
        <table tableName="DEPT" schema="C##SCOTT"></table>
        <table tableName="EMP" schema="C##SCOTT"></table>
        <table tableName="SALGRADE" schema="C##SCOTT"></table>
    </context>
</generatorConfiguration>
```

```java
package com.kermi.project1.generator;


import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author : Kermi
 * @Date : 2019/6/11 12:31 下午
 * @Version : 1.0
 */
public class GeneratorStarter {

    public void generator() throws Exception{

        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        //指定 逆向工程配置文件
        String basePath = new ApplicationHome(this.getClass()).getSource().getParentFile().getPath()+"/classes/generatorConfig.xml";
        System.out.println(basePath);
        File configFile = new File(basePath);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                callback, warnings);
        myBatisGenerator.generate(null);

    }

    public static void main(String[] args) {
        try {
            GeneratorStarter generatorSqlmap = new GeneratorStarter();
            generatorSqlmap.generator();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

<https://www.cnblogs.com/wxywxy/p/10697173.html>



一、mapper接口中的方法解析

mapper接口中的部分常用方法及功能如下：

| 方法                                                         | 功能说明                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| int countByExample(UserExample example) thorws SQLException  | 按条件计数                                                   |
| int deleteByPrimaryKey(Integer id) thorws SQLException       | 按主键删除                                                   |
| int deleteByExample(UserExample example) thorws SQLException | 按条件删除                                                   |
| String/Integer insert(User record) thorws SQLException       | 插入数据（返回值为ID）                                       |
| User selectByPrimaryKey(Integer id) thorws SQLException      | 按主键查询                                                   |
| ListselectByExample(UserExample example) thorws SQLException | 按条件查询                                                   |
| ListselectByExampleWithBLOGs(UserExample example) thorws SQLException | 按条件查询（包括BLOB字段）。只有当数据表中的字段类型有为二进制的才会产生 |
| int updateByPrimaryKey(User record) thorws SQLException      | 按主键更新                                                   |
| int updateByPrimaryKeySelective(User record) thorws SQLException | 按主键更新值不为null的字段                                   |
| int updateByExample(User record, UserExample example) thorws SQLException | 按条件更新                                                   |
| int updateByExampleSelective(User record, UserExample example) thorws SQLException | 按条件更新值不为null的字段                                   |

二、Example类解析

mybatis的逆向工程中会生成实体类及实体类对应的example类，example类用于添加条件，相当where后面的部分。

xxxExample example = new xxxExample(); 

Criteria criteria = new Example().createCriteria();

example类中的部分常用方法及功能如下：

| 方法                                       | 功能说明                                    |
| ------------------------------------------ | ------------------------------------------- |
| example.setOrderByClause(“字段名 ASC”);    | 添加升序排列条件，DESC为降序                |
| example.setDistinct(false)                 | 去除重复，boolean型，true为选择不重复的记录 |
| criteria.andXxxIsNull                      | 添加字段xxx为null的条件                     |
| criteria.andXxxIsNotNull                   | 添加字段xxx不为null的条件                   |
| criteria.andXxxEqualTo(value)              | 添加xxx字段等于value条件                    |
| criteria.andXxxNotEqualTo(value)           | 添加xxx字段不等于value条件                  |
| criteria.andXxxGreaterThan(value)          | 添加xxx字段大于value条件                    |
| criteria.andXxxGreaterThanOrEqualTo(value) | 添加xxx字段大于等于value条件                |
| criteria.andXxxLessThan(value)             | 添加xxx字段小于value条件                    |
| criteria.andXxxLessThanOrEqualTo(value)    | 添加xxx字段小于等于value条件                |
| criteria.andXxxIn(List<？>)                | 添加xxx字段值在List<？>条件                 |
| criteria.andXxxNotIn(List<？>)             | 添加xxx字段值不在List<？>条件               |
| criteria.andXxxLike(“%”+value+”%”)         | 添加xxx字段值为value的模糊查询条件          |
| criteria.andXxxNotLike(“%”+value+”%”)      | 添加xxx字段值不为value的模糊查询条件        |
| criteria.andXxxBetween(value1,value2)      | 添加xxx字段值在value1和value2之间条件       |
| criteria.andXxxNotBetween(value1,value2)   | 添加xxx字段值不在value1和value2之间条件     |

在mybatis逆向工程生成的文件XxxExample.java中包含一个static的内部类Criteria，Criteria中的方法是定义SQL 语句where后的查询条件。