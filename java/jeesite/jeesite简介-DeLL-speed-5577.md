- JeeSite 是一个 Java EE 企业级快速开发平台，基于经典技术组合（Spring Boot、Spring MVC、Apache Shiro、MyBatis、Beetl、Bootstrap、AdminLTE）在线代码生成工具。

![1555503350437](E:\课程文件\markdown\image\%5CUsers%5Cvip87%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1555503350437.png)



赶紧去克隆了jeesite的源代码先，克隆地址为 https://gitee.com/thinkgem/jeesite4.git

克隆命令，大家都会了吧，git clone https://gitee.com/thinkgem/jeesite4.git

 

克隆下来之后，不要直接就把项目放到工作空间哦，这样是不行的哦...



1、拷贝刚刚克隆下来的jeesite中的web文件夹，到你的工作空间下，重命名为你的工程名，如你取的名字，叫做：jeesite-web



 

2、打开pom.xml文件，修改第13行，artifactId为你刚刚新起的工程名，如：<artifactId>jeesite-web</artifactId>

 



3、导入到idea，这个步骤，你就不用多说了


4、这时，idea的会自动加载Maven依赖包，初次加载会比较慢（根据自身网络情况而定）

5、初始化数据库，这个时候在mysql命令行窗口输入以下命令

set global read_only=0;

set global optimizer_switch='derived_merge=off';

create user 'jeesite'@'%' identified by 'jeesite';

create database jeesite DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

grant all privileges on jeesite.* to 'jeesite'@'%' identified by 'jeesite';

flush privileges;

6、打开你刚刚的工程jeesite-web下的jeesite.xml，配置以下数据库连接，目录： /src/main/resources/config/jeesite.yml

 



 

 

 

7、你刚刚的项目，如果导入jar包加载没出错的话，就可以执行脚本运行初始化我们的数据库了，哈哈哈...你家你，曾经就是在这里奋斗了很久...

记得哦，你运行的脚本是init-data.bat！



你们，看着提示一直进行下去就好，虽然运行的时候，你会觉得像黑客一样，哗啦啦的一堆代码的东西在滚动，但是别紧张，运行成功后，你会开森得不要不要的，哈哈哈...



看到这个的时候，你仿佛看到了曙光！！！

8、因为这个是springboot的项目，所以呢，我们就不用tomcat了，汤姆猫被抛弃了，啊啊啊啊啊...

9、请你打开application.yml，对，没错，它还是在刚刚的目录

就在工程目录下的src/main/resources/config/，

配置你的服务端口port、部署路径context-path，例如:




server:

  port: 8080
  context-path: /jeesite-web
  tomcat:
  uri-encoding: UTF-8


好啦，离成功就差最后一步，运行了

 

点击idea的运行，大功告成！哈哈哈

 



 

 

 

是不是看到这个很激动了，感觉上天了呢，别急！

输入登录信息先咯，你~

用户名：system   密码：admin

 


