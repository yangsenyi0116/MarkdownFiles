**插件基础：**

关于Gradle支持的插件可以[点我搜索](https://plugins.gradle.org/)。其实Gradle的核心只是一个空空的框架，所谓的Gradle构建便捷脚本其实都是由插件提供支持的，插件添加了新的任务。在Gradle中一般有两种类型的插件，如下：

- 脚本插件
	是额外的构建脚本，它会进一步配置构建，通常会在构建内部使用。脚本插件可以从本地文件系统或远程获取，如果从文件系统获取则是相对于项目目录，如果是远程获取则是由HTTP URL指定。

- 二进制插件
	是实现了Plugin接口的类，并且采用编程的方式来操纵构建。

插件需要通过Project.apply()方法完成声明应用，相同的插件可以应用多次。如下例子：

```groovy
//脚本插件
apply from: 'other.gradle'

//二进制插件
apply plugin: 'java'
```

插件还可以使用插件ID，插件的id作为给定插件的唯一标识符，我们可以给插件注册一个缩写字符的id。譬如下面例子：

```groovy
//通过Java插件的id进行引用
apply plugin: JavaPlugin
```

使用构建脚本块应用插件：

我们可以向构建脚本中加入插件的类路径然后再应用插件和使用插件的任务，如下：

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "com.jfrog.bintray.gradle:gradle-bintray-plugin:0.4.1"
    }
}

apply plugin: "com.jfrog.bintray"
```

**Gradle插件拓展：**

可以看见，Gradle其实是依托于各种插件壮大的，譬如Java插件用来构建Java工程，Android插件用来构建打包Android工程，我们只需要选择合适的插件即可，插件会为我们提供丰富的任务用来快捷处理构建，具体详情参考各插件API即可。

---

### Gradle的Java插件构建实例

上面说了，插件是Gradle的扩展，它会通过某种方式配置我们的项目（譬如加入一些task）；Gradle自带许多插件，我们也可以编写自己的插件然后开源.，Java 插件就是这样的一个插件，该插件已经给项目定义了默认的参数（譬如Java源文件位置），所以通常我们不需要在脚本中加入太多东西。

单个基础Java项目构建：
```groovy
//把Java插件加入到项目中，也就是许多预定制的任务被自动加入到了项目里
apply plugin: 'java'
```
加入上面插件以后Gradle默认希望能在src/main/java路径下找到源代码，在 src/test/java路径下找到测试代码，任何src/main/resources路径的文件都会被包含在JAR文件里，任何src/test/resources路径的文件都会被加入到classpath中以运行测试代码，所有的输出文件将会被创建在构建目录里，JAR文件存放在 build/libs文件夹里。

加入Java插件后我们可以通过gradle tasks命令来列出项目的所有任务，这样就可以知道Java插件添加了哪些task。常用的task如下：

- build task
	当运行gradle build命令时Gradle将会编译和测试你的代码，并且创建一个包含类和资源的JAR文件。

- clean task
	当运行gradle clean命令时Gradle将会删除build生成的目录和所有生成的文件。

- assemble task
	当运行gradle assemble命令时Gradle将会编译并打包代码，但是并不运行单元测试。

- check task
	当运行gradle check命令时Gradle将会编译并测试你的代码，其他的插件会加入更多的检查步骤。

单个具有外部依赖的Java项目构建：

当然了，一个Java项目可能会有许多外部依赖（即调用第三方JAR），为了在项目里引用这些 JAR包，我们需要告诉Gradle去哪里找他们，好在Gradle支持许多仓库，这些仓库可以被用来提取或者放置依赖，我们可以很方便的从这些仓库中取得第三方Jar包。如下：
```groovy
//加入Maven仓库
repositories {
    mavenCentral()
}
```

接着加入一些编译阶段来自于mavenCentral仓库的依赖，如下：

```groovy
dependencies {
    //编译阶段
    compile group: 'commons-collections', name: 'commons-collections', version: '3.2'
    //测试编译阶段
    testCompile group: 'junit', name: 'junit', version: '4.+'
}
```

定制构建项目：

Java插件给项目加入了一些属性，这些属性已经被赋予了默认的值且已经够我们日常使用了，如果我们觉得这些默认属性不好也可以自己修改。如下：

```groovy
//定制 MANIFEST.MF 文件
sourceCompatibility = 1.5
version = '1.0'
jar {
    manifest {
        attributes 'Implementation-Title': 'Gradle Quickstart', 'Implementation-Version': version
    }
}	
```

默认Java插件加入的任务是常规性的任务，但是我们可以定制任务，譬如我们可以设置一个任务的属性、在任务中加入行为、改变任务的依赖、完全重写一个任务等。如下：

```groovy
//测试阶段加入一个系统属性
test {
    systemProperties 'property': 'value'
}
```

关于哪些属性是可用的问题，我们可以使用gradle properties命令列出项目的所有属性。

**发布JAR文件：**

通常JAR文件需要在某个地方发布，我们可以通过Gradle方便的进行发布，譬如下面例子将发布到一个本地的目录，如下：

```groovy
//uploadArchives task
uploadArchives {
    repositories {
       flatDir {
           dirs 'repos'
       }
    }
}
```

**多Java项目构建：**

在Gradle中为了定义一个多项目构建我们需要创建一个设置文件（settings.gradle），设置文件放在源代码的根目录，它用来指定要包含哪个项目且名字必须叫做settings.gradle。如下例子：

```txt
//多项目工程结构树：
multiproject/
  api/
  services/webservice/
  shared/
```

```gro
//多项目构建settings.gradle文件
include "shared", "api", "services:webservice", "services:shared"
```

对于大多数多项目构建有一些配置对所有项目都是通用的，所以我们将在根项目里定义一个这样的通用配置（配置注入技术 configuration injection）。 根项目就像一个容器，subprojects方法遍历这个容器的所有元素并且注入指定的配置。如下：

```groovy
//多项目构建通用配置
subprojects {
    apply plugin: 'java'
    apply plugin: 'eclipse-wtp'

    repositories {
       mavenCentral()
    }

    dependencies {
        testCompile 'junit:junit:4.11'
    }

    version = '1.0'

    jar {
        manifest.attributes provider: 'gradle'
    }
}
```

可以看见，上面通用配置把Java插件应用到了每一个子项目中。

我们还可以在同一个构建里加入项目之间的依赖，这样可以保证他们的先后关系。如下：

```groovy
//api/build.gradle
dependencies {
    compile project(':shared')
}
```

