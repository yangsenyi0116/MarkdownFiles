# 学习资料

本篇文章，打算总结下学习过程，分析学习要点。

关于netty的资料颇多，学习起来未免杂乱。对于初学者麻烦点在于难以找到重点，再此则推荐一个学习路线。

- [Netty 实战(精髓)](https://waylau.gitbooks.io/essential-netty-in-action/)
- [Netty权威指南](http://item.jd.com/11681556.html)
- 通信理论学习，akka bio nio aio（可以绕过）

# *第一步* 上手练习

## Telnet示例（15分钟）

[使用Netty创建Telnet服务](http://www.jianshu.com/p/5dcbc0456376)
[官方示例TelnetClient](https://netty.io/4.0/xref/io/netty/example/telnet/TelnetClient.html)

```
推荐这个示例是因为它足够简单，简单到客户端也没有。
```

## 进阶示例（30分钟）

《Netty 实战(精髓》->[第一个netty应用)](https://waylau.gitbooks.io/essential-netty-in-action/GETTING STARTED/Your first Netty application.html)

```
在这一章能够学习到更多细节。
```

- 服务端 

  (serverChannel)

  - EchoServerHandler 简单的接收打印，将所接收的消息返回给发送者
  - 冲刷所有待审消息到远程节点
  - 关闭通道后，操作完成

通过这几步代码，学习 *Netty* 组件模型的基础 -> *ChannelHandler* ，它是重中之重。在稍后的第二步将会深入讲解。

- 服务端 

  (server)

  - EchoServer 创建EventLoopGroup、ServerBootstrap
  - 指定 Channel
  - 设置 socket 地址使用所选的端口
  - 添加 EchoServerHandler 到 Channel 的 ChannelPipeline
  - 绑定的服务器;sync 等待服务器关闭
  - 关闭 channel 和 块，直到它被关闭
  - 关机的 EventLoopGroup，释放所有资源

- 客户端 *(client)* -> Bootstrap的构建过程

http://www.jianshu.com/p/5dcbc0456376

# *第二步* 熟悉组件

对于从零开始，想要快速上手的同学来说。只想说直接给一份代码，从安全角度出发也不敢用到生产环境。出了问题也无从下手修改。

一些实用组件推荐，请务必熟悉它们的作用。
跟着[Netty 实战(精髓)](https://waylau.gitbooks.io/essential-netty-in-action/)，看《[Netty 总览](https://waylau.gitbooks.io/essential-netty-in-action/GETTING STARTED/Netty Overview.html)》

- 学习步骤如下：

1. Channel、ChannelHandler

2. Encode、Decode、ByteBuf

3. ChannelHandlerContext

4. ReferenceCountUtil

5. ChannelFuture、Promise

6. ChannelPipeline、ChannelInitializer

7. EventLoop、EventLoopGroup

8. Attributekey

9. CharsetUtil

   尽量，按照其在整个通信过程中所属顺位，讲解所有 *Netty* 主要组件。大致结构图如下：
   ![img](https://images2017.cnblogs.com/blog/538961/201712/538961-20171222113929678-1562710551.png)

## 1.Channel、ChannelHandler

熟悉ChannelInboundHandlerAdapter 抽象类里面每个方法的用处。

利用责任单一原则，把消息

## 2.Encode、Decode、ByteBuf

- Encode、Decode
- ByteBuf
  (ByteBuf和相关辅助类)[http://www.cnblogs.com/wade-luffy/p/6196481.html]
  (WroldClock)[https://netty.io/4.0/xref/io/netty/example/worldclock/WorldClockServerHandler.html]

# *第三步* 难点、要点

1. EventLoop 数量、名称
2. 粘包、半包
3. 心跳 3次
4. 重连
5. session
6. 不想转关键字
7. 内存保护、溢出oom
8. 连接数与释放
9. 拒绝连接、黑名单、白名单
10. Nginx
11. fail over重试与幂等性

# *第四步* 高阶原理

对于已经在项目中应用了 *netty* 的同学，更多的想学习源码，但是并不推荐直接看源码。
因为常常在没有一定设计功底的情况下，会看的不明所以。

1. ByteBuf
2. Channel 和Unsafe
3. ChannelPipeline 和ChannelHandler
4. EventLoop 和EventLoopGroup
5. Future 和Promise

# 总结

更多协议类型支持，请自行根据需求搜索相应资料。

在通过github搜索相应源码参考学习时，切记分辨出事netty3.x与netty4.+版本。因为改动很大，很多资料已经没有参考价值。

# 分布式服务

- 幂等性
- fail over、fail fast、fail safe、fail back
- 分布式事务
- cas原子锁
- zookeeper与paxos算法





-------

关于netty的资料颇多，学习起来未免杂乱。对于初学者麻烦点在于难以找到重点，再此则推荐一个学习路线。

- [Netty 实战(精髓)](https://waylau.gitbooks.io/essential-netty-in-action/)
- [Netty权威指南](http://item.jd.com/11681556.html)
- 通信理论学习，akka bio nio aio（可以绕过）

# *第一步* 上手练习

## Telnet示例（15分钟）

[使用Netty创建Telnet服务](http://www.jianshu.com/p/5dcbc0456376)
[官方示例TelnetClient](https://netty.io/4.0/xref/io/netty/example/telnet/TelnetClient.html)

```
推荐这个示例是因为它足够简单，简单到客户端也没有。
```

## 进阶示例（30分钟）

《Netty 实战(精髓》->[第一个netty应用)](https://waylau.gitbooks.io/essential-netty-in-action/GETTING STARTED/Your first Netty application.html)

```
在这一章能够学习到更多细节。
```

- 服务端

   

  (serverChannel)

  - EchoServerHandler 简单的接收打印，将所接收的消息返回给发送者
  - 冲刷所有待审消息到远程节点
  - 关闭通道后，操作完成

通过这几步代码，学习 *Netty* 组件模型的基础 -> *ChannelHandler* ，它是重中之重。在稍后的第二步将会深入讲解。

- 服务端

   

  (server)

  - EchoServer 创建EventLoopGroup、ServerBootstrap
  - 指定 Channel
  - 设置 socket 地址使用所选的端口
  - 添加 EchoServerHandler 到 Channel 的 ChannelPipeline
  - 绑定的服务器;sync 等待服务器关闭
  - 关闭 channel 和 块，直到它被关闭
  - 关机的 EventLoopGroup，释放所有资源

- 客户端 *(client)* -> Bootstrap的构建过程

http://www.jianshu.com/p/5dcbc0456376

# *第二步* 熟悉组件

对于从零开始，想要快速上手的同学来说。只想说直接给一份代码，从安全角度出发也不敢用到生产环境。出了问题也无从下手修改。

一些实用组件推荐，请务必熟悉它们的作用。
跟着[Netty 实战(精髓)](https://waylau.gitbooks.io/essential-netty-in-action/)，看《[Netty 总览](https://waylau.gitbooks.io/essential-netty-in-action/GETTING STARTED/Netty Overview.html)》

- 学习步骤如下：

1. Channel、ChannelHandler

2. Encode、Decode、ByteBuf

3. ChannelHandlerContext

4. ReferenceCountUtil

5. ChannelFuture、Promise

6. ChannelPipeline、ChannelInitializer

7. EventLoop、EventLoopGroup

8. Attributekey

9. CharsetUtil

   尽量，按照其在整个通信过程中所属顺位，讲解所有 *Netty* 主要组件。大致结构图如下：
   ![img](https://images2017.cnblogs.com/blog/538961/201712/538961-20171222113929678-1562710551.png)

## 1.Channel、ChannelHandler

熟悉ChannelInboundHandlerAdapter 抽象类里面每个方法的用处。

利用责任单一原则，把消息

## 2.Encode、Decode、ByteBuf

- Encode、Decode
- ByteBuf
  (ByteBuf和相关辅助类)[http://www.cnblogs.com/wade-luffy/p/6196481.html]
  (WroldClock)[https://netty.io/4.0/xref/io/netty/example/worldclock/WorldClockServerHandler.html]

# *第三步* 难点、要点

1. EventLoop 数量、名称
2. 粘包、半包
3. 心跳 3次
4. 重连
5. session
6. 不想转关键字
7. 内存保护、溢出oom
8. 连接数与释放
9. 拒绝连接、黑名单、白名单
10. Nginx
11. fail over重试与幂等性

# *第四步* 高阶原理

对于已经在项目中应用了 *netty* 的同学，更多的想学习源码，但是并不推荐直接看源码。
因为常常在没有一定设计功底的情况下，会看的不明所以。

1. ByteBuf
2. Channel 和Unsafe
3. ChannelPipeline 和ChannelHandler
4. EventLoop 和EventLoopGroup
5. Future 和Promise