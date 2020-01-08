## docker提供了C/S模式以及远程访问模式
1. server-client模式
   user<->Docker CLI客户端<->docker守护进程

   docker 的守护进程即server端运行在宿主机上，守护进程在启动后一直在后端运行，而用户不会直接和守护进程交互，而是和docker的客户端即docker命令行接口交互，从用户接受命令传递给守护进程。守护进程接受到命令后执行并返回运行结果。
2. server-RemoteAPI
   user<->自定义程序<->docker守护进程
   除了使用docker命令行接口与server端进行通信外，还可以使用RemoteAPI进行通信，它是一种Restful风格的API，即可自行编写程序与docker进行集成

> docker客户端和守护进程间通过socket进行连接，docker提供了三种连接模式
> unix:///var/run/docker.sock(默认链接方式)
> tcp://host:port
> fd://socketfd


java-docker 包主要使用了Jersey 2.X版本去操纵Docker
Jersey是JAX-RS（JSR311）开源参考实现用于构建RESTful Web service
在2.0以后还加入了OKHTTP来提高性能和安全性
OKHttp是一款高效的HTTP客户端，支持连接同一地址的链接共享同一个socket，通过连接池来减小响应延迟，还有透明的GZIP压缩，请求缓存等优势，其核心主要有路由、连接协议、拦截器、代理、安全性认证、连接池以及网络适配，拦截器主要是指添加，移除或者转换请求或者回应的头部信息
java-docker就是用简单易用的java代码拼凑出一个完整的请求，该请求包括了，请求域，请求头，请求目标，然后调用netty，用同步非阻塞的方式发送给docker的服务端，确保命令的可达性，服务端调用成功后返回调用信息给客户端。


docker最新的APi为EngineAPI。
Engine API是Docker Engine提供的HTTP API。它是Docker客户端用来与引擎通信的API，因此Docker客户端可以做的所有事情都可以通过API来完成。

比如
1. 列出所有容器
   GET /containers/json
   可带参数
   1. all type:boolean default:false
   返回所有容器。默认情况下，仅显示正在运行的容器
   2. limit type:integer
   返回此数量的最近创建的容器，包括未运行的容器。
   3. size type:boolean default:false
   返回容器的大小，字段名为SizeRW和SizeRootFs
   4. filters type:string
   对容器列表进行过滤，编码为json格式map[string][]string,例如{"status":["paused"]}将返回已经暂停的容器

    返回状态
    1. 200 no error
        响应模式
        ```json
        [
            {
            "Id": "8dfafdbc3a40",
            "Names": [
            "/boring_feynman"
            ],
            "Image": "ubuntu:latest",
            "ImageID": "d74508fb6632491cea586a1fd7d748dfc5274cd6fdfedee309ecdcbc2bf5cb82",
            "Command": "echo 1",
            "Created": 1367854155,
            "State": "Exited",
            "Status": "Exit 0",
            "Ports": [],
            "Labels": {},
            "SizeRw": 12288,
            "SizeRootFs": 0,
            "HostConfig": {},
            "NetworkSettings": {},
            "Mounts": []
            }
        ]
        ```
    1. 400 bad parameter(请求参数错误)
    2. 500 server
   
2. 创建容器
   POST /containers/create
    请求参数
    1. name type:string
        将指定的名称分配给容器。必须匹配/?[a-zA-Z0-9_-]+
    
    请求体
    1. hostname type:string 
        用于容器的主机名
    2. Domainname  type:string
        容器使用的域名。
    3. User type:string
        在容器内运行命令的用户。
    4. AttachStdin type:boolean default:false
        是否附加stdin
    5. AttachStdout type:boolean default:true
        是否附加stdout
    6. AttachStderr type:boolean default:true
        是否附加stderr
    7. exposedPorts 暴露端口
        暴露端口
        对象将端口映射为以下形式的空对象：
        {"<port>/<tcp|udp|sctp>": {}}
        1. <Additional Properties> * = {} type:object defualt:{}
    ...

    ```json
    {
        "Hostname": "",
        "Domainname": "",
        "User": "",
        "AttachStdin": false,
        "AttachStdout": true,
        "AttachStderr": true,
        "Tty": false,
        "OpenStdin": false,
        "StdinOnce": false,
        "Env": [
        "FOO=bar",
        "BAZ=quux"
        ],
        "Cmd": [
        "date"
        ],
        "Entrypoint": "",
        "Image": "ubuntu",
        "Labels": {
        "com.example.vendor": "Acme",
        "com.example.license": "GPL",
        "com.example.version": "1.0"
        },
        "Volumes": {
        "/volumes/data": { }
        },
        "WorkingDir": "",
        "NetworkDisabled": false,
        "MacAddress": "12:34:56:78:9a:bc",
        "ExposedPorts": {
        "22/tcp": { }
        },
        "StopSignal": "SIGTERM",
        "StopTimeout": 10,
        "HostConfig": {
        "Binds": [],
        "Links": [],
        "Memory": 0,
        "MemorySwap": 0,
        "MemoryReservation": 0,
        "KernelMemory": 0,
        "NanoCPUs": 500000,
        "CpuPercent": 80,
        "CpuShares": 512,
        "CpuPeriod": 100000,
        "CpuRealtimePeriod": 1000000,
        "CpuRealtimeRuntime": 10000,
        "CpuQuota": 50000,
        "CpusetCpus": "0,1",
        "CpusetMems": "0,1",
        "MaximumIOps": 0,
        "MaximumIOBps": 0,
        "BlkioWeight": 300,
        "BlkioWeightDevice": [],
        "BlkioDeviceReadBps": [],
        "BlkioDeviceReadIOps": [],
        "BlkioDeviceWriteBps": [],
        "BlkioDeviceWriteIOps": [],
        "MemorySwappiness": 60,
        "OomKillDisable": false,
        "OomScoreAdj": 500,
        "PidMode": "",
        "PidsLimit": -1,
        "PortBindings": {},
        "PublishAllPorts": false,
        "Privileged": false,
        "ReadonlyRootfs": false,
        "Dns": [],
        "DnsOptions": [],
        "DnsSearch": [],
        "VolumesFrom": [],
        "CapAdd": [],
        "CapDrop": [],
        "GroupAdd": [],
        "RestartPolicy": {},
        "AutoRemove": true,
        "NetworkMode": "bridge",
        "Devices": [ ],
        "Ulimits": [],
        "LogConfig": {},
        "SecurityOpt": [ ],
        "StorageOpt": { },
        "CgroupParent": "",
        "VolumeDriver": "",
        "ShmSize": 67108864
        },
        "NetworkingConfig": {
        "EndpointsConfig": {}
        }
    }
    ```

    返回体
    1. 201 容器创建成功
    2. 400 请求参数错误
    3. 404 没有找到对应的容器
    4. 409 冲突
    5. 500 服务器错误

   