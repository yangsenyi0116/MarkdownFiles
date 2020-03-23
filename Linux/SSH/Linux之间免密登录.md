```bash
ssh-keygen

ssh-copy-id [ip]
```

```bash
# 生成公钥
ssh-keygen

~/.ssh/id_rsa.pub

#发送公钥到linux
scp ./id_rsa.pub root@192.168.xxx.xxx:~/.ssh/windows_ras.pub

#配置Linux
cd ~/.ssh
touch authorized_keys
echo windows_ras.pub >> authorized_keys
```

