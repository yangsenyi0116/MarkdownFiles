1.创建目录和配置文件my.cnf

```bash
mkdir /docker
mkdir /docker/mysql
mkdir /docker/mysql/conf
mkdir /docker/mysql/data
 
创建my.cnf配置文件
touch /docker/mysql/conf/my.cnf
 
my.cnf添加如下内容：
[mysqld]
user=mysql
character-set-server=utf8
default_authentication_plugin=mysql_native_password
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
```

2.创建容器，并后台启动

```bash
docker run -d -p 3306:3306 --privileged=true -v /docker/mysql/conf/my.cnf:/etc/mysql/my.cnf -v /docker/mysql/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 --name mysqltest2 mysql:5.7
```

参数说明：

--privileged=true：容器内的root拥有真正root权限，否则容器内root只是外部普通用户权限

-v /docker/mysql/conf/my.cnf:/etc/my.cnf：映射配置文件

-v /docker/mysql/data:/var/lib/mysql：映射数据目录

