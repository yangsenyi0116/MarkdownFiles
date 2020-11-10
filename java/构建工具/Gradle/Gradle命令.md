**多任务调用命令：**

```bash
gradle task1 task2 [...]
```

**排除任务命令：**

```
gradle -x task1 task2 [...]
```

**失败后继续执行构建命令：**

只要有任务调用失败Gradle默认就会中断执行，我们可以使用–continue选项在一次调用中不中断执行，然后发现所有失败原因。

**简化任务名命令：**

当我们调用某个任务时如果名字太长我们可以采用简化操作，但是必须保证可以唯一区分出该任务的字符，譬如：

```bash
//简写
gradle -x t1
//替换
gradle -x task1
```



**选择执行构建命令：**

调用gradle命令默认会构建当前目录下的build.gradle文件，我们可以使用-b参数选择其他目录的构建文件且当使用此参数时settings.gradle将不会生效。如下：

```groovy
//选择文件构建subdir/myproject.gradle
task hello << {
    println "using build file '$buildFile.name' in '$buildFile.parentFile.name'."
}
```

执行过程：

```bash
xxx@XXX:~/$ gradle -b subdir/myproject.gradle hello
:hello
using build file 'myproject.gradle' in 'subdir'.

BUILD SUCCESSFUL

Total time: 1.397 secs
```

此外我们还可以使用-p参数来指定构建的目录，譬如在多项目构建中可以用-p替代-b参数。如下执行过程：

```bash
xxx@XXX:~/$ gradle -p subdir hello
:hello
using build file 'build.gradle' in 'subdir'.

BUILD SUCCESSFUL

Total time: 1.397 secs
```

**获取构建信息：**

- gradle projects命令：列出子项目名称列表。
- gradle tasks命令：列出项目中所有任务。
- gradle help –task someTask命令：可以显示指定任务的详细信息。
- gradle dependencies命令：列出项目的依赖列表，所有依赖会根据任务区分，以树型结构展示。