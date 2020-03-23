## 146. 请问运行时异常与受检异常有什么区别？

异常表示程序运行过程中可能出现的非正常状态，运行时异常表示虚拟机的通常操作中可能遇到的异常，是一种常见运行错误，只要程序设计得没有问题通常就不会发生。受检异常跟程序运行的上下文环境有关，即使程序设计无误，仍然可能因使用的问题而引发。Java编译器要求方法必须声明抛出可能发生的受检异常，但是并不要求必须声明抛出未被捕获的运行时异常。异常和继承一样，是面向对象程序设计中经常被滥用的东西，在Effective Java中对异常的使用给出了以下指导原则：
\- 不要将异常处理用于正常的控制流（设计良好的API不应该强迫它的调用者为了正常的控制流而使用异常）
\- 对可以恢复的情况使用受检异常，对编程错误使用运行时异常
\- 避免不必要的使用受检异常（可以通过一些状态检测手段来避免异常的发生）
\- 优先使用标准的异常
\- 每个方法抛出的异常都要有文档
\- 保持异常的原子性
\- 不要在catch中忽略掉捕获到的异常



## 147. 请问什么是java序列化？以及如何实现java序列化？

序列化就是一种用来处理对象流的机制，所谓对象流也就是将对象的内容进行流化。可以对流化后的对象进行读写操作，也可将流化后的对象传输于网络之间。序列化是为了解决在对对象流进行读写操作时所引发的问题。
序列化的实现：将需要被序列化的类实现Serializable接口，该接口没有需要实现的方法，implements Serializable只是为了标注该对象是可被序列化的，然后使用一个输出流(如：FileOutputStream)来构造一个 ObjectOutputStream(对象流)对象，接着，使用ObjectOutputStream对象的writeObject(Object obj)方法就可以将参数为obj的对象写出(即保存其状态)，要恢复的话则用输入流。



## 148. 请问java中有几种类型的流？JDK为每种类型的流提供了一些抽象类以供继承，请说出他们分别是哪些类？

字节流，字符流。字节流继承于InputStream OutputStream，字符流继承于InputStreamReader OutputStreamWriter。在java.io包中还有许多其他的流，主要是为了提高性能和使用方便。



## 149. 请说明一下Java中的异常处理机制的原理以及如何应用。

当JAVA 程序违反了JAVA的语义规则时，JAVA虚拟机就会将发生的错误表示为一个异常。违反语义规则包括2种情况。一种是JAVA类库内置的语义检查。例如数组下标越界,会引发IndexOutOfBoundsException;访问null的对象时会引发NullPointerException。另一种情况就是JAVA允许程序员扩展这种语义检查，程序员可以创建自己的异常，并自由选择在何时用throw关键字引发异常。所有的异常都是 java.lang.Thowable的子类。



## 150. 请问你平时最常见到的runtime exception是什么？

- ArithmeticException,
- ArrayStoreException,
- BufferOverflowException,
- BufferUnderflowException,
- CannotRedoException,
- CannotUndoException,
- ClassCastException,
- CMMException,
- ConcurrentModificationException,
- DOMException,
- EmptyStackException,
- IllegalArgumentException,
- IllegalMonitorStateException,
- IllegalPathStateException,
- IllegalStateException,
- ImagingOpException,
- IndexOutOfBoundsException,
- MissingResourceException,
- NegativeArraySizeException,
- NoSuchElementException,
- NullPointerException,
- ProfileDataException,
- ProviderException,
- RasterFormatException, SecurityException, SystemException, UndeclaredThrowableException,
- UnmodifiableSetException,
- UnsupportedOperationException



## 151. 请问error和exception有什么区别?

error 表示恢复不是不可能但很困难的情况下的一种严重问题。比如说内存溢出。不可能指望程序能处理这样的情况。
exception 表示一种设计或实现问题。也就是说，它表示如果程序运行正常，从不会发生的情况。



## 152. 请问运行时的异常与一般情况下出现的异常有什么相同点和不同点？

异常表示程序运行过程中可能出现的非正常状态，运行时异常表示虚拟机的通常操作中可能遇到的异常，是一种常见运行错误。java编译器要求方法必须声明抛出可能发生的非运行时异常，但是并不要求必须声明抛出未被捕获的运行时异常。



## 153. 请问如何打印日志？

cat /var/log/*.log

如果日志在更新，如何实时查看tail -f /var/log/messages

还可以使用watch -d -n 1 cat /var/log/messages

-d表示高亮不同的地方，-n表示多少秒刷新一次。