### 配置固定 IP 和 DNS

> 当关机后再启动虚拟机有时 IP 地址会自动更换，导致之前的配置不可用；配置完 Kubernetes 网络后虚拟机还会出现无法联网的情况，后经研究发现是 DNS 会被自动重写所致，Ubuntu Server 18.04 LTS 版本的 IP 和 DNS 配置也与之前的版本配置大相径庭，故在此说明下如何修改 IP 和 DNS

### 修改固定 IP

编辑 `vi /etc/netplan/50-cloud-init.yaml` 配置文件，注意这里的配置文件名未必和你机器上的相同，请根据实际情况修改。修改内容如下：	

```yaml
network:
    ethernets:
        ens33:
          addresses: [192.168.141.134/24]
          gateway4: 192.168.141.2
          nameservers:
            addresses: [192.168.141.2]
    version: 2
```

使配置生效 `netplan apply`



### 修改 DNS

#### [#](https://www.funtl.com/zh/service-mesh-kubernetes/配置网络.html#方法一)方法一

- 停止 `systemd-resolved` 服务：`systemctl stop systemd-resolved`
- 修改 DNS：`vi /etc/resolv.conf`，将 `nameserver` 修改为如 `114.114.114.114` 可以正常使用的 DNS 地址

#### [#](https://www.funtl.com/zh/service-mesh-kubernetes/配置网络.html#方法二)方法二

```bash
vi /etc/systemd/resolved.conf
```

把 DNS 取消注释，添加 DNS，保存退出，重启即可

![img](https://www.funtl.com/assets1/Lusifer_20190602201826.png)