```bash
cd /etc/sysconfig/network-scripts

vi ifcfg-ens33

# 修改ONBOOT
ONBOOT=yes

service network restart
```

