 1.修改配置文件

vi /etc/my.cnf 在[mysqld] 后面添加下面的代码

```cnf
sync_binlog=1
binlog-do-db=mydemo #设置需要同步的数据库名
binlog_ignore_db=mysql  #不需要同步的数据库  如果有多个不需要同步则依次列出
# binlog_ignore_db=xxxx
```


2. 重新启动mysql 服务器

```bash
 service mysqld restart
```

3.进入mysql平台　设置需要同步的用户账号
```bash
  create user '用户名'@'从服务器ip地址' identified by '密码'
  create user 'eduask'@'10.0.0.66' identified by 'test123'  #创建一个从服务器用户同步到主服务器
```


4. 给slave 用户赋权限
```mysql
grant all on *.* to 'eduask'@'10.0.0.%' identified by 'test123'
grant replication slave on *.* to 'eduask'@'10.0.0.66' identified by 'test123' #允许从服务器用户远程登录
```


5.刷新授权表
```mysql
flush privileges
```

6.给数据库创建测试数据库
```mysql
create table userinfo(
    uid int primary key auto_increment,
    uname char(30) not null,
    upwd char (30) not null
  );
  insert into userinfo (uid,uname,upwd) values(null,'aaa','test123')
```

 7.退出系统  
```mysql
   exit
```


8. 导出主服务器数据库 传递到从服务器
```mysql
   
 ------------从服务器配置完成后再操作-----------
  /usr/local/web/mysql/bin/mysqldump -uroot -ptest123 数据库名 > /home/数据库名.sql
  scp 从哪里 到哪里
  scp /home/数据库名.sql root@10.0.0.66:/home
  [提示： 如果系统提示你是否远程传输 yes]
  系统会提示你输入密码： test123
```


9.重新启动主服务器
```bash
  service mysqld stop 
  service mysqld start
```
10. 进入mysql 平台
```bash
/usr/local/web/mysql/bin/mysql -uroot -ptest123
```
11.查询主服务器状体 master
```mysql
    show master status;
    #运行后mysql 会显示一个当前master 的服务器日志状况表，我们需要记住两个字段的值
    #file: mysql-bin.000006 #这是需要同步的master 的二进制文件
    #position:519
```

 

12. 配置从服务器

```mysql
#1.进入mysql 平台
/usr/local/web/mysql/bin/mysql -uroot -ptest123
#2.创建数据库
create database mydemo;
eixt
#3.导入master 的数据库
/usr/local/web/mysql/bin/mysql -uroot -ptest123 数据库名 < /home/数据库名.sql
#4.配置从服务器mysql 配置文件
vi /etc/my.cnf
#找到 server-id=1 改成 2 （注意：server-id 必须要比master 的大 关键是不能重复）
#在 [mysqld] 后面添加
replicate-do-db=mydemo #配置需要同步的数据库名
#保存文件
:wq
#5.重启mysqld
service mysqld restart
#6.以root用户登录mysql　平台
/usr/local/web/mysql/bin/mysql -uroot -ptest123
stop slave;
change master to master_host=``'10.0.0.12'``, #master ip
master_user='eduask', #master 用户名
master_password='test123', #master 密码
master_port=3306,  #master 端口
master_log_file='mysql-bin.000006', #master 需要同步的二进制
master_log_pos=519, #master pos
master_connect_retry=60; #连接master 的时间
#7.启动slave
start slave;
#8. 查看是否连接同步成功
show slave status\G;
#如果 SLAVE_IO_running yes
slave_sql_running yes
#这两个参数都为 yes 则表示成功 否则失败
```


 测试主从数据库同步

```mysql
 #1.进入主服务器
/usr/local/web/mysql/bin/mysql -uroot -ptest123
use mydemo;
insert into userinfo(uid,uname,upwd) values(null'111','222');
 #2. 进入从服务器
/usr/local/web/mysql/bin/mysql -uroot -ptest123
use mydemo;
select * from userinfo;
#如果从服务器中有master 的数据 成功
```
