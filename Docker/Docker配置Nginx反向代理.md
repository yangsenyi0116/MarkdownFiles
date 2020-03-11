```bash
## 获取nginx镜像
docker pull nginx

## 创建镜像容器
docker run --name=nginx-test -p 80:80 nginx

## 在本地建立相关目录
mkdir -p /var/nginx/conf /var/nginx/logs /var/nginx/www

## 将nginx容器中的配置文件拷贝出来
docker cp ******:/etc/nginx/nginx.conf /var/nginx/conf

## 删除原来的容器并建立新容器
docker run -d -p 80:80 --name=nginx  \
-v /var/nginx/www:/usr/share/nginx/html \
-v /var/nginx/conf/nginx.conf:/etc/nginx/nginx.conf \
-v /var/nginx/logs:/var/log/nginx \
nginx


##修改配置文件
vim /var/nginx/conf/nginx.conf
server{
       listen 80;
       charset utf-8;
       server_name 192.168.112.135;
 
       location / {
          proxy_pass http://192.168.112.135:8080;
          proxy_redirect default;
       }
    }
    
## 重新加载配置文件
docker exec ****** /etc/init.d/nginx reload
```

