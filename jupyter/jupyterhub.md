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