## OSI7层参考模型
1. 物理层
2. 链路层
3. 网络层
4. 传输控制层
5. 会话层
6. 表示层
7. 应用层

## TCP 5层模型
1. 物理层
2. 链路层
3. 网络层
4. 传输控制层
5. 应用层

```shell
exec 8<> /dev/tcp/www.baidu.com/80
# 在/proc/$$(当前进程对象)/fd下边创建文件描述符8并拥有输入输出流，指向baidu的80端口的socket连接
echo -e "GET / HTTP/1.0\n" 1>& 8
# -e格式化输出
# 发送一个请求根路径资源的http1.0的请求，指定给1这个文件描述符并发送给8的输入流

# $$ 当前进程id

# > 到文件
# >& 到文件描述符
cat 0<& 8
```

## TCP 协议

- 面向连接的
- 可靠的传输

### 三次握手

c –syn-> s

s–syn+ack-> c

c –ack–> s

### 四次分手

c –fin–> s

s –fin ack–> c

s –fin –> c 

c –ack–> s

## Socket

### 套接字

`ip:port=>ip:port `

### 多个socket对应一个进程：多路复用

## Linux抓包工具`tcpdump`

```bash
tcpdump -nn -i eth0 port 80
## -nn 所有的ip端口号转换成数 字显示 -i 接口名称 
```



mac是链路层

ip是网络层

## BIO 模型

![image-20201014231539133](../../images/image-20201014231539133.png)