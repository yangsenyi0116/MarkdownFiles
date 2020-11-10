## Method 介绍

继承的方法（包括重载、重写和隐藏的）会被编译器强制执行，这些方法都无法反射。

因此，反射一个类的方法时不考虑父类的方法，只考虑当前类的方法。

每个方法都由 **修饰符、返回值、参数、注解和抛出的异常**组成。

`java.lang.reflect.Method` 方法为我们提供了获取上述部分的 API。	

## 获取方法的信息

下面的代码演示了如何获得一个方法的 **修饰符、返回值、参数、注解和抛出的异常** 等信息：

```java
public class MethodTypeSpy extends BaseTestClass {
    private static final String fmt = "%24s:   %s\n";
    private static final String HELLO_WORLD = "I'm cute shixin";

    @Deprecated
    public static void main(String[] args) throws ClassNotFoundException {
        MethodTypeSpy methodTypeSpy = new MethodTypeSpy();
        Class<? extends MethodTypeSpy> cls = methodTypeSpy.getClass();
        printFormat("Class：%s \n", cls.getCanonicalName());
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            printFormat(fmt, "Method name", declaredMethod.getName());  //获得单独的方法名
            //获得完整的方法信息（包括修饰符、返回值、路径、名称、参数、抛出值）
            printFormat(fmt, "toGenericString", declaredMethod.toGenericString());

            int modifiers = declaredMethod.getModifiers();      //获得修饰符
            printFormat(fmt, "Modifiers", Modifier.toString(modifiers));

            System.out.format(fmt, "ReturnType", declaredMethod.getReturnType());   //获得返回值
            System.out.format(fmt, "getGenericReturnType", declaredMethod.getGenericReturnType());//获得完整信息的返回值

            Class<?>[] parameterTypes = declaredMethod.getParameterTypes(); //获得参数类型
            Type[] genericParameterTypes = declaredMethod.getGenericParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                System.out.format(fmt, "ParameterType", parameterTypes[i]);
                System.out.format(fmt, "GenericParameterType", genericParameterTypes[i]);
            }

            Class<?>[] exceptionTypes = declaredMethod.getExceptionTypes();     //获得异常名称
            Type[] genericExceptionTypes = declaredMethod.getGenericExceptionTypes();
            for (int i = 0; i < exceptionTypes.length; i++) {
                System.out.format(fmt, "ExceptionTypes", exceptionTypes[i]);
                System.out.format(fmt, "GenericExceptionTypes", genericExceptionTypes[i]);
            }

            Annotation[] annotations = declaredMethod.getAnnotations(); //获得注解
            for (Annotation annotation : annotations) {
                System.out.format(fmt, "Annotation", annotation);
                System.out.format(fmt, "AnnotationType", annotation.annotationType());
            }
        }
    }
}
```

查看当前类 `MethodTypeSpy`的方法 `main()` 的信息，运行结果：

```console
Class：net.sxkeji.shixinandroiddemo2.test.reflection.MethodTypeSpy 
             Method name:   main
         toGenericString:   public static void net.sxkeji.shixinandroiddemo2.test.reflection.MethodTypeSpy.main(java.lang.String[]) throws java.lang.ClassNotFoundException
               Modifiers:   public static
              ReturnType:   void
    getGenericReturnType:   void
           ParameterType:   class [Ljava.lang.String;
    GenericParameterType:   class [Ljava.lang.String;
          ExceptionTypes:   class java.lang.ClassNotFoundException
   GenericExceptionTypes:   class java.lang.ClassNotFoundException
              Annotation:   @java.lang.Deprecated()
          AnnotationType:   interface java.lang.Deprecated

Process finished with exit code 0
```

## 获取方法的参数名称

从 JDK 1.8 开始，`java.lang.reflect.Executable.getParameters` 为我们提供了获取普通方法或者构造方法的名称的能力。

在 JDK 中 `java.lang.reflect.Method` 和 `java.lang.reflect.Constructor` 都继承自 `Executable`，因此它俩也有同样的能力。

**然而在 Android SDK 中 `Method`, `Constructor` 继承自 `AbstractMethod`**，无法获得方法的参数名：

```java
public final class Method extends AbstractMethod implements GenericDeclaration, Member
```

你可以在 J2EE 环境下练习官方的 **获取参数名称代码**：

```java
/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import java.lang.reflect.*;
import java.util.function.*;
import static java.lang.System.out;

public class MethodParameterSpy {

    private static final String  fmt = "%24s: %s%n";

    // for the morbidly curious
    <E extends RuntimeException> void genericThrow() throws E {}

    public static void printClassConstructors(Class c) {
        Constructor[] allConstructors = c.getConstructors();
        out.format(fmt, "Number of constructors", allConstructors.length);
        for (Constructor currentConstructor : allConstructors) {
            printConstructor(currentConstructor);
        }  
        Constructor[] allDeclConst = c.getDeclaredConstructors();
        out.format(fmt, "Number of declared constructors",
            allDeclConst.length);
        for (Constructor currentDeclConst : allDeclConst) {
            printConstructor(currentDeclConst);
        }          
    }

    public static void printClassMethods(Class c) {
       Method[] allMethods = c.getDeclaredMethods();
        out.format(fmt, "Number of methods", allMethods.length);
        for (Method m : allMethods) {
            printMethod(m);
        }        
    }

    public static void printConstructor(Constructor c) {
        out.format("%s%n", c.toGenericString());
        Parameter[] params = c.getParameters();
        out.format(fmt, "Number of parameters", params.length);
        for (int i = 0; i < params.length; i++) {
            printParameter(params[i]);
        }
    }

    public static void printMethod(Method m) {
        out.format("%s%n", m.toGenericString());
        out.format(fmt, "Return type", m.getReturnType());
        out.format(fmt, "Generic return type", m.getGenericReturnType());

        Parameter[] params = m.getParameters();
        for (int i = 0; i < params.length; i++) {
            printParameter(params[i]);
        }
    }

    public static void printParameter(Parameter p) {
        out.format(fmt, "Parameter class", p.getType());
        out.format(fmt, "Parameter name", p.getName());
        out.format(fmt, "Modifiers", p.getModifiers());
        out.format(fmt, "Is implicit?", p.isImplicit());
        out.format(fmt, "Is name present?", p.isNamePresent());
        out.format(fmt, "Is synthetic?", p.isSynthetic());
    }

    public static void main(String... args) {        

        try {
            printClassConstructors(Class.forName(args[0]));
            printClassMethods(Class.forName(args[0]));
        } catch (ClassNotFoundException x) {
            x.printStackTrace();
        }
    }
}
```

## 获取方法的修饰符

方法可以被以下修饰符修饰：

- 访问权限控制符：public, protected, private
- 限制只能有一个实例的：static
- 不允许修改的：final
- 抽象，要求子类重写：abstract
- 预防重入的同步锁：synchronized
- 用其他语言实现的方法：native
- 严格的浮点型强度：strictfp
- 注解

类似获取 Class 的修饰符，我们可以使用 “Method.getModifiers()`方法获取当前成员变量的修饰符。返回`java.lang.reflect.Modifier“` 中定义的整形值。



```java
public class MethodModifierSpy extends BaseTestClass {

    private final static String CLASS_NAME = "java.lang.String";

    public static void main(String[] args) {
        MethodModifierSpy methodModifierSpy = new MethodModifierSpy();
        Class<? extends MethodModifierSpy> cls = methodModifierSpy.getClass();
        printFormat("Class: %s \n\n", cls.getCanonicalName());

        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            printFormat("\n\nMethod name： %s \n", declaredMethod.getName());
            printFormat("Method toGenericString： %s \n", declaredMethod.toGenericString());

            int modifiers = declaredMethod.getModifiers();
            printFormat("Method Modifiers： %s\n", Modifier.toString(modifiers));

            System.out.format("synthetic= %-5b,  var_args= %-5b,  bridge= %-5b \n"
                    , declaredMethod.isSynthetic(), declaredMethod.isVarArgs(), declaredMethod.isBridge());
        }

    }

    public final void varArgsMethod(String... strings) {

    }

}
```

```console
Class: net.sxkeji.shixinandroiddemo2.test.reflection.MethodModifierSpy 



Method name： main 
Method toGenericString： public static void net.sxkeji.shixinandroiddemo2.test.reflection.MethodModifierSpy.main(java.lang.String[]) 
Method Modifiers： public static
synthetic= false,  var_args= false,  bridge= false 


Method name： varArgsMethod 
Method toGenericString： public final void net.sxkeji.shixinandroiddemo2.test.reflection.MethodModifierSpy.varArgsMethod(java.lang.String...) 
Method Modifiers： public final transient
synthetic= false,  var_args= true ,  bridge= false 

Process finished with exit code 0
```

**注意：上面的最后一行可以看到，方法有三种类型：synthetic, varagrs, bridge。**

下面介绍这三种方法类型：

### **`synthetic method`：合成方法**

什么是合成方法呢？

首先需要理解一个概念：

> 对于 Java 编译器而言，内部类也会被单独编译成一个class文件。
> 那么原有代码中的相关属性可见性就难以维持，synthetic method也就是为了这个目的而生成的。生成的synthetic方法是包访问性的static方法.

```java
public class Foo {
  private Object baz = "Hello";
  private int get(){
    return 1;
  }
  private class Bar {
    private Bar() {
      System.out.println(get());
    }
  }
}
```

上面的代码中，Bar 访问了 Foo 的 private 方法 get()。

使用 `javap -private Foo`看一下:

```java
public class Foo {
  private java.lang.Object baz;
  public Foo();
  private int get();
  static int access$000(Foo); //多出来的 synthetic 方法，为了在 Bar 中的这段代码 System.out.println(get());
}
```

因此可以这么理解：

> Synthetic (合成)方法是由编译器产生的、源代码中没有的方法。
> 当内部类与外部类之前有互相访问 private 属性、方法时，编译器会在运行时为调用方创建一个 synthetic 方法。

合成方法主要创建于嵌套内部类中。

我们可以使用 `Method.isSynthetic()` 方法判断某个方法是否为 synthetic 。

### **`varargs ( variable arguments) method`：Java 可变参数方法**

```java
public void testVarargs(String... strings){
    //...
}
```

- 创建时必须放在方法尾部，即一个方法只能有一个可变数组参数
- 调用时可以传入一个数组：
  - `testVarargs(new String[]{"shixin","zhang"});`
- 也可以分别传入多个参数：
  - `testVarargs("shixin","zhang");`

推荐使用后者。

我们可以使用 `Method.isVarArgs()` 方法判断某个方法包含可变参数 。

### **`bridge method`：桥接方法**

我们知道，为了兼容 JDK 1.5 以前的代码，泛型会在编译时被去除（泛型擦除），这时需要创建**桥接方法**。

```java
/** 
 * @author Mikan 
 * @date 2015-08-05 16:22 
 */  
public interface SuperClass<T> {  

    T method(T param);  

}  

package com.mikan;  

/** 
 * @author Mikan 
 * @date 2015-08-05 17:05 
 */  
public class SubClass implements SuperClass<String> {  
    public String method(String param) {  
        return param;  
    }  
}  
```

上面的代码创建了一个泛型接口和实现类。

实现类在运行时的字节码如下：

```java
localhost:mikan mikan$ javap -c SubClass.class  
Compiled from "SubClass.java"  
public class com.mikan.SubClass implements com.mikan.SuperClass<java.lang.String> {  
  public com.mikan.SubClass();  
    flags: ACC_PUBLIC  
    Code:  
      stack=1, locals=1, args_size=1  
         0: aload_0  
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V  
         4: return  
      LineNumberTable:  
        line 7: 0  
      LocalVariableTable:  
        Start  Length  Slot  Name   Signature  
               0       5     0  this   Lcom/mikan/SubClass;  

  public java.lang.String method(java.lang.String);  
    flags: ACC_PUBLIC  
    Code:  
      stack=1, locals=2, args_size=2  
         0: aload_1  
         1: areturn  
      LineNumberTable:  
        line 11: 0  
      LocalVariableTable:  
        Start  Length  Slot  Name   Signature  
               0       2     0  this   Lcom/mikan/SubClass;  
               0       2     1 param   Ljava/lang/String;  

  public java.lang.Object method(java.lang.Object);  
    flags: ACC_PUBLIC, ACC_BRIDGE, ACC_SYNTHETIC  
    Code:  
      stack=2, locals=2, args_size=2  
         0: aload_0  
         1: aload_1  
         2: checkcast     #2                  // class java/lang/String  
         5: invokevirtual #3                  // Method method:(Ljava/lang/String;)Ljava/lang/String;  
         8: areturn  
      LineNumberTable:  
        line 7: 0  
      LocalVariableTable:  
        Start  Length  Slot  Name   Signature  
               0       9     0  this   Lcom/mikan/SubClass;  
               0       9     1    x0   Ljava/lang/Object;  
}  
```

可以看到，实现类的字节码中多了两个方法，一个是默认的无参构造方法，另一个就是编译器自动生成的桥接方法（flags 包括 ACC_BRIDGE 和 ACC_SYNTHETIC），它的参数、返回值类型都是 Object。但是它把 Object 类型的参数强制转换成了 String 类型，再调用在 SubClass 类中声明的方法，转换过来其实就是：

```java
public Object method(Object param) {  
    return this.method(((String) param));  
} 
```

可以看到，桥接方法的参数、返回值和 JDK 1.5 以前的“泛型”方法一样，都是 Object，实际上调用的却是真正的泛型方法。

明修栈道暗度陈仓啊。有些类似适配器模式。

小结一下：

桥接方法由编译器自动生成，参数、返回值都是 Object，然后调用实际泛型方法。

它实现了将泛型生成的字节码与 1.5 以前的字节码进行兼容。

我们可以使用 Method.isBridge() 方法判断某个方法是否为桥接方法 。

## 反射调用方法

我们可以使用 `java.lang.reflect.Method.invoke()` 方法来反射调用一个方法（下面的代码是 JDK 1.6）：

```java
public native Object invoke(Object receiver, Object... args)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
```

- 第一个参数是方法属于的对象（如果是静态方法，则可以直接传 null）
- 第二个可变参数是该方法的参数
- 如果调用的方法有抛出异常，异常会被 `java.lang.reflect.InvocationTargetException` 包一层

**当然一般只用于正常情况下无法直接访问的方法（比如：private 的方法，或者无法或者该类的对象）。**

```java
public class MethodInvoke extends BaseTestClass {

    private boolean checkString(String s) {
        printFormat("checkString: %s\n", s);
        return TextUtils.isEmpty(s);
    }

    private static void saySomething(String something) {
        System.out.println(something);
    }

    private String onEvent(TestEvent event) {
        System.out.format("Event name: %s\n", event.getEventName());
        return event.getResult();
    }

    static class TestEvent {
        private String eventName;
        private String result;

        public TestEvent(String eventName, String result) {
            this.eventName = eventName;
            this.result = result;
        }

        public String getResult() {
            return result;
        }

        public String getEventName() {
            return eventName;
        }
    }

    public static void main(String[] args) {
        try {
            Class<?> cls = Class.forName("net.sxkeji.shixinandroiddemo2.test.reflection.MethodInvoke");
            MethodInvoke object = (MethodInvoke) cls.newInstance();
            Method[] declaredMethods = cls.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                String methodName = declaredMethod.getName();       //获取方法名
                Type returnType = declaredMethod.getGenericReturnType();    //获取带泛型的返回值类型
                int modifiers = declaredMethod.getModifiers();      //获取方法修饰符

//                declaredMethod.setAccessible(true);

                if (methodName.equals("onEvent")) {
                    TestEvent testEvent = new TestEvent("shixin's Event", "cuteType");
                    try {
                        Object invokeResult = declaredMethod.invoke(object, testEvent);
                        System.out.format("Invoke of %s, return %s \n", methodName, invokeResult.toString());
                    } catch (InvocationTargetException e) {     //处理被调用方法可能抛出的异常
                        Throwable cause = e.getCause();
                        System.out.format("Invocation of %s failed:  %s\n", methodName, cause.getMessage());
                    }
                } else if (returnType == boolean.class) {
                    try {
                        declaredMethod.invoke(object, "shixin's parameter");
                    } catch (InvocationTargetException e) {
                        Throwable cause = e.getCause();
                        System.out.format("Invocation of %s failed:  %s\n", methodName, cause.getMessage());
                    }
                }else if (Modifier.isStatic(modifiers) && !methodName.equals("main")){    //静态方法，调用时 object 直接传入 null
                    try {
                        declaredMethod.invoke(null, "static method");
                    } catch (InvocationTargetException e) {
                        Throwable cause = e.getCause();
                        System.out.format("Invocation of %s failed:  %s\n", methodName, cause.getMessage());
                    }
                }

            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
```

```console
checkString: shixin's parameter
Invocation of checkString failed:  Stub!
Event name: shixin's Event
Invoke of onEvent, return cuteType 
static method

Process finished with exit code 0
```

## 调用含有可变参数的方法

首先需要理解的是，可变参数是用一个数组实现的。

`Class.getDeclaredMethod(name, parameterTypes)` 方法为我们提供了获取有可变参数的方法：

```java
public Method getDeclaredMethod(String name, Class<?>... parameterTypes)
        throws NoSuchMethodException {
    return getMethod(name, parameterTypes, false);
}
```

可以看到，第二个参数是 `Class` 类型的可变参数，我们在调用时可以传入一个 `Class` 数组。

下面的代码演示了如何调用一个含有可变参数方法：

```java
public class VarArgsMethodInvoke extends BaseTestClass {

    public void printVarArgs(String... varArgs) {
        System.out.format("printVarArgs：\n");
        for (String arg : varArgs) {
            System.out.format("%20s\n", arg);
        }
    }

    public static void main(String[] args) {
        VarArgsMethodInvoke object = new VarArgsMethodInvoke();
        Class<? extends VarArgsMethodInvoke> cls = object.getClass();
        try {
//            Class[] argTypes = new Class[]{String[].class};
            Method declaredMethod = cls.getDeclaredMethod("printVarArgs", String[].class);
            String[] varArgs = {"shixin", "zhang"};
            declaredMethod.invoke(object, (Object) varArgs);

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
```

```java
printVarArgs：
              shixin
               zhang

Process finished with exit code 0
```

## 常见错误 1 ：泛型擦除导致的 `NoSuchMethodException`

```java
public class MethodReflectionFailed<T> extends BaseTestClass {

    public void lookUp(T t){}
    public void find(Integer integer){}

    public static void main(String[] args) {
        //虽然声明类型为 Integer，实际会被擦除
        Class<? extends MethodReflectionFailed> cls = (new MethodReflectionFailed<Integer>()).getClass();
//        Class<Integer> parameterClass = Integer.class;
        Class<Object> parameterClass = Object.class;
        try {
            Method lookUp = cls.getMethod("lookUp", parameterClass);
            printFormat("Method:    %s\n", lookUp.toGenericString());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}```

反射调用泛型方法时，由于运行前编译器已经把泛型擦除，**参数类型会被擦除为上边界（默认 Object）**。

这时你想调用的 ```lookup(Integer)``` 是不存在的，因为它实际上是 ```lookup(Object)```，上述代码运行结果：

​```java
java.lang.NoSuchMethodException: net.sxkeji.shixinandroiddemo2.test.reflection.MethodReflectionFailed.lookUp(java.lang.Integer)
    at java.lang.Class.getMethod(Class.java:1786)
    at net.sxkeji.shixinandroiddemo2.test.reflection.MethodReflectionFailed.main(MethodReflectionFailed.java:25)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at com.intellij.rt.execution.application.AppMain.main(AppMain.java:144)


```

只要传入的参数改为 `Object` 就可以了：

```java
Method:    public void net.sxkeji.shixinandroiddemo2.test.reflection.MethodReflectionFailed.lookUp(T)
```

**小结：反射调用方法时要传入上边界。**

## 常见错误 2 ：访问不可见方法导致的 `IllegalAccessException`

当你访问 private 的方法或者 private 的类中的方法，会抛出这个异常。

解决方法就是给该 method 设置 setAccessible(true)

注意：我们无法访问 private 的方法是因为有权限管理机制，setAccessible(true) 只是发出允许访问当前方法的请求，并不能保证一定成功。在成功后我们才可以反射调用。

## 常见错误 3：反射调用方法时传入错误参数导致的 `IllegalArgumentException`

如果一个方法没有参数，但是我们反射时传入参数，就会导致 `llegalArgumentException`。

此外，当声明一个可变参数方法 `foo(Object... o)` 时，编译器会使用一个 Object 数组将所有参数传过去。
也就是说 `foo(Object... o)` 相当于 `foo(Object[] o)`。