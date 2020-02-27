**1.配置环境变量**

我的电脑->属性->高级系统属性->高级->环境变量->path，点击编辑，进入页面后点击新增，将mysql的Path复制进来,

例如我的就是：C:\Program Files\MySQL\MySQL Server 8.0\bin

2、修改my-default.ini（如果没有就新增.ini文件）

在其中修改或添加配置：

```ini
[mysqld] 

basedir=C:\Program Files\MySQL\MySQL Server 8.0（mysql所在目录） 

datadir=C:\Program Files\MySQL\MySQL Server 8.0\data （mysql所在目录\data）
```

3、以管理员身份运行cmd（一定要以管理员权限运行，不然后面生成data文件夹时会报权限不够的错误，报错信息：Install/Remove of the Service Denied!）

命令切换到mysql安装路径下的bin文件夹

```
cd C:\Program Files\MySQL\MySQL Server 8.0\bin
```
输入`mysqld -install`命令以在文件路径下自动生成data目录（注：使用`mysqld --initialize`命令前，不要自己创建data目录，有data目录就先删除，再使用`mysqld --initialize`，否则无法正常生成全部data目录文件。）