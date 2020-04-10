# JupyterHub

https://jupyterhub.readthedocs.io/en/stable/index.html

> [JupyterHub](https://github.com/jupyterhub/jupyterhub)是为多个用户提供[Jupyter笔记本电脑](https://jupyter-notebook.readthedocs.io/en/latest/)的最佳方法。它可以用于一类学生，公司数据科学小组或科学研究小组。它是一个多用户**集线器**，可生成，管理和代理单用户[Jupyter笔记本](https://jupyter-notebook.readthedocs.io/en/latest/)服务器的多个实例。

2种使用情况

1. 如果您需要少量用户（0-100）和单个服务器的简单案例，请查看 [Littlest JupyterHub](https://github.com/jupyterhub/the-littlest-jupyterhub)发行版。
2. 如果您需要允许更多的用户，则可以在云上使用动态数量的服务器，请看一下[使用Kubernetes](https://github.com/jupyterhub/zero-to-jupyterhub-k8s)的[零至JupyterHub](https://github.com/jupyterhub/zero-to-jupyterhub-k8s)。



JupyterHub由四个子系统组成：

- JupyterHub 的**中心枢纽**（龙卷风过程）
- 一个可**配置的http代理**（node-http-proxy），该代理从客户端的浏览器接收请求
- **Spawners监视的**多个**单用户Jupyter笔记本服务器**（Python / IPython / tornado）
- 管理用户如何访问系统的**身份验证类**



[![JupyterHub子系统](../images/jhub-fluxogram-1578457271465.jpeg)](https://jupyterhub.readthedocs.io/en/latest/_images/jhub-fluxogram.jpeg)





JupyterHub执行以下功能：

- 集线器启动代理
- 代理默认将所有请求转发到集线器
- 集线器处理用户登录并按需生成单用户服务器
- 集线器将代理配置为将URL前缀转发到单用户笔记本服务器



## 启动中心服务器

要启动集线器服务器，请运行以下命令：

```
jupyterhub
```

`https://localhost:8000`在浏览器中访问，并使用`UNIX凭据登录`。

要**允许多个用户登录**集线器服务器，必须 `jupyterhub`以*特权用户*（例如root）启动：

```
sudo jupyterhub
```



## 启动JupyterHub与docker

可以使用以下命令启动JupyterHub docker映像：

```
docker run -d -p 8000:8000 --name jupyterhub jupyterhub/jupyterhub jupyterhub
```

此命令将创建一个名为容器`jupyterhub`，你可以 **停止和恢复**使用。`docker stop/start`

集线器服务将监听端口8000上的所有接口，这使其成为**在台式机或笔记本电脑上测试JupyterHub**的不错选择。

如果要在具有公共IP的计算机上运行docker，则应（如在MUST中一样）通过在docker配置中添加ssl选项或使用启用了ssl的代理来使用ssl **保护它**。

[挂载卷](https://docs.docker.com/engine/admin/volumes/volumes/) 将使您能够将数据存储在docker映像（主机系统）之外，因此即使您启动新映像也将保持持久性。

该命令将在您的docker容器中生成一个根shell。您可以使用root shell **在容器中创建系统用户**。这些帐户将在JupyterHub的默认配置中用于身份验证。`docker exec -it jupyterhub bash`