1. 资源分配 -内核的主要功能是管理计算机的资源，并允许其他程序运行和使用这些资源。这些资源是-CPU，内存和I / O设备。

2. 进程管理 - 进程定义应用程序可以访问哪些内存部分。内核的主要任务是允许执行应用程序，并通过诸如硬件抽象之类的功能来支持它们。

要运行应用程序，内核首先为应用程序设置地址空间，然后将包含应用程序代码的文件加载到内存中，然后为程序设置堆栈并分支到程序内部的给定位置，从而最终启动程序执行。

3. 内存管理 -内核具有对系统内存的完全访问权限。它允许进程根据需要安全地访问此内存。虚拟寻址可帮助内核在两个不相连的区域中创建内存的虚拟分区，其中一个保留给内核（内核空间），另一个保留给应用程序（用户空间）。

4. I / O设备管理 -要执行有用的功能，进程需要访问连接到计算机的外围设备，这些外围设备由内核通过设备驱动程序控制。设备驱动程序是使操作系统与硬件设备进行交互的计算机程序。它为操作系统提供有关如何控制特定硬件并与之通信的信息。

内核维护可用设备的列表。设备管理器首先在不同的硬件总线（例如外围组件互连（PCI）或通用串行总线（USB））上执行扫描，以检测已安装的设备，然后搜索适当的驱动程序。内核提供I / O，以允许驱动程序通过某个端口或内存位置物理访问其设备。

5. 进程间通信 -内核提供了用于进程之间的同步和通信的方法，称为进程间通信（IPC）。IPC有多种方法，例如信号量，共享内存，消息队列，管道（或称为fifo）等。

6. 调度 -在多任务系统中，内核会给每个程序一个时间片，并从一个进程到另一个进程的切换如此之快，以至于用户看起来就像是同时在执行这些进程一样。内核使用调度算法来确定下一个正在运行的进程以及将分配多少时间。该算法在进程之间设置优先级。

7. 系统调用和中断处理 - 系统调用是一种机制，应用程序使用该机制向操作系统请求服务。系统调用包括关闭，打开，读取，等待和写入。要访问内核提供的服务，我们需要调用相关的内核函数。大多数内核提供C库或API，这些库又调用相关的内核功能。

很少有方法可以调用相应的内核功能，包括使用软件模拟的中断，使用门控调用，使用特殊系统调用指令以及使用基于内存的队列。

8. 安全或保护管理 -内核还提供了针对错误（错误控制）和恶意行为（安全）的保护。一种解决方案是基于语言的保护系统，其中内核将仅允许执行由受信任的语言编译器生成的代码。



kernel
A kernel provides programming language support in Jupyter. IPython is the default kernel. Additional kernels include R, Julia, and many more.
内核在Jupyter中提供编程语言支持


enterprise启动时 会启动2种镜像
- elyra/kernel-image-puller:dev
  - The ability to add new nodes and have kernel images on those nodes automatically populated. 添加新节点并在这些节点上自动填充内核映像的功能。
  - The ability to configure new kernelspecs that use different images and have those images pulled to all cluster nodes. 配置使用不同映像的新内核规范并将这些映像拉到所有群集节点的能力。

- elyra/enterprise-gateway:dev


https://jupyter-enterprise-gateway.readthedocs.io/en/latest/_images/Scalability-After-JEG.gif


Jupyter Enterprise Gateway不管理多个Jupyter Notebook部署，因此您应该使用JupyterHub

Jupyter Enterprise Gateway是一个Web服务器，它提供对企业内Jupyter内核的无头访问。
- 增加了对整个企业中托管的远程内核的支持，可以通过以下方式启动内核：
    - 在企业网关服务器本地（今天的内核网关行为）
    - 使用轮循算法在群集的特定节点上
    - 在关联的资源管理器标识的节点上
- 提供对开箱即用的由YARN，IBM Spectrum Conductor，Kubernetes或Docker Swarm管理的Apache Spark的支持。其他的则可以通过Enterprise Gateway的可扩展框架进行配置。
- 从客户端通过Enterprise Gateway服务器到内核的安全通信
- 多租户功能
- 永久内核会话
- 能够将给定用户的配置文件（包括配置设置）与内核相关联


可以在托管集群中启动和分布的Python / R / Toree内核。




https://jupyter-enterprise-gateway.readthedocs.io/en/latest/roadmap.html
- 内核配置文件
- 使客户端能够为内核请求不同的资源配置（例如，小，中，大）
- 配置文件应由管理员定义并为用户和/或组启用。
- 管理界面
- 具有正在运行的内核的仪表板
- 生命周期管理
- 时间运行，停止/终止，配置文件管理等
- 支持其他资源管理
- 用户环境
- 高可用性
- 会话持久性



enterprise gateway支持的功能
- 能够在不同服务器上启动内核，从而在整个企业中分配资源利用率
- 可插拔框架允许支持其他资源管理器
- 从客户端到内核的安全通信
- 持久性内核会话

在以下情况下使用Enterprise Gateway：
- 您有一个由有限资源（GPU，大内存等）组成的大型计算机集群，并且用户需要笔记本电脑提供的这些资源
- 您有大量需要访问共享计算群集的用户
- 您需要一定数量的高可用性/灾难恢复，以便可以启动另一个网关服务器来服务现有（和远程）内核



Pull this image, along with all of the elyra/kernel-* images to each of your managed nodes. Although manual seeding of images across the cluster is not required, it is highly recommended since kernel startup times can timeout and image downloads can seriously undermine that window.


通过将内核作为受管资源运行，并利用基础资源管理器来利用所有群集节点上的资源

可插拔架构，以支持其他资源管理器