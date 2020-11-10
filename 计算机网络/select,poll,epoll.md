![操作系统IO](../../images/%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9FIO.png)


```java
package com.kermi.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Kermi
 * @version 1.0.0
 * @date 2020-10-15
 */
public class SocketMultplexingSingleThreadv1 {

    int port = 9090;
    private ServerSocketChannel server = null;
    private Selector selector = null; // linux 多路复用器(select poll epoll kqueue) nginx event{}

    public void initServer() {
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));

            // 如果在epoll模型下,open--> epoll_create -> fd3
            selector = Selector.open(); //  select poll *epoll 优先选择：epoll 但是可以-D修正

            // server约等于listen状态的fd4
            /*
            register
            如果:
            select,poll: jvm里面开辟一个数组fd4放进去
            epoll: epoll_ctl(fd3, ADD, fd4, EPOLLIN
            */
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        initServer();
        System.out.println("服务器启动了....");
        try {
            while (true) {
                Set<SelectionKey> keys = selector.keys();
                System.out.println(keys.size() + "    size");

                // 1. 调用多路复用器(select, poll or epoll(epoll_wait))
                /*
                select()是什么意思
                1. select poll 内核的select(fd4) poll(fd4)
                2. epoll: 内核的epoll_wait()
                参数可以带时间： 没有时间，0： 阻塞，有时间 设置一个超时时间
                selector.wakeup() 结果返回0
                 */
                while (selector.select(500) > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selectionKeys.iterator();
                    // NIO 自己对着每一个fd采用系统调用，浪费资源，

                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove(); // set   不溢出会重复循环处理
                        if (key.isAcceptable()) {
                            // 语义上accpet接收连接并返回新的FD
                            // 那信的FD怎么办？
                            // select, poll,因为他们的内核没有空间，那么在jvm中保存和前边的fd那个listen的一起
                            // epoll 我们希望通过epoll_ctl把信的客户端fd注册到内核空间
                            acceptHandler(key);
                        } else if (key.isReadable()) {
                            readHandler(key);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHandler(SelectionKey key) {
        SocketChannel client = ((SocketChannel) key.channel());
        ByteBuffer buffer = ((ByteBuffer) key.attachment());

        buffer.clear();
        int read = 0;

        try {
            while (true) {

                read = client.read(buffer);
                while (read != -1) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        System.out.println((char) buffer.get());
                    }
                    buffer.clear();
                    read = client.read(buffer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptHandler(SelectionKey key) {
        try {
            ServerSocketChannel ssc = ((ServerSocketChannel) key.channel());
            SocketChannel client = ssc.accept(); // 目的是调用accept接收客户端 fd7
            client.configureBlocking(false);

            ByteBuffer buffer = ByteBuffer.allocate(8192);

            // 调用了register
            /*
            select,poll, jvm里面开辟一个数组fd7放进去
            epoll: epoll_ctl(fd3,ADD, fd7, EPOLLIN
             */
            client.register(selector, SelectionKey.OP_READ, buffer);
            System.out.println("-----------------------------------");
            System.out.println("新客户端:" + client.getRemoteAddress());
            System.out.println("-----------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

多线程版

```java
package com.kermi.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Kermi
 * @version 1.0.0
 * @date 2020-10-16
 */
public class SocketMultplexingThreads {
    int port = 9090;
    private ServerSocketChannel server = null;
    private Selector selector1 = null;
    private Selector selector2 = null;
    private Selector selector3 = null;

    public static void main(String[] args) {
        SocketMultplexingThreads service = new SocketMultplexingThreads();
        service.initServer();
        NioThread T1 = new NioThread(service.selector1, 2);
        NioThread T2 = new NioThread(service.selector2);
        NioThread T3 = new NioThread(service.selector3);

        T1.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        T2.start();
        T3.start();

        System.out.println("服务启动了");
    }

    public void initServer() {
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));
            selector1 = Selector.open();
            selector2 = Selector.open();
            selector3 = Selector.open();
            server.register(selector1, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class NioThread extends Thread {
    static int selectors = 0;
    static volatile BlockingQueue<SocketChannel>[] queue;
    static AtomicInteger idx = new AtomicInteger();
    Selector selector = null;
    int id = 0;

    NioThread(Selector selector, int n) {
        this.selector = selector;
        this.selectors = n;

        queue = new LinkedBlockingDeque[selectors];
        for (int i = 0; i < n; i++) {
            queue[i] = new LinkedBlockingDeque<>();
        }
        System.out.println("Boss启动");
    }

    NioThread(Selector sel) {
        this.selector = sel;
        id = idx.getAndIncrement() % selectors;
        System.out.println("worker" + id + "启动");
    }

    @Override
    public void run() {
        try {
            while (true) {
                while (selector.select(10) > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selectionKeys.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isAcceptable()) {
                            acceptHandler(key);
                        } else if (key.isReadable()) {
                            readHandler(key);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHandler(SelectionKey key) {
        SocketChannel client = ((SocketChannel) key.channel());
        ByteBuffer buffer = ((ByteBuffer) key.attachment());

        buffer.clear();
        int read = 0;

        try {
            while (true) {

                read = client.read(buffer);
                while (read != -1) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        System.out.println((char) buffer.get());
                    }
                    buffer.clear();
                    read = client.read(buffer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptHandler(SelectionKey key) {

        try {
            ServerSocketChannel ssc = ((ServerSocketChannel) key.channel());
            SocketChannel client = ssc.accept();
            client.configureBlocking(false);
            int num = idx.getAndIncrement() % selectors;
            queue[num].add(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```