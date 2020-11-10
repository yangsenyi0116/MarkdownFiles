## Field 成员变量的介绍

每个成员变量有**类型**和**值**。

`java.lang.reflect.Field` 为我们提供了获取当前对象的成员变量的类型，和重新设值的方法。

#### 获取变量的类型

类中的变量分为两种类型：基本类型和引用类型：

- 基本类型（ 8 种）
  - 整数：byte, short, int, long
  - 浮点数：float, double
  - 字符：char
  - 布尔值：boolean
- 引用类型
  - 所有的引用类型都继承自 java.lang.Object
  - 类，枚举，数组，接口都是引用类型
  - java.io.Serializable 接口，基本类型的包装类（比如 java.lang.Double）也是引用类型

`java.lang.reflect.Field` 提供了两个方法获去变量的类型：

- Field.getType()：返回这个变量的类型
- Field.getGenericType()：如果当前属性有签名属性类型就返回，否则就返回 `Field.getType()`

```java

public class FieldSpy<T> extends BaseTestClass {
    public boolean[][] b = {{true, true}, {false, false}};
    public String name = "shixinzhang";
    public Integer integer = 23;
    public T type;

    public static final String CLASS_NAME = "net.sxkeji.shixinandroiddemo2.test.reflection.FieldSpy";

    public static void main(String[] args) {
        try {
            Class<?> aClass = Class.forName(CLASS_NAME);
            Field[] fields = aClass.getFields();
            for (Field field : fields) {
                printFormat("Field：%s \n",field.getName());
                printFormat("Type：\n  %s\n", field.getType().getCanonicalName());
                printFormat("GenericType:\n  %s\n", field.getGenericType().toString());
                printFormat("\n\n");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```

运行结果：

```java
Field：b         //二维的布尔值数组
Type：
  boolean[][]
GenericType:
  class [[Z


Field：name 
Type：
  java.lang.String
GenericType:
  class java.lang.String


Field：integer 
Type：
  java.lang.Integer
GenericType:
  class java.lang.Integer


Field：type         //泛型 T 类型，运行时被擦除为 Object
Type：
  java.lang.Object
GenericType:
  T


Field：CLASS_NAME 
Type：
  java.lang.String
GenericType:
  class java.lang.String



Process finished with exit code 0
```

## 获取成员变量的修饰符

成员变量可以被以下修饰符修饰：

- 访问权限控制符：`public`, `protected`, `private`
- 限制只能有一个实例的：`static`
- 不允许修改的：`final`
- 不会被序列化：`transient`
- 线程共享数据的一致性：`volatile`
- 注解

类似获取 Class 的修饰符，我们可以使用 `Field.getModifiers()` 方法获取当前成员变量的修饰符。
返回 `java.lang.reflect.Modifier` 中定义的整形值。



由于 `Field` 间接继承了 `java.lang.reflect.AnnotatedElement` ，因此运行时也可以获得修饰成员变量的注解，当然前提是这个注解被 `java.lang.annotation.RetentionPolicy.RUNTIME` 修饰。



### 获取和修改成员变量的值

拿到一个对象后，我们可以在运行时修改它的成员变量的值，对运行时来说，反射修改变量值的操作和类中修改变量的结果是一样的。

举个例子：

```java
enum MyHabit {
    LOL,
    CODE
}

public class People extends BaseTestClass {
    public long idCarNumber = 1000000000;
    public String[] name = {"shi", "xin"};
    public MyHabit habit = MyHabit.CODE;

    public static void main(String[] args) {
        People shixin = new People();
        String fmt = "%6s:  %s  = %s \n";
        Class<? extends People> cls = shixin.getClass();
        try {
            Field idCarNumber = cls.getDeclaredField("idCarNumber");
            System.out.format(fmt, "before", idCarNumber.getName(), shixin.idCarNumber);
            idCarNumber.setLong(shixin, 123456);
            System.out.format(fmt, "after", idCarNumber.getName(), shixin.idCarNumber);

            Field name = cls.getDeclaredField("name");
            System.out.format(fmt, "before", name.getName(), Arrays.asList(shixin.name));
            name.set(shixin, new String[]{"hei", "hei"});
            System.out.format(fmt, "after", name.getName(), Arrays.asList(shixin.name));

            Field habit = cls.getDeclaredField("habit");
            System.out.format(fmt, "before", habit.getName(), shixin.habit);
            habit.set(shixin, MyHabit.LOL);
            System.out.format(fmt, "after", habit.getName(), shixin.habit);


        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
```

运行结果：

```console
before:  idCarNumber  = 1000000000 
 after:  idCarNumber  = 123456 
before:  name  = [shi, xin] 
 after:  name  = [hei, hei] 
before:  habit  = CODE 
 after:  habit  = LOL 

Process finished with exit code 0
```

## 常见错误 1 ：无法转换类型导致的 `java.lang.IllegalArgumentException`

下面的代码和上个例子的代码很相似，但是运行时却报错：

```java
public class FieldTrouble extends BaseTestClass {
    public Integer value;

    public static void main(String[] args) {
        FieldTrouble fieldTrouble = new FieldTrouble();
        Class<? extends FieldTrouble> cls = fieldTrouble.getClass();
        try {
            Field value = cls.getField("value");
            value.setInt(fieldTrouble, 23);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
```

```console
Exception in thread "main" java.lang.IllegalArgumentException: Can not set java.lang.Integer field net.sxkeji.shixinandroiddemo2.test.reflection.FieldTrouble.value to (int)23
    at sun.reflect.UnsafeFieldAccessorImpl.throwSetIllegalArgumentException(UnsafeFieldAccessorImpl.java:167)
    at sun.reflect.UnsafeFieldAccessorImpl.throwSetIllegalArgumentException(UnsafeFieldAccessorImpl.java:191)
    at sun.reflect.UnsafeObjectFieldAccessorImpl.setInt(UnsafeObjectFieldAccessorImpl.java:114)
    at java.lang.reflect.Field.setInt(Field.java:949)
    at net.sxkeji.shixinandroiddemo2.test.reflection.FieldTrouble.main(FieldTrouble.java:24)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at com.intellij.rt.execution.application.AppMain.main(AppMain.java:144)
```

为什么我们无法给 Integer 类型的 value 使用 `setInt()` 方法重新设值呢？

**这是因为在使用反射获取或者修改一个变量的值时，编译器不会进行自动装/拆箱。**

因此我们无法给一个 Integer 类型的变量赋整型值，必须给它赋一个 Integer 对象才可以。

使用 `Field.set(Object obj, Object value)` 方法解决这个问题：

```java
f.set(ft, new Integer(43));
```

## 常见错误 2：反射非 public 的变量导致的 `NoSuchFieldException`

如果你使用 `Class.getField()` 或者 `Class.getFields()` 获取非 public 的变量，编译器会报 `java.lang.NoSuchFieldException` 错。

下面这张图说明了获取变量的四个方法所左右的对象类型：

![shixinzhang](assets/20170118195945257.jfif)

## 常见错误 3 ：修改 `final`类型的变量导致的 `IllegalAccessException`

当你想要获取或者修改 不可修改（`final`）的变量时，会导致`IllegalAccessException`。

举个例子：

```java
public class FieldTrouble extends BaseTestClass {
    public Integer value;
    public final boolean wannaPlayGame = true;

    public static void main(String[] args) {
        FieldTrouble fieldTrouble = new FieldTrouble();
        Class<? extends FieldTrouble> cls = fieldTrouble.getClass();
        try {
//            Field value = cls.getField("value");
//            value.setInt(fieldTrouble, 23);
//            value.set(fieldTrouble, new Integer(23));

            Field wannaPlayGame = cls.getDeclaredField("wannaPlayGame");
//            wannaPlayGame.setAccessible(true);    //加上这句就没问题
            wannaPlayGame.setBoolean(fieldTrouble, false);
            System.out.format("Field：%s    %s\n", wannaPlayGame.getName(),fieldTrouble.wannaPlayGame);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}```

上面的代码想要通过反射修改 wannaPlayGame 变量，运行后会报错：

​```java
java.lang.IllegalAccessException: Can not set final boolean field net.sxkeji.shixinandroiddemo2.test.reflection.FieldTrouble.wannaPlayGame to (boolean)false
    at sun.reflect.UnsafeFieldAccessorImpl.throwFinalFieldIllegalAccessException(UnsafeFieldAccessorImpl.java:76)
    at sun.reflect.UnsafeFieldAccessorImpl.throwFinalFieldIllegalAccessException(UnsafeFieldAccessorImpl.java:84)
    at sun.reflect.UnsafeQualifiedBooleanFieldAccessorImpl.setBoolean(UnsafeQualifiedBooleanFieldAccessorImpl.java:96)
    at java.lang.reflect.Field.setBoolean(Field.java:801)
    at net.sxkeji.shixinandroiddemo2.test.reflection.FieldTrouble.main(FieldTrouble.java:28)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at com.intellij.rt.execution.application.AppMain.main(AppMain.java:144)



```

**这是因为在类初始化后，无形之中会有一个访问限制阻止我们修改 `final` 类型的变量**。

由于 `Field` 继承自 `AccessibleObject` , 我们可以使用 `AccessibleObject.setAccessible()` 方法告诉安全机制，这个变量可以访问。

因此上面的例子中，声明这个变量是可访问的：``wannaPlayGame.setAccessible(true)``，运行就正常了。

### 总结

在使用反射修改某个对象的成员变量前你要明白，这样做会造成一定程度的性能开销，因为在反射时这样的操作需要引发许多额外操作，比如验证访问权限等。只在特殊情况下这么做。

另外使用反射也会导致一些运行时的计算优化失效。比如下面的代码，运行时极可能会优化为最后一句：

```java
int x = 1;
x = 2;
x = 3;
```

但是如果使用反射 `Field.set*()` 做同样的操作，可能就不会有这种优化了。

使用 `setAccessible(true)` 方法前也需要注意，这可能会导致意想不到的后果，比如：
在运行时虽然你通过反射修改了变量 a 的值，但其他部分可能还在使用原来的值。