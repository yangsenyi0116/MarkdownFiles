**允许root远程登录**

```bash
sudo vim /etc/ssh/sshd_config
```

**修改文件**

```config
PermiteRootLogin yes
```

**重启ssh服务**

```bash
systemctl restart ssh
```

