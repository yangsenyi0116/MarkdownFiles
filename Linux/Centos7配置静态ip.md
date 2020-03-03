```bash
vim /etc/sysconfig/network-scripts/ifcfg-ens33

BOOTPROTO=static
ONBOOT=yes

IPADDR=[静态ip]
GATEWAY=[默认网关]
NETMASK=[子网掩码]

# 重启网卡
service network restart

# 配置dns
vi /etc/NetworkManager/NetworkManager.conf

dns=none

vi /etc/resolv.conf

# 主DNS
nameserver 8.8.8.8
# 备用DNS
nameserver 114.114.114.114
```

