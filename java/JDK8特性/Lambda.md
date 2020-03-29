## Java SE8

本文是对OpenJDK  Lambda项目JSR 335所增加功能的非正式概述,对2011年12月发布的上一次迭代进行了改进。一些语言变化的正式描述和OpenJDK开发人员预览可以在JSR的早期草案中找到。额外的历史设计文档可以在OpenJDK项目页面找到。还有一个附带Lambda库版本增强的介绍文档，描述了作为JSR 335的一部分添加的库增强功能。

Lambda项目的主要目标是让"代码即数据"这个模式在Java中方便易用并且使其深入人心。主要的新语言特点包括:

1.Lambda表达式(非官方的,"闭包"(closures)或者"匿名方法"(anonymous methods))
 2.方法和构造器引用
 3.拓展的目标类型和目标引用
 4.接口中的默认和静态方法

## 1. 背景

Java是一门面向对象语言。 在函数式和面向对象语言中, 基础元素都可以动态封装程序行为: 面向对象语言使用含有方法(method)的对象(object), 函数式语言则使用函数(function)。看起来他们似乎没有什么共同点或者相似度, 这是因为java的对象相对比较"重量级"：对于单独声明的类的实例化 (instantiations)包含了大量的字段和方法。

我们经常可以见到某个对象只是简单的封装了一个函数， 最典型的例子就是 Java API 定义的接口(Interface)， 有时被称为"回调接口"。用户需要通过提供一个实例来调用这个API



```csharp
public interface ActionListener { 
    void actionPerformed(ActionEvent e);
}
```

对于只是为了在使用的时候实例化一次的情况, 我们通常实例化一个匿名内部类来实现这个接口而不是再去额外声明一个类。



```csharp
button.addActionListener(new ActionListener() { 
  public void actionPerformed(ActionEvent e) { 
    ui.dazzle(e.getModifiers());
  }
});
```

很多常用的类库都依赖于这种特性,尤其是在并发API中, 代码在执行的时候必须在线程间保持独立。 根据摩尔定律,我们总是容易得到更多而不是更快的处理器,而串行API的处理能力非常有限,因此并发编程变得异常重要。

由于回调的方式的编码风格越来越贴近于函数式编程，Java中的代码轻量化变得非常重要。因此匿名内部类并不是一个很好的选择, 原因主要有以下几点:

1. 语法冗余
2. 匿名类中成员名称和this非常容易跟外部混淆
3. 不灵活的类加载以及实例创建机制
4. 不能够捕获非final的局部变量
5. 在流程控制上不够抽象化

这个项目解决了以上的很多问题。引入了由作用域规则组成的新的更精确的表达式解决了1)和2)
 定义了更为灵活以及易于优化的表达式机制回避了3),通过允许编译器推断变量的不可变性(允许捕获有效的final局部变量,并不一定要有final关键字)改善了问题4)

不过此项目的目的并不是解决内部类带来的所有问题, 比如捕获可变变量4)或者非局部的流程控制5)并不在范畴以内(未来可能会提供对这些特性的支持)

## 2.函数式接口

尽管有着自身的局限性,匿名内部类对Java的类型系统有很好的适应性:一个函数对应了一个接口类型。这个特性使用起来非常便利:

接口已经是Java类型系统(type system)的一部分
 接口天然具有运行时表示( runtime representation)的特性
 接口还可以通过Javadoc注释来表达一些非正式的约定，比如断言操作是可交换的

ActionListener这个接口只有一个方法, 很多回调式接口都有这个特性, 比如Runnable和Comparator。 我们给这种只有一个方法的接口统一命名为函数式接口(之前被叫做SAM 类型, "单一抽象方法" Single Abstract Method)

声明函数式接口的时候不需要做额外的工作, 编译器会根据接口的结构进行识别(识别过程不是简单去数方法个数,接口还有可能冗余的声明了`Object`提供的方法,比如`toString()`,或者声明了静态或者默认方法,这些都不在"只有一个方法"这个限定条件中)。但是API的作者可以根据`@FunctionalInterface` 来得知接口是设计为函数式的 (而不是恰好只有一个方法), 有了这个注释, 编译器会验证接口是不是满足函数式接口的结构。

之前有一种代替(或者是补充)函数式类型的提议是引入新的结构化类型,被叫做”箭头类型”(arrow types)。例如一个可以将String和Object转为一个int的函数可以表达如下 (String,Object)->int。这个想法在充分考虑后放弃了,至少现在是这样, 因为它有以下不足:

1. 增加了类型系统的复杂度, 以及混合使用结构 (structural) 类型和名义 (nominal) 类型 (Java几乎全部是名义类型)
2. 导致不同库的代码风格差异, 一些库会继续使用回调接口, 与此同时另外一些库会使用结构函数类型
3. 语法变的非常笨拙, 尤其是包含了受检异常 (checked exceptions) 以后
4. 对于每个不同的函数类型, 不太可能会有一个运行时表示, 这意味着开发者会受到类型擦除的困扰和限制。比如我们不太可能对方法m(T->U)和m(X->Y) 方法重载

因此,我们采用了"使用你所知"(use what you know)的方式。 因为现有的库广泛使用函数式接口,所以我们整理并利用了这个模式。这使得现有的库可以和Lambda表达式一起使用。
 为了说明这一点，这里有一些已经在Java7中存在的接口怎样适用于新的特性的例子

[java.lang.Runnable
 ](https://link.jianshu.com?t=http://download.oracle.com/javase/7/docs/api/java/lang/Runnable.html)
 [java.util.concurrent.Callable
 ](https://link.jianshu.com?t=http://download.oracle.com/javase/7/docs/api/java/util/concurrent/Callable.html)
 [java.security.PrivilegedAction
 ](https://link.jianshu.com?t=http://download.oracle.com/javase/7/docs/api/java/security/PrivilegedAction.html)
 [java.util.Comparator
 ](https://link.jianshu.com?t=http://download.oracle.com/javase/7/docs/api/java/util/Comparator.html)
 [java.io.FileFilter
 ](https://link.jianshu.com?t=http://download.oracle.com/javase/7/docs/api/java/io/FileFilter.html)
 [java.beans.PropertyChangeListener
 ](https://link.jianshu.com?t=http://www.fxfrog.com/docs_www/api/java/beans/PropertyChangeListener.html)

除此之外, Java 8 新增了一个新的包 [java.util.function
 ](https://link.jianshu.com?t=http://download.java.net/jdk8/docs/api/java/util/function/package-summary.html)
 包含了一些常用的函数式接口,例如:
 Predicate<T>    -- 输入T，返回boolean
 Consumer<T>     -- 输入T, 进行逻辑操作, 没有返回值
 Function<T,R>   -- 输入T, 返回R
 Supplier<T>     -- 提供一个T的实例 (比如工厂)
 UnaryOperator<T>    --输入T，返回T
 BinaryOperator<T>   -- 输入 (T, T) ，返回 T

除了这些基本的接口,还有一些对于基本类型的特殊处理接口,比如 IntSupplier 或LongBinaryOperator(我们并没有提供所有基础类型的特殊处理,只有int, long和double,其他的基本类型可以通过其转化得来)。与此类似的还有一些对于多个参数的特殊处理, 比如BiFunction<T,U,R>, 代表了将输入(T,U)转换为返回结果R

## 3. Lamda表达式

匿名内部类带来的最大的问题就是笨重,也可以叫做代码的”高度”问题(vertical problem), 比如前面的ActionListener接口使用了5行来封装了一个很简单的行为。

Lamda表达式是一种匿名方法，使用更为轻量级的机制替代匿名内部类解决这个问题。下面列出了一些Lambda表达式的例子:



```rust
(int x, int y) -> x + y      x, y作为入参，返回x+y
() -> 42          没有入参, 返回42
(String s) -> { System.out.println(s); }    s作为入参,打印s, 没有返回值
```

Lambda在语法上通常包含了一个参数列表 (argument list), 一个箭头符号 -> 和函数体 (body) 。函数体可以是一个表达式也可以是一个声明语句。在表达式中, 函数体被计算并且返回。在函数体中的计算类似于在方法中,return关键字会把控制权交给匿名函数的调用者;break和continue只能使用在循环(loop)中;如果函数体需要计算出一个结果,函数内每一条逻辑路径必须提供一个返回值或者抛出异常。

在通常情况下,Lambda表达式的语法被优化的非常精简。比如消除了 “return” 关键字, 相对于Lambda表达式的大小而言,return在语法上已经十分复杂了。

Lambda表达式会经常出现在嵌套的上下文 (nested contexts) 中, 比如作为方法调用的参数或者另外一个Lambda表达式的结果。为了减少这种情况带来的干扰，我们删去了一些不必要的分隔符。但是在一些我们需要将Lambda表达式分离开来的情况下, 我们可以将像其他表达式那样将它放在括号里。

下面是一些Lambda表达式在声明中出现的例子



```rust
FileFilter java = (File f) -> f.getName().endsWith(".java");
String user = doPrivileged(() -> System.getProperty("user.name"));
  new Thread(() -> {
  connectToService();
  sendNotification();
  }).start();
```

## 4.目标类型

需要注意的是函数式接口本身并不是Lambda表达式语法的一部分。所以Lambda表达式到底代表了什么？它所代表的类型是从上下文中推断出来的。举例说明, 下面的Lambda表达式就代表了一个ActionListener:



```rust
ActionListener l = (ActionEvent e) -> ui.dazzle(e.getModifiers());
```

这种做法意味着同一个Lambda表达式在不同的上下文中可以表示不同的类型。



```rust
Callable<String> c = () -> "done";     
PrivilegedAction<String> a = () -> "done";
```

() -> "done";   在第一个例子中表示了一个Callable接口的实例, 而在第二个例子中则表示了一个PrivilegedAction的实例。

编译器负责推断Lambda表达式的类型,使用表达式所在上下文中的期待(expected)类型, 我们把它叫做目标类型 (target type) 。Lambda表达式只能出现在目标类型是一个函数式接口的上下文中。

当然,没有一个Lambda表达式可以适用于每一种可能的目标类型。编译器会去检查Lambda表达式使用的类系是否和目标类型的方法签名 (method signature) 一致。也就是说, 如果下列条件满足的话, Lambda表达式的目标类型可以推断为T

1. T是函数式接口类型
2. Lambda表达式和T的参数数量相同, 并且参数类型也相同。
3. Lambda表达式的返回类型和T的返回类型一样
4. Lambda表达式抛出的异常和T的throws 抛出的异常兼容

一个函数式接口已经知晓Lambda表达式应该具有哪些参数类型,因此我们无须赘述。Lambda的参数类型可以由目标类型推断出来。



```rust
Comparator<String> c = (s1, s2) -> s1.compareToIgnoreCase(s2);
```

编译器可以推断出s1和s2是string类型。此外, 当只有一个参数需要推断的时候, () 就可以省略掉。



```rust
FileFilter java = f -> f.getName().endsWith(".java");
button.addActionListener(e -> ui.dazzle(e.getModifiers()));
```

这些改进体现了我们的设计目标: 不把垂直问题转化为水平问题。我们希望读者在阅读和使用Lambda的时候尽可能的阅读少的代码。

Lambda表达式不是第一个上下文相关类型的Java表达式: 范型方法 (generic method) 调用和<> 构造函数调用, 这些都是类似的的基于赋值类型的目标类型检查的例子。



```dart
List<String> ls = Collections.emptyList();
List<Integer> li = Collections.emptyList();
Map<String,Integer> m1 = new HashMap<>();
Map<Integer,String> m2 = new HashMap<>();
```

## 5. 目标类型的上下文

我们在之前提到Lambda表达式只能出现在具有目标类型的上下文中,列举如下:

1. 变量声明
2. 赋值
3. 返回语句
4. 数组的初始化
5. 方法或者构造器参数
6. Lambda表达式体
7. 条件表达式(:?)
8. 类型(Cast)转换表达式

在开始的3个例子中,目标类型只是简单的被赋值或者被返回的类型



```rust
Comparator<String> c;
c = (String s1, String s2) -> s1.compareToIgnoreCase(s2);
public Runnable toDoLater() {
  return () -> {
    System.out.println("later");
  };
}
```

数组初始化的上下文有点像赋值,除了"变量" (variable) 是一个数组元素并且它的类型从数组类型中推断而出。



```rust
filterFiles(new FileFilter[] { 
               f -> f.exists(), f -> f.canRead(), f -> f.getName().startsWith("q") 
            });
```

在方法的参数这个例子中情况变得更为复杂,目标类型的推断还需要依据其他两个语言特性,重载的解析(overload resolution)和类型参数推断(type argument inference)。

重载解析包括了找到特定方法调用(method invocation)的最合适的方法声明。因为不同的声明可以有不同的特征, 这点可以影响到Lambda表达式使用的参数的目标类型。编译器会使用已知的Lambda表达式的信息来做出选择。如果Lambda表达式参数是显式类型 (explicitly typed) (明确指定了参数类型), 那么编译器不仅仅知道参数的类型, 同时也知道了表达式体内的所有返回表达式的类型。如果Lambda是隐型类型(implicitly typed)(参数类型推断而来) ,重载解析会忽略掉Lambda主体而只使用Lambda的参数数量来判断。

如果在寻找最合适的方法声明时存在歧义(ambiguous),类型转换或者显式Lambda可以给编译器提供额外的类型信息。如果Lambda的返回目标类型依赖于推断出的参数类型, 那么Lambda函数体也可能给编译器提供信息用于推断参数类型



```dart
List<Person> ps = ...String<String> names = ps.stream().map(p -> p.getName());
```

ps 是一个Person的List, 所以ps.stream()是一个Person类型的Stream。 map()方法是一个R的泛型,它的参数是一个Function<T,R>, T是这个stream的元素类型(在这里我们已经知道T是Person)。当我们确定了重载类型并且知道了Lambda的目标类型,我们接下来需要去推断R;经过我们对Lambda表达式的类型检查,发现返回类型是String, R也就是String,因此这个map()表达式的类型是一个String的Stream。大多数情况下, 编译器已经可以完成推断工作, 但是如果依旧不行的话, 我们可以通过一个显式的Lambda (给参数p赋予一个显式类型) 来提供额外的类型信息。比如将Lambda转换为一个显式的目标类型Function<Person,String>或者为泛型参数R提供一个显式的参数类型(.<String>map(p -> p.getName()))。

Lambda的方法体本身也可提供目标类型, 我们通过外部的目标类型来得出内部的返回类型。这样我们可以很方便的写出一些可以返回其他函数的函数。



```rust
Supplier<Runnable> c = () -> () -> { System.out.println("hi"); };
```

与此类似,条件表达式可以从上下文中”向下”传递目标类型。



```rust
Callable<Integer> c = flag ? (() -> 23) : (() -> 42);
```

最后,如果从上下文中很难推断出目标类型,表达式的类型转换(cast)提供了显式指定Lambda的目标类型的机制。



```csharp
// Illegal: Object o = () -> { System.out.println("hi"); };
Object o = (Runnable) () -> { System.out.println("hi"); };
```

对于方法声明被不相关的函数接口类型重载引起的歧义,类型转换会非常有帮助。
 编译器的目标类型推断并不仅限于Lambda, 泛型方法调用和<>构造器调用同样利用了这个机制。下列语法在JAVA7中非法, 但是在JAVA8中可以使用。



```dart
List<String> ls = Collections.checkedList(new ArrayList<>(), String.class);
Set<Integer> si = flag ? Collections.singleton(23): Collections.emptySet();
```

## 6. 词法作用域

在内部类中确定命名(name)(和this关键字)的含义显然更加困难而且容易出错。继承的成员变量, 包括类的方法可能会覆盖外部类的声明, 并且未经限定的this关键字的引用会指向内部类本身。

Lambda表达式则相对简单, 它不从父类 (supertype) 继承任何变量名, 也不会引入新的作用域。它基于词法作用域,表达式中的变量名在封闭的上下文环境下解释执行(也包括了Lambda表达式中的形式参数)。作为自然的拓展, this关键字和对于它的成员的引用具有相同的含义。 (As a natural extension,  the this keyword and references to its members have the same meaning as they would immediately outside the Lambda expression.)

举例说明,以下程序打印了两次 "Hello, world!"



```csharp
public class Hello {
  Runnable r1 = () -> { System.out.println(this); }
  Runnable r2 = () -> { System.out.println(toString()); }
  public String toString() { return "Hello, world!"; }
  public static void main(String... args) {
    new Hello().r1.run();
    new Hello().r2.run();
  }
}
```

如果使用匿名内部类来做同样的事情,令人惊讶的是,打印出来的是外部类的引用,比如Hello$1@5b89a773 和Hello$2@537a7706。

和词法作用域的方式保持一致,遵循 其他参数化构造器(比如for循环和catch块类似)的模式,Lambda表达式的参数不可以覆盖上下文中的任何局部变量。

## 7.变量捕获

在JAVA7中,编译器对于内部类上下文中局部变量引用的检查能力非常有限(变量捕获)。如果变量被声明为非 final 的话, 就会发生编译错误。在JAVA8中,内部类和Lambda都没有了这种限制,当然final的局部变量也可以被有效的捕获。

局部变量如果不会改变,它实际上就是一个有效的final变量,换句话来说,声明一个final并不会导致编译错误。



```rust
Callable<String> helloCallable(String name) {
  String hello = "Hello";
  return () -> (hello + ", " + name);
}
```

this 关键字的引用,  包括(通过未限定字段的引用和方法调用)隐性引用本质上都是对一个final本地变量(local variable)的引用。包含此类引用的Lambda函数体实际上捕获了this的实例。其他情况下, object并不保留对于this的引用。

注: 未限定原文是unqualified, 有无限定主要是指是否对类所在的包名进行明确说明
 qualified 的例子如java.util.Date,
 unqualified的例子是 Date(这里Date就有可能是java.sql.Date)

这种机制有利于内存管理:因为内部类的实例总是持有外部类实例的强引用,如果Lambda没有捕获到外部类的成员变量,它就不会持有对外部类的引用。内部类的这个特性往往会导致内存泄漏。

尽管我们放宽了对捕获变量的语法限制，我们仍然不允许捕获可变的的局部变量。下面就是一个错误的示范。



```php
int sum = 0;
list.forEach(e -> { sum += e.size(); }); // 非法,编译错误
List<Integer> aList = new List<>();
list.forEach(e -> { aList.add(e); }); // 合法,编译通过
```

除非这个是完全串行的。我们很难保证这样的Lambda表达式不存在竞争条件。除非我们强制将其扼杀在编译阶段,让这样的方法不能逃出它所在的线程中,否则会带来更多问题。Lambda表达式对值而不是变量封闭。

另一个不支持捕获可变变量的原因是有一种更好的方法去解决累加问题,我们把它叫做归约(reduction)。[java.util.stream](https://link.jianshu.com?t=http://download.java.net/jdk8/docs/api/java/util/stream/package-summary.html) package,这个包提供了对于集合(collections)和其他数据结构的通用和特定的归约方法(sum,min,max…)。比如除了使用forEach和可变变量,我们可以用以下方法来做归约,并且在串行和并行下均能保证线程安全。



```cpp
int sum = list.stream()
              .mapToInt(e -> e.size())
              .sum();
```

sum()方法只是为了计算方便而提供的一个方法,等价于下面这个更通用的归约方式。



```cpp
int sum = list.stream()
              .mapToInt(e -> e.size())
              .reduce(0, (x,y) -> x+y);
```

归约获取到一个初始值(以防输入为空)和一个操作数(这里是加法),并且以以下方式进行计算。



```cpp
0 + list[0] + list[1] + list[2] + ...
```

归约也可以使用其他的操作符进行计算,比如最小值(minimum),最大值(maximum)和乘积(product)等等。而且如果操作满足结合律 (associative) ,我们很容易并且很安全的将其并行化计算。所以我们最终选择提供一个更利于并行化和更不易出错的类库来实现累积运算(accumulation)而不是提供某种本质上是串行而且在多处理器情况下容易引起竞争的语法。

## 8.方法引用 (Method references)

Lambda表达式允许我们定义匿名方法并且将其作为函数式接口的一个实例。对于现有的方法(method)我们也想采用同样的方式。

方法引用是一种类似Lambda的表达式(比如也需要目标类型和对函数式接口的实现),不同之处在于它
 不需要提供函数体,而是通过函数命名来指向已有的方法。
 比如我们有一个Person类,并可以按照姓名或者年龄排序,



```java
class Person { 
    private final String name;
    private final int age;
    public int getAge() { return age; }
    public String getName() { return name; }
   ...
}
Person[] people = ...
Comparator<Person> byName = Comparator.comparing(p -> p.getName());
Arrays.sort(people, byName);
```

我们也可以直接使用方法引用来指向Person.getName()。



```php
Comparator<Person> byName = Comparator.comparing(Person::getName);
```

Person::getName表达式可以被认为是一个简化的Lambda表达式, 只是简单的方法调用并且返回所需要的值。尽管方法引用在语法上看起来没有变的更加简洁(此例中),但是它更清晰,我们可以直接根据方法的名字进行调用。

因为函数式接口方法的参数充当了隐式调用的参数,引用方法签名(signature)可以通过放宽条件,装箱,以及作为可变长度的数组分组来操作参数,就像实际的方法调用。(方法签名指的是方法名和一系列参数列表和其顺序)



```rust
Consumer<Integer> b1 = System::exit;   // void exit(int status)
Consumer<String[]> b2 = Arrays::sort;  // void sort(Object[] a)
Consumer<String> b3 = MyProgram::main; // void main(String... args)
Runnable r = MyProgram::main;          // void main(String... args)
```

## 9.不同种类的方法引用

方法的引用有很多种,它们之间的语法有着细微的差别。

1. 静态方法(ClassName::methName))
2. 某个类的实例的方法(instanceReference::methName)
3. 某个类的父类方法(super::methName)
4. 一个特定类型的任意对象的实例方法(ClassName::methName)
5. 类的构造器引用(ClassName::new)
6. 数组构造器引用(TypeName[]::new)

对于静态方法引用,该方法所在的类在::分隔符前,比如Integer::sum

对于某个类的实例的引用,表达式引用的对象位于::分隔符之前。



```dart
Set<String> knownNames = ...
Predicate<String> isKnown = knownNames::contains;
```

这个隐式Lambda表达式会捕获knownNames引用的String类,表达式体会去调用Set.contains方法使用该对象。

有了引用特定类的方法这个特性,我们可以非常方便的对不同的函数式接口类型进行转换。



```swift
Callable<Path> c = ...
PrivilegedAction<Path> a = c::call;
```

对于任意对象的方法引用,方法所属的类型在分隔符之前,并且调用的接收者是函数式接口方法的第一个参数:



```dart
Function<String, String> upperfier = String::toUpperCase;
```

这个隐式的Lambda表达式有一个参数, 等待被转换为大写的String,也是toUpperCase方法的接收者(入参)。

如果实例方法是属于一个泛型类,它的参数类型可以写在::分隔符之前,或者直接由编译器推断出来(通常情况)。

需要注意的是,从语法上静态方法引用也可能被解释为类的实例方法的引用。编译器通过尝试识别每种类型的适用方法来确定到底是哪一种(实例方法比静态方法要少一个参数) 。
 注:对于实例方法引用,这个少的参数是一个隐含的this, 例子如下



```csharp
public class StaticVsInstance {
    public int value = 1;
    public int add(int i){
        return this.value + i;
    }
    public  static int add(int value, int i){
        return value + i;
    }
```

对于所有形式的方法引用,方法类型参数都可以按需推断,或者可以在::分隔符之后显式提供。

构造器也可以通过"new"关键字,以类似静态方法的方式被引用。



```dart
SocketImplFactory factory = MySocketImpl::new;
```

如果一个类有多个构造器, 那么我们就会根据目标类型的方法签名来选择最合适的一个。
 对于内部类, 语法上并不支持显式提供封闭实例的参数给构造器的引用。(For inner classes, no syntax supports explicitly providing an enclosing instance parameter at the site of the constructor reference.)

如果要实例化一个泛型,参数类型可以写在类名之后,也可以直接使用<>让编译器来做推断工作。
 数组类型的构造器引用的语法比较特殊,我们认为它有一个允许传入int参数的构造器,例子如下:



```cpp
IntFunction<int[]> arrayMaker = int[]::new;
int[] array = arrayMaker.apply(10);  // creates an int[10]
```

## 10.默认和静态方法

Lambda表达式和方法引用极大的丰富了Java语言, 但是真正要实现我们"代码即数据"这个目标的关键是通过合适的类库来利用这些新特性。

在Java7中,给已有的类库增加新功能十分困难。特别是接口在发布(publish)以后就会定型, 除非有人可以把接口的所有实现同时更改, 否则向接口中新加一个方法会导致已有的实现出问题。增加默认方法(default methods)的目的是为了使接口在发布以后还可以做修改。

举例说明,标准的集合类(collection)API应该提供对Lambda操作的支持。比如removeAll 方法可以被概括为删除collection中的所有元素,无论元素类型是什么 ,这个类型应该是函数式接口Predicate的一个实例。但是这个新的方法(default boolean removeIf(Predicate<? super E> filter))要在哪里定义?我们不能直接在Collection接口中直接新增一个抽象方法, 因为已有的实现类并不知道这些。我们可以在Collections 工具类中增加一个静态方法,但是这样就会把这个新方法降为"次级地位"。

注: 在Java8中不推荐类似Maps,Collections,Lists这种工具类做法,鬼知道里面做了什么,而且有些人可能根本不知道这些辅助类。

默认方法提供了一个更"面向对象"的方法来给接口增加一个具体的实现。我们给接口增加了新的类型方法, 可以是抽象或者默认的。默认方法有具体的实现,并且接口的实现类不需要重写(override)这个方法。默认方法并不算在函数式接口的抽象方法的数目限制中。

比如我可以增加一个skip方法到Iterator中,如下所示:



```java
interface Iterator<E> {
    boolean hasNext();
    E next();
    void remove();
    default void skip(int i) {
        for (; i > 0 && hasNext(); i--) next();
    }
}
```

对于以上Iterator的定义, 所有实现了Iterator的类会自动继承这个skip方法。从使用者的角度来看, skip只是接口提供的另外一个虚方法 (virtual method) 。调用这个子类实例的skip方法并不需要类本身有skip的实现,而会直接调用默认方法。当然如果子类有更为优雅或优化的实现, 也可以对这个方法进行重写。

当子接口继承父接口的时候,可以用default方法重写父接口中的default以及抽象方法 ,也可以将原有的default方法重新抽象化。

除了默认方法以外,Java8还允许在接口中使用静态(static)方法,这也使我们可以把工具类方法放置在接口中而不是放在它的辅助类中(这种类通常命名为接口的复数形式, 比如Collections, Arrays)。举例来说, Comparator接口可以定义一个静态工具方法去生成比较器。



```java
public Comparator<T> comparing(Function<T, U> keyExtractor) {
    return (c1, c2) -> keyExtractor.apply(c1).compareTo(keyExtractor.apply(c2));
}
```

## 11. 默认方法的继承

默认方法的继承和其他方法几乎没有什么区别。但是当一个类或者接口的父类提供了多个有相同方法签名的方法的时候，继承的规则(inheritance rules)会尝试解决冲突。我们有以下两个准则:

1. 类的方法声明优先于接口默认方法, 无论类的方法是具体还是抽象 (因此接口的默认方法是在类的整个层级没有提供任何有效信息的情况下的备选方案)
2. 在多个父接口共有一个”祖父”接口的情况下,被其他接口覆盖的方法会被忽略,
    第二条规则的解释我们有这个例子,比如Collection和List接口提供了不同的removeAll的默认方法,然后Queue继承了Collection的默认方法;在下面的实现中,List的声明的优先级高于Queue中的声明。



```ruby
class LinkedList<E> implements List<E>, Queue<E> { ... }
```

当两个独立的默认方法冲突的时候,或者一个默认方法和一个抽象方法冲突的时候,会出现编译错误。这时我们必须显式的覆盖父类中的方法。通常这也意味着需要选择一个优先的默认值并且在定义函数体的时候调用它。我们对super关键字在语法上也有所加强, 可以使用特定父类的实现。



```java
interface Robot extends Artist, Gun {
    default void draw() { Artist.super.draw(); }
}
```

super 前的名字必须指向一个定义了这个default方法的父类 (接口)。这种形式的方法调用不仅仅局限于消除歧义 ,也可以在类和接口中直接使用。
 在任何情况下,我们在实现多个接口的时候,接口的顺序并不会有任何影响。

## 12.组合应用

Lambda语言和库特性协同工作, 我们以下面的例子说明: 根据姓将人的list排序
 之前我们这么写,看起来十分臃肿。



```csharp
List<Person> people = ...
Collections.sort(people, new Comparator<Person>() {
    public int compare(Person x, Person y) {
        return x.getLastName().compareTo(y.getLastName());
    }
});
```

有了Lambda,我们可以更加精确。



```css
Collections.sort(people, (Person x, Person y) -> x.getLastName().compareTo(y.getLastName()));
```

但是这种方法并不抽象,我们仍然需要做真正的对比(当对比的key是基本类型的时候会更糟糕)。类库中小小的改动可以帮助改进, 比如在Comparator中增加一个静态的comparing 方法。



```css
Collections.sort(people, Comparator.comparing((Person p) -> p.getLastName()));
```

我们还可以静态引入Compatator.comparing,以及让编译器进行类型推断来使其更精减。



```css
Collections.sort(people, comparing(p -> p.getLastName()));
```

这个Lambda表达式只是一个对于已有方法getLastName的转发器, 我们也可以使用方法引用来代替Lambda。



```css
Collections.sort(people, comparing(Person::getLastName));
```

我们并不想使用像Collections.sort这种的辅助方法,它十分的繁琐,而且也没有办法给实现list的类做定制化处理,而且用户在使用List接口和阅读对应的java文档的时候很难找到这个方法。

默认方法提供了一个更加"面向对象"的解决方案，我们在List中增加了一个默认sort方法。



```css
people.sort(comparing(Person::getLastName));
```

这样看起来和我们的题设更加贴切: 根据姓将人的列表排序。

如果我们在Comparator中加一个默认的reverse方法,我们可以以降序的方式排序。



```css
people.sort(comparing(Person::getLastName).reversed());
```

## 13.总结

Java8增加了一些新的特性, 比如Lambda表达式, 方法引用, 接口中的默认和静态方法, 还有更为广泛的类型推断。这些特性可以更简洁和精确的表达我们的意图,并且使我们可以更容易的开发功能更为强大和易于并行的类库。

