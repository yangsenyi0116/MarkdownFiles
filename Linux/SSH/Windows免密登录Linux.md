```bash
# windows先生成ssh公钥
DESKTOP_**** > ssh-keygen

cd C:/user/[user]/.ssh

ls
id_rsa  id_rsa.pub  known_hosts

scp id_rsa.pub [user]@[ip]:/root/.ssh/win_id_rsa.pub


# 登录到linux
cd /root/.ssh

cat win_id_rsa.pub >> authorized_keys


# linux免密登录linux同理
```