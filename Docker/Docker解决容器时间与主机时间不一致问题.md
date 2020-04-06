**Docker容器时间与主机时间不一致**

通过date命令查看时间

查看主机时间

```
[root@localhost ~]# date



2016年 07月 27日 星期三 22:42:44 CST
```

查看容器时间

```
root@b43340ecf5ef:/#date                                                                                                                          



Wed Jul 27 14:43:31 UTC 2016
```

可以发现，他们相隔了8小时。

CST应该是指（China Shanghai Time，东八区时间） 
UTC应该是指（Coordinated Universal Time，标准时间） 

所以，这2个时间实际上应该相差8个小时。(bluer: 所以没有设置过的容器, 一般跟宿主机时间相差8h)

所以，必须统一两者的时区。

**共享主机的localtime (方法一)**

创建容器的时候指定启动参数，挂载localtime文件到容器内 ，保证两者所采用的时区是一致的。

```bash
docker run -d -v /etc/localtime:/etc/localtime --rm -p 8888:8080 tomcat:latest
```

**复制主机的localtime (方法二)**

```bash
docker cp /etc/localtime:[容器ID或者NAME]/etc/localtime
```

在完成后，再通过date命令进行查看当前时间。 

但是，在容器中运行的程序的时间不一定能更新过来，比如在容器运行的MySQL服务，在更新时间后，通过sql查看MySQL的时间

```sql
select now() from dual;
```

可以发现，时间并没有更改过来。 

这时候必须要重启mysql服务或者重启Docker容器，mysql才能读取到更改过后的时间。

**创建自定义的dockerfile (方法三)**

创建dockerfile文件，其实没有什么内容，就是自定义了该镜像的时间格式及时区。

```bash
FROM redis FROM tomcat 
ENV CATALINA_HOME /usr/local/tomcat
#设置时区
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \  && echo 'Asia/Shanghai' >/etc/timezone \
```

保存后，利用docker build命令生成镜像使用即可。