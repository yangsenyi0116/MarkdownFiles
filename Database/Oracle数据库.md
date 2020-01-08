## Oracle概念

### 用户与模式

- 用户（user）:Oracle用户是用连接数据库和访问数据库对象的。（用户是用来连接数据库访问数据库）。
- 模式(schema)：模式是数据库对象的集合。模式对象是数据库数据的逻辑结构。（把数据库对象用模式分开成不同的逻辑结构）。
- 用户（user）与模式(schema)的区别：用户是用来连接数据库对象。而模式用是用创建管理对象的。(模式跟用户在oracle 是一对一的关系。)



## 2019-06-03

### 数据库的作用

- 储存数据
- 检索数据



### C/S 和 B/S

#### C/S又称Client/Server或客户/服务器模式。服务器通常采用高性能的PC、工作站或小型机，并采用大型数据库系统，如Oracle、Sybase、Informix或SQLServer。

#### B/S是Brower/Server的缩写，客户机上只要安装一个浏览器（Browser），如Netscape Navigator或Internet Explorer，服务器安装Oracle、Sybase、Informix或SQL Server等数据库。



#### Client/Server是建立在局域网的基础上的。

#### Browser/Server是建立在广域网的基础上的.



#### C/S 一般建立在专用的网络上, 小范围里的网络环境, 局域网之间再通过专门服务器提供连接和数据交换服务.

#### B/S 建立在广域网之上的, 不必是专门的网络硬件环境,例与电话上网, 租用设备. 信息自己管理. 有比C/S更强的适应范围, 一般只要有操作系统和浏览器就行。





```sql
--创建用户
CREATE USER c##dm_user IDENTIFIED BY system;

--赋予连接权限
GRANT CONNECT TO c##dm_user

--查询
SELECT * FROM scott.emp

--给dm_user赋予查询scott.emp的权限
GRANT SELECT TO scott.emp TO dm_user

--给用户赋予DBA角色的所有权限
GRANT DBA TO C##DM_USER

--取消权限
REVOKE DBA FROM C##DM_USER

--修改用户密码
ALTER USER C##DM_USER IDENTIFIED BY 123456

--删除用户
DROP USER C##DM_USER CASCADE;

grant create session to C##DM_USER
GRANT CREATE TABLE to C##DM_USER
```





#### oracle中不存在库的关系，只有用户的关系



#### 创建表

```sql
CREATE TABLE dm_student(
	stu_code char(8),
	stu_name varchar(50),
	stu_age INTEGER
);
```



##### 1.char 

> char的长度是固定的，比如说，你定义了char(20),即使你你插入abc，不足二十个字节，数据库也会在abc后面自动加上17个空格，以补足二十个字节； 
>
> char是区分中英文的，中文在char中占两个字节，而英文占一个，所以char(20)你只能存20个字母或10个汉字。 
>
> char适用于长度比较固定的，一般不含中文的情况 

##### 2.varchar/varchar2

> varchar是长度不固定的，比如说，你定义了varchar(20),当你插入abc，则在数据库中只占3个字节。 
>
> varchar同样区分中英文，这点同char。 
>
> varchar2基本上等同于varchar，它是oracle自己定义的一个非工业标准varchar，不同在于，varchar2用null代替varchar的空字符串 
>
> varchar/varchar2适用于长度不固定的，一般不含中文的情况 

##### 3.nvarchar/nvarchar2

> nvarchar和nvarchar2是长度不固定的 
>
> nvarchar不区分中英文，比如说：你定义了nvarchar(20),你可以存入20个英文字母/汉字或中英文组合，这个20定义的是字符数而不是字?





### 数据库的作用

​	准确安全的保存数据



## 2019-06-04

### 数据库完整性包括

- 域完整性
- 实体完整性
- 引用完整性
- 自定义完整性



### 约束

![1559615968025](G:\onedriver\OneDrive\MarkDown\image\1559615968025.png)

![1559618114510](G:\onedriver\OneDrive\MarkDown\image\1559618114510.png)

### 代码实例

```sql
--主表
CREATE TABLE dm_class(
	cls_id INTEGER NOT NULL PRIMARY KEY,
	cls_code VARCHAR(10) NOT NULl UNIQUE,
	cls_name VARCHAR(20) NOT NULL
);

--从表
CREATE TABLE dm_stu1(
	stu_id INTEGER NOT NULL PRIMARY KEY,
	stu_sex CHAR(3) DEFAULT '女' NOT NULL,
	stu_phone VARCHAR2(20) NULL,
	stu_code CHAR(8) not NULL UNIQUE,
	stu_score NUMBER(4,1),
	stu_birthday DATE,
	stu_cls_id INTEGER NOT NULL,
	CONSTRAINT ck_stu_score CHECK (stu_score<=150 AND stu_score>=0),
	CONSTRAINT fk_stu_class_id FOREIGN KEY (stu_cls_id) REFERENCES dm_class(cls_id)
);

INSERT INTO dm_class(cls_id,cls_code,cls_name) VALUES (1,'1','一班');

INSERT INTO dm_stu1(stu_id) VALUES (1);
INSERT INTO dm_stu1(stu_id,stu_sex,STU_CODE) VALUES (2,'男','00000001');
INSERT INTO DM_STU1(STU_ID,STU_SEX,STU_CODE,STU_BIRTHDAY,STU_CLS_ID) VALUES (4,'男','00000003',TO_DATE('1999-01-03', 'yyyy-MM-dd'),1);

SELECT * FROM dm_stu1;

drop table dm_stu1;
```



```sql
CREATE TABLE DM_DEMO(
	DM_NAME INTEGER
);

--修改
--表名
ALTER TABLE dm_demo 
   RENAME TO dm_demo2;
--列
--  添加列
ALTER TABLE dm_demo2
  ADD dm_name VARCHAR2(20);
--  列名
ALTER TABLE dm_demo2
  RENAME COLUMN dm_name TO dm_name2;
--  类型
ALTER TABLE dm_demo2
  MODIFY (dm_name2 VARCHAR2(10) DEFAULT 'abc');
--  非空
ALTER TABLE dm_demo2
  MODIFY (dm_name2 VARCHAR2(10) DEFAULT 'abc' NOT NULL);--根据实际情况考虑
--  default
--  删除
ALTER TABLE dm_demo2
  DROP COLUMN dm_name2;
--约束
--  添加
--  唯一
--  check
ALTER TABLE dm_demo2
  ADD dm_age INTEGER;
ALTER TABLE dm_demo2
  ADD CONSTRAINT ck_dm_age CHECK (dm_age>0 AND dm_age<150);
ALTER TABLE dm_demo2
  DROP CONSTRAINT ck_dm_age;
--  pk
ALTER TABLE dm_demo2
  ADD CONSTRAINT pk_dm_id PRIMARY KEY (dm_id);
--  外键
ALTER TABLE dm_demo2
  ADD dm_cls_id INTEGER NOT NULL;
ALTER TABLE dm_demo2
  ADD CONSTRAINT fk_dm_cls_id FOREIGN KEY (dm_cls_id) REFERENCES dm_class (cls_id);
--  删处约束
```



### COMMIT&ROLLBACK

- commit（提交）
- rollback（回滚）

>rollback只能回滚commit之前的数据



### 与java整合

![1559636646567](G:\onedriver\OneDrive\MarkDown\image\1559636646567.png)




```java
public static void main( String[] args ) throws ClassNotFoundException, SQLException {
	String sql = "INSERT INTO DM_CLASS VALUES(4,'4','四班')";
    Class.forName("oracle.jdbc.driver.OracleDriver");
    String url = "jdbc:oracle:thin:@DeLL-speed-5577:1521:ORCL";
    String user = "c##dm_user";
    String password = "system";
    Connection connection = DriverManager.getConnection(url,user,password);
    Statement statement = connection.createStatement();

    int  count = statement.executeUpdate(sql);

    if(count>0) {
        System.out.println("插入成功");
    }else{
        System.out.println("插入失败");
    }

    statement.close();
    connection.close();
}
```



## 2019-06-05

---

### 数据的更新，sql中的运算符，删除



![1559703897367](G:\onedriver\OneDrive\MarkDown\image\1559703897367.png)

DML

>- INSERT
>- UPDATE
>- DELETE 
>- SELECT

DDL

> - CREATE
> - ALTER
> - DROP

DCL

>- GRANT
>- REVOKE



powerdesigner的使用

从概念模型到物理模型到生成sql脚本



## 2019-06-06

---

### 查询，子查询

>group by <> having

![1559805526721](G:\onedriver\OneDrive\MarkDown\image\1559805526721.png)

#### 函数

avg count sum max min

#### 建立序列

#### 删除表数据

![1559806655182](G:\onedriver\OneDrive\MarkDown\image\1559806655182.png)



![1559806712182](G:\onedriver\OneDrive\MarkDown\image\1559806712182.png)



#### 获取新插入的主键

```java
ResultSet rs = preparedstatement.getgeneratedKeys();
if(rs.next()){
    System.out.println("id=" + rs.getInt(1));
}

```

