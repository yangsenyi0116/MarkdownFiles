### 什么是 Reflection 反射，为什么要用它

Java 强类型语言，但是我们在运行时有了解、修改信息的需求，包括类信息、成员信息以及数组信息。

### Java 中 Reflection 和 Introspection 区别？

说起反射，还有一个相似的概念 ‘Introspection’，字面意思是“自省、内省”，它们之间的区别如下：

#### 内省

在运行时检查一个对象的类型或者属性
最常见的例子就是运行时通过 a instanceof A 来判断 a 对象的类型

#### 反射

用来在运行时检查或者修改一个对象信息
可以用来实现看似不可能的操作，比如访问私有方法，动态创建对象
可以看到，反射是在内省的基础上，增加了修改的能力。

### 反射的入口：java.lang.Class

日常开发中的对象，分为两种，基本类型和引用类型：

- 基本类型，（固定的 8 种）
  - 整数：byte, short, int, long

  - 小数：float, double

  - 字符：char

  - 布尔值：boolean

- 引用类型
    - 所有的引用类型都继承自 java.lang.Object
    - 类，枚举，数组，接口都是引用类型
    - java.io.Serializable 接口，基本类型的包装类（比如 java.lang.Double）也是引用类型

对每一种对象，JVM 都会实例化一个 java.lang.Class 的实例，java.lang.Class 为我们提供了在运行时访问对象的属性和类型信息的能力。Class 还提供了创建新的类和对象的能力。最重要的是，Class 是调用其他反射 API 的入口，我们必须先获得一个 Class 实例才可以进行接下来的操作。

### 得到一个 Class 对象

除了 java.lang.reflect.ReflectPermission 以外，java.lang.reflect 中的其他类都没有 public 的构造函数，也就是说要得到这些类，我们必须通过 Class 。

下面是几种得到 Class 对象的不同方法：

#### 1.Object.getClass 方法

如果我们已经拿到了一个对象，可以很方便地使用它的 getClass 方法获得一个 Class 对象(当然这仅限于引用类型的对象)：

```java
Class c = "shixinzhang.top".getClass();
```

返回的对象 c 是 String 类型。

```java
enum Sex{
    FEMALE,
    MALE
}

Class c = FEMALE.getClass();
```

上述例子中 FEMALE 是 枚举 *Sex* 的实例，因此 FEMALE.getClass() 返回的就是 枚举类型 Sex 的 Class。

```java
byte[] bytes = new byte[1024];
Class<? extends byte[]>c = bytes.getClass();
```

由于数组也是 Object 的一种，因此我们可以调用 getClass() 方法获得 byte 数组类型的 Class。



#### **2. .class 语法**

如果我们当前没有某个类的对象，无法使用 getClass() 方法，那还可以使用另外一种方法获取 Class：**在要获得的类名后加上 .class** ，比如这样：

```java
Integer.class.newInstance();
int.class.newInstance()
```

可以看到，这种方式不仅能用于引用类型，基本类型也可以。

当然数组也可以喽：

```java
Class b = int[][].class;
```

#### **3.Class.forName()**

如果我们有一个类的完整路径，就可以使用 Class.forName(“类完整的路径”) 来得到相应的 Class，这个方法**只能用于引用类型**，比如：

```java
Class<?> c = Class.forName("java.lang.String");
Class<?> aClass = Class.forName("top.shixinzhang.androiddemo2.beans.BookBean");
```

#### **4.静态属性 TYPE**

上面介绍，使用 .class 后缀可以很方便地获得基本类型的 Class。

对于基本类型和 void 的包装类，还有另外一种方式获得 Class，那就是静态属性 TYPE 。

每个包装类都有 TYPE 属性，以 Double 为例：

```java
public static final Class<Double> TYPE
        = (Class<Double>) double[].class.getComponentType();
```

可以看到这个属性就是使用 .class 的方式获得 Class 并保存。

因此我们可以直接调用包装类的 TYPE：

```java
Class<Integer> integerWrapper = Integer.TYPE;
Class<Double> doubleWrapper = Double.TYPE;
Class<Void> voidWrapper = Void.TYPE;
```

#### 5.返回 Class 的方法

如果我们已经有了一个 Class，可以使用下面的一些方法来获得它相关的类：

- Class.getSuperclass()

  - 返回调用类的父类

- Class.getClasses()
  - 返回调用类的所有公共类、接口、枚举组成的 Class 数组，包括继承的
- Class.getDeclaredClasses()
  - 返回调用类显式声明的所有类、接口、枚举组成的 Class 数组
- Class.getDeclaringClass()
- java.lang.reflect.Field.getDeclaringClass()
- java.lang.reflect.Method.getDeclaringClass()
- java.lang.reflect.Constructor.getDeclaringClass()
  - 返回类/属性/方法/构造器所在的类



### Class 的修饰符：Modifier

一个 Class 可以被以下修饰符的一种或者多种修饰：

- 访问权限控制符：`public`, `protected`, `private`
- 抽象的、需要实现的：``abstract`
- 限制只能有一个实例的：`static`
- 不允许修改的：`final`
- 线程同步锁：`synchronized`
- 原生函数：`native`
- 采用严格的浮点精度：`strictfp`
- 接口
- 注解

当然上面的修饰符不是所有 Class 都可以修饰，比如：

- `Interface` 不能是 `final` 的
- `enum` 不能是 `abstract` 的

`java.lang.reflect.Modifier` 提供了对 Class 修饰符的解码，我们可以使用 `Class.getModifiers()` 获得调用类的修饰符的二进制值，然后使用 `Modifier.toString(int modifiers)` 将二进制值转换为字符串，`Modifier.toString()` 方法实现如下：

```java
public static java.lang.String toString(int modifiers) {
    StringBuilder buf = new StringBuilder();
    if (isPublic(modifiers)) {
        buf.append("public ");
    }
    if (isProtected(modifiers)) {
        buf.append("protected ");
    }
    if (isPrivate(modifiers)) {
        buf.append("private ");
    }
    if (isAbstract(modifiers)) {
        buf.append("abstract ");
    }
    if (isStatic(modifiers)) {
        buf.append("static ");
    }
    if (isFinal(modifiers)) {
        buf.append("final ");
    }
    if (isTransient(modifiers)) {
        buf.append("transient ");
    }
    if (isVolatile(modifiers)) {
        buf.append("volatile ");
    }
    if (isSynchronized(modifiers)) {
        buf.append("synchronized ");
    }
    if (isNative(modifiers)) {
        buf.append("native ");
    }
    if (isStrict(modifiers)) {
        buf.append("strictfp ");
    }
    if (isInterface(modifiers)) {
        buf.append("interface ");
    }
    if (buf.length() == 0) {
        return "";
    }
    buf.setLength(buf.length() - 1);
    return buf.toString();
}
```

注意：

- Interface 默认是 abstract 的，虽然我们没有添加，编译器会在编译器为每个 Interface 添加这个修饰符。
- 只有被 @Retention(RetentionPolicy.RUNTIME) 修饰的注解才可以在运行时被发射获取
- Java 中预定义的注解 @Deprecated,@Override, 和 @SuppressWarnings 中只有 @Deprecated 可以在运行时被访问到

### Class 的成员：Member

`java.lang.reflect.Member` 是一个接口，代表 Class 的成员，每个成员都有类型，分为是否从父类继承，还有是否可以直接访问。

Member 有三个实现类：

- java.lang.reflect.Constructor：表示该 Class 的构造函数
- java.lang.reflect.Field：表示该 Class 的成员变量
- java.lang.reflect.Method：表示该 Class 的成员方法

#### 获取构造函数

java.lang.Class 提供了以下方法用于获取该类的构造函数：

![shixinzhang](assets/20170118195710021.jfif)

注意：构造函数无法从父类继承

#### 获取成员变量

java.lang.Class 提供了以下方法用于获取该类的成员变量：

![shixinzhang](assets/20170118195728230.jfif)

#### 获取成员方法

java.lang.Class 提供了以下方法用于获取该类的成员方法：

![shixinzhang](assets/20170118195741886.jfif)