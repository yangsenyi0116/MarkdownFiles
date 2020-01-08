<<<<<<< HEAD
```txt
打开/work/example.ipynb；

if("$work$example.kernel"文件不存在){
    创建新内核()；
    response=req.get("/api/session");
    创建文件"$work$example.kernel"；
    token=该设备上用户token
    kernel_id=response.kernel_id;
    将token和kernel_id写入"$work$example.kernel"；
}else{
    读取"$work$example.kernel";
    if(当前用户token!=读取文件内容的token){
		//该文件不属于该用户
		throw new Exception();
	}
	
	thisFile_kernel=读取文件内容中的kernel_id;
	try{
        res=openWebSocketConnect(url="gateway网关地址/api/kernel/${kernel_id}")；
	}
	
	if(res.code==201){
        //连接内核成功
        return message;
	}else{
        //连接内核失败，内核已经停止或者故障，重新启动一个内核
        response=req.get("/api/session");
        //将新的kernel_id写入到文件中
        kernel_id=response.kernel_id;
        将kernel_id写入"$work$example.kernel"
	}
}
```

```txt
# $work$example.kernel
user_token: ****************************
kernel_id: **************************
```

笔记本在连接内核时会先向网关发送请求/api/session

得到可用的kernel_id

接着发送请求ws://gateway_url:port/api/kernel/{kernel_id}?sessionId={sessionid}来将会话升级为websoket连接，来连接上内核



在jupyterlab中证实了这一观点

=======
```txt
打开/work/example.ipynb；

if("$work$example.kernel"文件不存在){
    创建新内核()；
    response=req.get("/api/session");
    创建文件"$work$example.kernel"；
    token=该设备上用户token
    kernel_id=response.kernel_id;
    将token和kernel_id写入"$work$example.kernel"；
}else{
    读取"$work$example.kernel";
    if(当前用户token!=读取文件内容的token){
		//该文件不属于该用户
		throw new Exception();
	}
	
	thisFile_kernel=读取文件内容中的kernel_id;
	try{
        res=openWebSocketConnect(url="gateway网关地址/api/kernel/${kernel_id}")；
	}
	
	if(res.code==201){
        //连接内核成功
        return message;
	}else{
        //连接内核失败，内核已经停止或者故障，重新启动一个内核
        response=req.get("/api/session");
        //将新的kernel_id写入到文件中
        kernel_id=response.kernel_id;
        将kernel_id写入"$work$example.kernel"
	}
}
```

```txt
# $work$example.kernel
user_token: ****************************
kernel_id: **************************
```

笔记本在连接内核时会先向网关发送请求/api/session

得到可用的kernel_id

接着发送请求ws://gateway_url:port/api/kernel/{kernel_id}?sessionId={sessionid}来将会话升级为websoket连接，来连接上内核



在jupyterlab中证实了这一观点

>>>>>>> c9bee07ba66fa28d4cb96ff27849f139a10e20d9
绝对路径的文件名不会重复，在内核检测的到心跳连接的时候内核不会停止，即使笔记本关闭