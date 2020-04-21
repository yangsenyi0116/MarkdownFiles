```shell
#查看系统当前使用的shell
echo $SHELL 

/bin/bash

#查看系统是否安装了zsh
cat /etc/shells 

/bin/sh
/bin/bash
/sbin/nologin
/usr/bin/sh
/usr/bin/bash
/usr/sbin/nologin
/bin/tcsh
/bin/csh


#安装zsh
# centos
yum -y install zsh
# ubuntu 
apt-get install zsh

#切换shell为zsh
chsh -s /bin/zsh
Changing shell for root.
Shell changed.

#重启服务器后，可使用reboot

#重启后，查看当前shell
echo $SHELL 
/bin/zsh

#安装 oh my zsh
#oh-my-zsh源码是放在github上，先确保你的机器上已安装了git
#安装：

wget https://github.com/robbyrussell/oh-my-zsh/raw/master/tools/install.sh -O - | sh

# 成功界面：
____  / /_     ____ ___  __  __   ____  _____/ /_  
 / __ \/ __ \   / __ `__ \/ / / /  /_  / / ___/ __ \ 
/ /_/ / / / /  / / / / / / /_/ /    / /_(__  ) / / / 
\____/_/ /_/  /_/ /_/ /_/\__, /    /___/____/_/ /_/  
                        /____/                       ....is now installed!
Please look over the ~/.zshrc file to select plugins, themes, and options.

p.s. Follow us at https://twitter.com/ohmyzsh.

p.p.s. Get stickers and t-shirts at http://shop.planetargon.com.


#修改主题：
vim ~/.zshrc
# 将ZSH_THEME改成ys
ZSH_THEME="ys"
#更新配置：
source ~/.zshrc   

#自动补齐插件
wget http://mimosa-pudica.net/src/incr-0.2.zsh 

#将此插件放到oh-my-zsh目录的插件库下：
#在~/.zshrc文件末尾加上
source ~/.oh-my-zsh/plugins/incr/incr*.zsh
#更新配置：
source ~/.zshrc 


#与vim的提示相冲突的解决方案
#解决方法：将~/.zcompdump*删除即可
rm -rf ~/.zcompdump*
exec zsh
```

