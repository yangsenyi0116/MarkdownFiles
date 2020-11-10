低版本gradle引入lombk的方式为：

```groovy
compile("org.projectlombok:lombok:1.18.2")
```
在5.0版本以后这种方式会引起编译错误，正确引入方式如下：
```groovy
annotationProcessor 'org.projectlombok:lombok:1.18.2'
compileOnly 'org.projectlombok:lombok:1.18.2'
```
单测使用的话就加上：
```groovy
testAnnotationProcessor 'org.projectlombok:lombok:1.18.2'
testCompileOnly 'org.projectlombok:lombok:1.18.2'
```