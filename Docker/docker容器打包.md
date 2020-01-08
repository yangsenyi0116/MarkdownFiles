[docker 将正在运行的容器打包为镜像](https://www.cnblogs.com/jackadam/p/9528448.html)

#### 将容器打包成镜像

`docker commit -a "author" -m "text" [container_id/container_name] [tag]`
OPTIONS说明：
-a :提交的镜像作者；
-c :使用Dockerfile指令来创建镜像；
-m :提交时的说明文字；
-p :在commit时，将容器暂停。