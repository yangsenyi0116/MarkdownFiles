OneDrive默认的保存位置是C:\Users\<win-username>\OneDrive，OneDrive只能默认同步保存在该位置的文件到其服务器，很多时候我们可能还需要同步其它目录的文件，现在借助DOS命令mklink将其它目录链接到OneDrive目录即可实现。
比如目录D:\Workspace\的数据也需要同步到OneDrive服务器，但又不想移动该目录。只需在CMD中执行如下命令：

```cmd
mklink /d "C:\Users\<win-username>\OneDrive\ws" "D:\Workspace"
```




