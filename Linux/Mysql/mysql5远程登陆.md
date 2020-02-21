1. “试一下”能否远程登录

`\> mysql -u root -p -h 10.0.42.180`

2. 登录数据库,默认本地访问

`\> mysql -u root -p`

3. 切换mysql数据库

`mysql> use mysql`

4. 执行查询，查看数据库当前有访问权限的信息

`mysql> SELECT User, Password, Host FROM user;`

只有localhost才能访问

5. 设置访问权限

`mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'root' WITH GRANT OPTION;`

其中"*.*"代表所有资源所有权限， “'root'@%”其中root代表账户名，%代表所有的访问地址，也可以使用一个唯一的地址进行替换，只有一个地址能够访问。如果是某个网段的可以使用地址与%结合的方式，如10.0.42.%。IDENTIFIED BY 'root'，这个root是指访问密码。WITH GRANT OPTION允许级联授权

6. 刷新访问权限表（不要忘了）

`mysql> FLUSH PRIVILEGES;`

7. 重新查看用户表

`mysql> SELECT User, Password, Host FROM user;`

8. 验证远程访问

`\> mysql -u root -p -h 10.0.42.180`