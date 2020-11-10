### Netty简介

Netty是一个高性能，高可扩展性的异步事件驱动的网络应用程序框架，它极大的简化了TCP和UDP客户端和服务器端网络开发。它是一个NIO框架，对Java NIO进行了良好的封装。作为一个异步NIO框架，Netty的所有IO操作都是异步非阻塞的，通过Future-Listener机制，用户可以方便的主动获取或者通过通知机制获得IO操作结果。

### Netty的特性

- 统一的API，适用于不同的协议
- 基于灵活、可扩展的事件驱动模型
- 高度可定制的线程模型
- 更好的吞吐量，低延迟
- 更省资源，尽量减少不必要的内存拷贝
- 完整的SSL/TLS和STARTTLS的支持
- 能在Applet与Android的限制环境运行良好
- 不再因过快、过慢或超负载连接导致OutOfMemoryError
- 不再有在高速网络环境下NIO读写频率不一致的问题

### Netty核心内容

Netty中最核心的内容主要有以下四个方面：

- Reactor线程模型：一种高性能的多线程程序设计思路
- Netty中自己定义的Channel概念：增强版的通道概念
- ChannelPipeline职责链设计模式：事件处理机制
- 内存管理：增强的ByteBuf缓冲区

### Netty整体结构图

![img](https://gitee.com/mo-se-de-feng/notes/raw/master/images/2.2.1%20Netty%E7%BB%93%E6%9E%84%E5%9B%BE.PNG)

### Netty核心组件

**EventLoop**：EventLoop维护了一个线程和任务队列，支持异步提交执行任务。EventLoop自身实现了Executor接口，当调用executor方法提交任务时，则判断是否启动，未启动则调用内置的executor创建新线程来触发run方法执行，其大致流程参考Netty源码SingleThreadEventExecutor如下：

![img](https://gitee.com/mo-se-de-feng/notes/raw/master/images/2.2.1%20EventLoop%E5%90%AF%E5%8A%A8.PNG)

**EventLoopGroup**： EventLoopGroup主要是管理eventLoop的生命周期，可以将其看作是一个线程池，其内部维护了一组EventLoop，每个eventLoop对应处理多个Channel，而一个Channel只能对应一个EventLoop

![img](https://gitee.com/mo-se-de-feng/notes/raw/master/images/2.2.1%20EventLoopGroup.PNG)

**Bootstrap**：BootStrap 是客户端的引导类，主要用于客户端连接远程主机，有1个EventLoopGroup。Bootstrap 在调用 bind()（连接UDP）和 connect()（连接TCP）方法时，会新创建一个单独的、没有父 Channel 的 Channel 来实现所有的网络交换。

**ServerBootstrap**： ServerBootstrap 是服务端的引导类，主要用户服务端绑定本地端口，有2个EventLoopGroup。ServerBootstarp 在调用 bind() 方法时会创建一个 ServerChannel 来接受来自客户端的连接，并且该 ServerChannel 管理了多个子 Channel 用于同客户端之间的通信。

**Channel**：Netty中的Channel是一个抽象的概念，可以理解为对Java NIO Channel的增强和扩展，增加了许多新的属性和方法，如bing方法等。

**ChannelFuture**：ChannelFuture能够注册一个或者多个ChannelFutureListener 实例，当操作完成时，不管成功还是失败，均会被通知。ChannelFuture存储了之后执行的操作的结果并且无法预测操作何时被执行，提交至Channel的操作按照被唤醒的顺序被执行。

**ChannelHandler**：ChannelHandler用来处理业务逻辑，分别有入站和出站的实现。

**ChannelPipeline**： ChannelPipeline 提供了 ChannelHandler链的容器，并定义了用于在该链上传播入站和出站事件流的API。

### Netty线程模型

Netty的线程模型是基于Reactor模式的线程实现。关于Reactor模式可以参考 [Reactor模式](http://2.1.xn--3 reactor-4e1vn03i.md/) ，Netty中依据用户的配置可以支持单线程的Reactor模型，多线程的Reactor模型以及主从多Reactor的模型。在Netty中其大致流程如下如下：

![img](https://gitee.com/mo-se-de-feng/notes/raw/master/images/2.2.1 Netty线程模型.PNG)