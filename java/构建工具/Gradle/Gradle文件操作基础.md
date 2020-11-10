实际使用Gradle过程中大多数时候需要操作文件，好在Gradle给我们提供了一些API来快捷处理。

**定位文件：**

我们可以使用Project.file()方法来定位一个文件获取File对象（详情参考Project的API），如下：

```groovy
//相对路径
File configFile = file('src/config.xml')
//绝对路径
File configFile = file(configFile.absolutePath)
//项目路径的文件对象 
File configFile = file(new File('src/config.xml'))
```

可以从Project的API发现file()方法能够接收任何形式的对象参数，它会将参数值转换为一个绝对文件对象，通常我们可以传一个String或File实例；如果传的路径是绝对路径，则会被直接构造为一个文件实例，否则会被构造为项目目录加上传递目录的文件对象；当然了，file()方法还能识别URL（譬如file:/some/path.xml等）。

文件集合：

文件集合其实是一组文件，Gradle使用FileCollection接口表示文件集合，Gradle API中许多类都实现了这个接口，譬如dependency configurations等。获取FileCollection实例的一种方法是Project.files()，我们可以传递任何数量的对象参数。如下：

```groovy
FileCollection collection = files('src/file1.txt',
                                  new File('src/file2.txt'),
                                  ['src/file3.txt', 'src/file4.txt'])
```

使用迭代操作还能将其转换为其他的一些类型，同时我们还可以使用+操作将两个文件集合合并，使用-操作对一个文件集合做减法。如下例子：

```groovy
// 对文件集合进行迭代
collection.each {File file ->
    println file.name
}

// 转换文件集合为其他类型
Set set = collection.files
Set set2 = collection as Set
List list = collection as List
String path = collection.asPath
File file = collection.singleFile
File file2 = collection as File

// 增加和减少文件集合
def union = collection + files('src/file3.txt')
def different = collection - files('src/file3.txt')
```

我们也可以向files()方法传递闭包或者可回调的实例参数，当查询集合的内容时就会调用它，然后将返回值转换为一些文件实例，返回值可以是files()方法支持的任何类型的对象。如下例子：

```groovy
task list << {
    File srcDir

    // 使用闭合创建一个文件集合
    collection = files { srcDir.listFiles() }

    srcDir = file('src')
    println "Contents of $srcDir.name"
    collection.collect { relativePath(it) }.sort().each { println it }

    srcDir = file('src2')
    println "Contents of $srcDir.name"
    collection.collect { relativePath(it) }.sort().each { println it }
}
```



**文件树：**

文件树可以代表一个目录树结构或一个ZIP压缩文件的内容，FileTree继承自FileCollection，所以我们可以像处理文件集合一样处理文件树，使用Project.fileTree()方法可以得到FileTree实例，它会创建一个基于基准目录的对象。如下：

```groovy
/以一个基准目录创建一个文件树
FileTree tree = fileTree(dir: 'src/main')

// 添加包含和排除规则
tree.include '**/*.java'
tree.exclude '**/Abstract*'

// 使用路径创建一个树
tree = fileTree('src').include('**/*.java')

// 使用闭合创建一个数
tree = fileTree('src') {
    include '**/*.java'
}

// 使用map创建一个树
tree = fileTree(dir: 'src', include: '**/*.java')
tree = fileTree(dir: 'src', includes: ['**/*.java', '**/*.xml'])
tree = fileTree(dir: 'src', include: '**/*.java', exclude: '**/*test*/**')

// 遍历文件树
tree.each {File file ->
    println file
}

// 过滤文件树
FileTree filtered = tree.matching {
    include 'org/gradle/api/**'
}

// 合并文件树A
FileTree sum = tree + fileTree(dir: 'src/test')

// 访问文件数的元素
tree.visit {element ->
    println "$element.relativePath => $element.file"
}
```

我们还可以使用ZIP或TAR等压缩文件的内容作为文件树，Project.zipTree()和Project.tarTree()方法可以返回一个FileTree实例。如下：

```groovy
// 使用路径创建一个ZIP文件
FileTree zip = zipTree('someFile.zip')

// 使用路径创建一个TAR文件
FileTree tar = tarTree('someFile.tar')

//TarTree可以根据文件扩展名得到压缩方式，如果我们想明确的指定压缩方式则可以如下操作
FileTree someTar = tarTree(resources.gzip('someTar.ext'))
```

**指定输入文件：**

Gradle中有些对象的属性可以接收一组输入文件，譬如JavaComplile任务的source属性（定义编译的源文件）。如下：

```groovy
//使用一个File对象设置源目录
compile {
    source = file('src/main/java')
}

//使用一个字符路径设置源目录
compile {
    source = 'src/main/java'
}

//使用一个集合设置多个源目录
compile {
    source = ['src/main/java', '../shared/java']
}

//使用FileCollection或者FileTree设置源目录
compile {
    source = fileTree(dir: 'src/main/java').matching {include 'org/gradle/api/**'}
}

//使用闭包设置源目录
compile {
    source = {
        // Use the contents of each zip file in the src dir
        file('src').listFiles().findAll {it.name.endsWith('.zip')}.collect { zipTree(it) }
    }
}

compile {
    //使用字符路径添加源目录
    source 'src/main/java', 'src/main/groovy'
    //使用File对象添加源目录
    source file('../shared/java')
    //使用闭包添加源目录
    source { file('src/test/').listFiles() }
}
```

复制文件：

我们可以使用复制任务（Copy）进行文件复制操作，复制任务扩展性很强，它可以过滤复制文件的内容，使用复制任务要提供想要复制的源文件和一个目标目录，如果要指定文件被复制时的转换方式则可以使用复制规则，复制规则是一个CopySpec接口的实现，我们使用CopySpec.from()方法指定源文件，CopySpec.into()方法指定目标目录即可。如下：

```groovy
task copyTask(type: Copy) {
    from 'src/main/webapp'
    into 'build/explodedWar'
}

task anotherCopyTask(type: Copy) {
    //复制src/main/webapp目录下的所有文件
    from 'src/main/webapp'
    //复制一个单独文件
    from 'src/staging/index.html'
    //复制一个任务输出的文件
    from copyTask
    //显式使用任务的outputs属性复制任务的输出文件
    from copyTaskWithPatterns.outputs
    //复制一个ZIP压缩文件的内容
    from zipTree('src/main/assets.zip')
    //指定目标目录
    into { getDestDir() }
}

task copyTaskWithPatterns(type: Copy) {
    from 'src/main/webapp'
    into 'build/explodedWar'
    include '**/*.html'
    include '**/*.jsp'
    exclude { details -> details.file.name.endsWith('.html') &&
                         details.file.text.contains('staging') }
}

task copyMethod << {
    copy {
        from 'src/main/webapp'
        into 'build/explodedWar'
        include '**/*.html'
        include '**/*.jsp'
    }
}

//在复制时重命名文件
task rename(type: Copy) {
    from 'src/main/webapp'
    into 'build/explodedWar'
    //使用闭包映射文件名
    rename { String fileName ->
        fileName.replace('-staging-', '')
    }
    // 使用正则表达式映射文件名
    rename '(.+)-staging-(.+)', '$1$2'
    rename(/(.+)-staging-(.+)/, '$1$2')
}
```

文件同步任务：

同步任务（Sync）继承自复制任务（Copy），当执行时会复制源文件到目标目录，然后从目标目录删除所有非复制文件。如下：
```groovy
task libs(type: Sync) {
    from configurations.runtime
    into "$buildDir/libs"
}
```
创建归档文件：

使用归档任务可以创建Zip、Tar、Jar、War、Ear等归档文件，如下：

```groovy
apply plugin: 'java'

task zip(type: Zip) {
    from 'src/dist'
    into('libs') {
        from configurations.runtime
    }
}
```