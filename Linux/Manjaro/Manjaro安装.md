启动后配置国内源

```bash
sudo pacman-mirrors -i -c China -m rank //更新镜像排名
sudo pacman -Syy //更新数据源
```

 

archlinuxcn源

```bash
# USTC
[archlinuxcn]
 
SigLevel = Optional TrustedOnly
Server = https://mirrors.ustc.edu.cn/archlinuxcn/$arch
```

```bash
sudo pacman -Syyu //更新数据源
sudo pacman -S archlinuxcn-keyring //安装导入GPG key
```



安装vmware-tools

```bash
sudo pacman -R open-vm-tools

git clone https://github.com/rasa/vmware-tools-patches.git

cd vmware-tools-patches

sudo ./patched-open-vm-tools.sh

```





主题安装

Step-1：主题，图标，色彩的更新
kdekdekde 的设置还是很好用的，先打开设置。

然后点击 工作空间主题 -> Plasma主题 -> 获得新 Plasma 主题 ，搜索 MacBreeze Shadowless ，点击安装。



然后选择并应用。



然后点击左上角的按钮返回，再点击 图标 -> 获取新图标主题 ，搜索 Mojave CT icons 并安装。



随后选择并应用。

然后返回设置界面，点击 应用程序风格 -> GNOME/GTK 应用程序风格 -> 获取新 GNOME/GTK 应用风格 -> 下载 GTK3 主题。

搜索 McMojave ，并安装。

将 GTK2GTK2GTK2 、GTK3GTK3GTK3 、图标主题、备用主题全部换为 Mojave-CT-light 。

唯一的问题是不装 gnome-tweaks 来修改的话，窗口按钮还是在右边 = =。

接着回到设置的主菜单，点击 色彩 -> 获取新配色方案 -> 评分 ，再搜索 Mac OS X? 点击安装。

选择这个配色方案即可。
