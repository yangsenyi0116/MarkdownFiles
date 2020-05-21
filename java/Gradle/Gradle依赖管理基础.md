**Gradle依赖声明：**

```groovy
apply plugin: 'java'

repositories {
    mavenCentral()
}

dependencies {
    compile group: 'org.hibernate', name: 'hibernate-core', version: '3.6.7.Final'
    testCompile group: 'junit', name: 'junit', version: '4.+'
}
```

**Gradle依赖配置：**

在Gradle中依赖可以组合成configurations（配置），一个配置简单地说就是一系列的依赖，通俗说也就是依赖配置；我们可以使用它们声明项目的外部依赖，也可以被用来声明项目的发布。下面我们给出几种Java插件中常见的配置，如下：

- compile
  用来编译项目源代码的依赖；

- runtime
  在运行时被生成的类需要的依赖，默认项，包含编译时的依赖；

- testCompile
  编译测试代码依赖，默认项，包含生成的类运行所需的依赖和编译源代码的依赖；

- testRuntime
  运行测试所需要的依赖，默认项，包含上面三个依赖；

**Gradle外部依赖：**

我们可以用Gradle声明许多种依赖，其中有一种是外部依赖（external dependency），它是在当前构建之外的一种依赖，一般存放在远程（譬如Maven）或本地的仓库里。如下是一个外部依赖的例子：

```groovy
dependencies {
    compile group: 'org.hibernate', name: 'hibernate-core', version: '3.6.7.Final'
}
```

可以看见，引用一个外部依赖需要用到group、name、version属性。上面的写法还有一种简写，如下规则：

```groovy
group:name:version
```

简写例子

```groovy
dependencies {
    compile 'org.hibernate:hibernate-core:3.6.7.Final'
}
```

**Gradle仓库：**

有了上面的外部依赖，你指定会想Gradle是咋找到那些外部依赖文件的。其实Gradle会在一个仓库（repository）里找这些依赖文件，仓库其实就是很多依赖文件的集合服务器, 他们通过group、name、version进行归类存储，好在Gradle可以解析好几种不同的仓库形式（譬如Maven等），但是Gradle默认不提前定义任何仓库，我们必须手动在使用外部依赖之前定义自己的仓库。



下面是一个使用MavenCentral仓库的例子：

```groovy
repositories {
    mavenCentral()
}
```

这是一个使用远程Maven仓库的例子：

```groovy
repositories {
    maven {
        url "http://repo.mycompany.com/maven2"
    }
}
```

这是一个使用本地文件系统里库的例子：

```groovy
repositories {
    ivy {
        // URL can refer to a local directory
        url "../local-repo"
    }
}
```

当然了，一个项目可以有好几个库，Gradle会根据依赖定义的顺序在各个库里寻找它们，在第一个库里找到了就不会再在第二个库里找它了，否则在第二个库找。



**Gradle发布artifacts：**

依赖配置也可以用来发布文件，我们可以通过在uploadArchives任务里加入仓库来完成。下面是一个发布到Maven 库的例子，Gradle将生成和上传pom.xml，如下：	

```groovy
apply plugin: 'maven'

uploadArchives {
    repositories {
        mavenDeployer {
            repository(url: "file://localhost/tmp/myRepo/")
        }
    }
}
```

