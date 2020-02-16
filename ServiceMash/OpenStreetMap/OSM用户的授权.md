\#12. 启动服务

Rails内置了一个web服务器,因此我们可以通过如下命令直接启动它：

```
bundle exec rails server
```

然后我们就可以在浏览器中输入 <http://localhost:3000/> 来打开

注意：OSM地图区域名信息不是通过本地数据库获取的.

\#13. 配置

当安装完软件之后，我们可能还需要执行一些特殊的步骤操作，如下：
\##13.1 导入数据库数据
在我们成功根据上述步骤安装完openstreetmap-website之后，我们的数据库中其实还没有地图数据导入。我们可以通过(Potlatch 2, iD, JOSM等)自己编辑地图数据或者从OSM文件中导入。OSM文件下载地址（以国家分类）：<http://download.geofabrik.de/asia/>
基于Osmosis工具导入地图数据至openstreetmap数据库中的命令如下：

```
osmosis --read-pbf greater-london-latest.osm.pbf \
  --write-apidb host="localhost" database="openstreetmap" \
  user="openstreetmap" password="" validateSchemaVersion="no"
注意：user和password参数需要根据本地情况修改,此处使用的osmosis版本必须>= 0.44.1
```

\##13.2 用户管理
如果我们在自己搭建的openstreetmap-website网页上注册一个用户之后，我们还需要通常邮箱验证才能登陆。如果我们我们不想安装一个邮箱服务器来发送邮件给我们外部邮箱，我们可以通过Rails控制台手动进行验证，具体命令如下：

```
$ bundle exec rails console
>> user = User.find_by_display_name("My New User Name")
=> #[ ... ]
>> user.status = "active"
=> "active"
>> user.save!
=> true
>> quit
```

\##13.3 修改用户权限

给某一个用户增加超级用户管理权限，命令如下:

```
$ bundle exec rails console
>> user = User.find_by_display_name("My New User Name")
=> #[ ... ]
>> user.roles.create(:role => "administrator", :granter_id => user.id)
=> #[ ... ]
>> user.roles.create(:role => "moderator", :granter_id => user.id)
=> #[ ... ]
>> user.save!
=> true
>> quit
```

\##13.4 设置OAuth Consumer Keys

openstreetmap-website内置的三个地图编辑应用是通过API 实现交互的，因此我们需要配置OAuth consumer keys ，具体如下：
三种内置地图数据编辑软件：
Potlatch 2
iD
The website itself (for the Notes functionality)
例如 ，我们如果使用Potlatch 2进行地图数据编辑，我们需要注册一个OAuth应用，步骤如下：
1.登陆我们的Rails Port网站，例如http://localhost:3000
2.点击用户名 ，进入用户配置界面
3.点击"my settings" 页面
4.点击 “oauth settings” 页面
5.点击 ‘Register your application’.
6.除非我们有OAuth账户，否则我们进行如下填写
Name：Local Potlatch
URL: “[http://localhost:3000](http://localhost:3000/)”
7.选上 ‘modify the map’ box选项框
8.其他的选项使用默认值即可
9.点击 “Register” 按钮
10.在界面中复制 “consumer key”
11.编辑config/application.yml
12.开启"potlatch2_key" 配置项并配置值
13.重启rails服务器
以下是一个展示参考示例application.yml:
编辑config/application.yml文件 ，设置 默认编辑器和potlatch2_key

```
# Default editor
default_editor: "potlatch2"
# OAuth consumer key for Potlatch 2
potlatch2_key: "8lFmZPsagHV4l3rkAHq0hWY5vV3Ctl3oEFY1aXth"
```

如果我们想使用iD (id_key) 或者the website/Notes (oauth_key)，可以参照上述步骤，consumer key 可以复用

\#13.5 排查问题

Rails有自己的日志.如果我们想查看日志，可使用如下命令 :

```
tail -f log/development.log
```

如果遇到一些其他问题，可以通过邮件（ [rails-dev@openstreetmap.org](mailto:rails-dev@openstreetmap.org)）或者 [#osm-dev IRC Channel](http://wiki.openstreetmap.org/wiki/IRC)留言

\##13.6 安装补充
如果你在安装过程中遇到错误：
1.如果是gem的依赖发生变化，可在rail_port目录下以root用户运行bundle install
2.如果是 OSM数据库表文件发生变化，我们可通过如下命令解决 ：

```
bundle exec rake db:migrate
```

bundle exec rake db:migrate
\##13.7 在osm dev服务器上测试
例如，当我们开发完了一个 rails_port补丁文件，我们需要将它贡献给其他人或者请求测试。我们可以在服务器上创建自己用户目录来提供入口。

\##13.8 开发者
关于更多开发者信息，请参考 CONTRIBUTING.md文件

\##13.9 产品部署
如果我们需要将The Rails Port用户产品，我们需要进行一些修改。
1.我们不推荐使用rails server在产品上。我们推荐使用Phusion Passenger，关于Phusion Passenger的搭建可以在很多网站上找到，这里不进行详细介绍。
2.开发者可能需要一些产品的开发环境和数据库的访问权限，我们需要提供一些账户给他们。
3.产品的数据库也需要进行扩展和增添一些功能，[具体详见INSTALL.md](http://xn--install-m15k72ngn5os6e.md/)。
4.这个版本的地图调用API函数非常耗内存，并且速度也慢。我们推荐使用CGIMap。
5.GPX导入插件也不够完善，可以使用别的插件替换
6.在编译这个软件之前，确保有如下宏定义: RAILS_ENV=production rake assets:precompile
7.确保用户可以对服务器上的/tmp目录有读写权限