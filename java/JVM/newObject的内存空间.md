既然一个Object对象占用16个字节，那这个16个字节中分别存放的是什么内容呢？

- 前面8个字节是对象头，也叫markword，记录对象被上锁的各种状态（锁升级）和垃圾回收相关信息等。
- 接下来4个字节（**4G堆内存以下；或者32G以内，并且开启了ClassPointer指针压缩，否则是8个字节**）是一个指向对象所属Class对象的指针。
- 接下来4个字节是为了8字节对齐而填充的padding。

查看是否开启指针压缩：

```js
java -XX:+PrintFlagsFinal | find "UseCompressed"
----
bool UseCompressedClassPointers  := true  {lp64_product}
bool UseCompressedOops           := true  {lp64_product}
1234
```

UseCompressedClassPointers：类对象指针压缩选项。

UseCompressedOops（oops–Ordinary Object Pointers)：普通对象指针压缩选项。

## 方法二

使用OpenJDK提供的jol（Java Object Layout）库进行观察。
Maven引入依赖：

```xml
<!-- https://mvnrepository.com/artifact/org.openjdk.jol/jol-core -->
<dependency>
    <groupId>org.openjdk.jol</groupId>
    <artifactId>jol-core</artifactId>
    <version>0.10</version>
</dependency>
123456
```

测试代码：

```java
Object o = new Object();
String layout = ClassLayout.parseInstance(o).toPrintable();
System.out.println(layout);
123
```

输出：

```xml
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
     12     4        (loss due to the next object alignment)
Instance size: 16 bytes
Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
1234567
```

解释：实例对象占用16个字节。

附图：
![对象内存布局](https://img-blog.csdnimg.cn/20200607112101451.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpbnlzdWNjZXNz,size_16,color_FFFFFF,t_70)