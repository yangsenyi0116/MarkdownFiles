```bash
# 更新yum包
yum update

yum install -y yum-utils device-mapper-persistent-data lvm2

# 设置yum源
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo（阿里仓库）

# 安装docker
yum install docker-ce-版本号
```

