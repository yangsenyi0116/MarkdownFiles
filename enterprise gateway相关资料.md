### On-demand Notebooks with JupyterHub, Jupyter Enterprise Gateway and Kubernetes

https://blog.jupyter.org/on-demand-notebooks-with-jupyterhub-jupyter-enterprise-gateway-and-kubernetes-e8e423695cbf



![img](https://miro.medium.com/max/2116/1*F_jJ1nDSQgBhrkbsEXB93Q.png)

#### [Jupyter Enterprise Gateway](https://jupyter-enterprise-gateway.readthedocs.io/en/latest/)通过使内核能够在其自己的Pod中启动来提供最佳的资源分配，从而使笔记本Pod具有最少的资源，而内核特定的资源则根据其生命周期进行分配/取消分配。它还使内核的基础映像成为一种选择。



Jupyter Enterprise Gateway使Jupyter Notebook能够启动和管理分布式集群（包括Kubernetes集群）中的远程内核。

Enterprise Gateway提供了一个Kubernetes部署描述符，使用以下命令可以很容易地将其部署到Kubernetes环境中：

```
kubectl create -f https://raw.githubusercontent.com/jupyter-incubator/enterprise_gateway/master/etc/kubernetes/enterprise-gateway.yaml
```

我们还建议您在Kubernetes集群的所有节点上下载内核映像，以避免在这些节点上首次启动内核时的延迟/超时。



![img](https://pic2.zhimg.com/v2-9b2eb36a2fbef8970cb2f7aa2dd03fa1_b.jpg)



[![部署图](https://github.com/jupyter/enterprise_gateway/raw/master/docs/source/images/deployment.png?raw=true)](https://github.com/jupyter/enterprise_gateway/blob/master/docs/source/images/deployment.png?raw=true)





kernel如何连接至notebook的

https://huadeyu.tech/tools/jupyter-remote-kernel.html

![notebook_components.png](https://jupyter.readthedocs.io/en/latest/_images/notebook_components.png)

Notebook与Kernel的交互是通过ZMQ进行

官方的Jupyter Notebook格式是使用[此JSON模式](https://github.com/jupyter/nbformat/blob/master/nbformat/v4/nbformat.v4.schema.json)定义的， Jupyter工具使用它来验证笔记本





## 通用报文格式

消息由以下四个字典结构定义：

```
{
  # The message header contains a pair of unique identifiers for the
  # originating session and the actual message id, in addition to the
  # username for the process that generated the message.  This is useful in
  # collaborative settings where multiple users may be interacting with the
  # same kernel simultaneously, so that frontends can label the various
  # messages in a meaningful way.
  'header' : {
                'msg_id' : str, # typically UUID, must be unique per message
                'username' : str,
                'session' : str, # typically UUID, should be unique per session
                # ISO 8601 timestamp for when the message is created
                'date': str,
                # All recognized message type strings are listed below.
                'msg_type' : str,
                # the message protocol version
                'version' : '5.0',
     },

  # In a chain of messages, the header from the parent is copied so that
  # clients can track where messages come from.
  'parent_header' : dict,

  # Any metadata associated with the message.
  'metadata' : dict,

  # The actual content of the message must be a dict, whose structure
  # depends on the message type.
  'content' : dict,

  # optional: buffers is a list of binary data buffers for implementations
  # that support binary extensions to the protocol.
  'buffers': list,
}
```

`session`消息头中的id标识具有状态的唯一实体，例如内核进程或客户端进程。

来自客户端的消息标头中的客户端会话ID在连接到内核的所有客户端中应该是唯一的。当客户端重新连接到内核时，它应该在其消息头中使用相同的客户端会话ID。客户端重新启动时，它应生成一个新的客户端会话ID。

在来自内核的消息标头中的内核会话标识应标识特定的内核进程。如果重新启动内核，则应重新生成内核会话ID。

消息头中的会话ID可用于标识发送实体。例如，如果客户端断开连接并重新连接到内核，并且来自内核的消息具有与断开连接之前不同的内核会话ID，则客户端应假定内核已重新启动。



能实现pip install 包的定位保存，前提是kernel没有关闭，连接到同一kernel中

问题：抓取环境时，不知道kernel在哪台机器中

```yaml
# This file defines the Kubernetes objects necessary for Enterprise Gateway kernels to run witihin Kubernetes.
# Substitution parameters are processed by the launch_kubernetes.py code located in the
# same directory.  Some values are factory values, while others (typically prefixed with 'kernel_') can be
# provided by the client.
#
# This file can be customized as needed.  No changes are required to launch_kubernetes.py provided kernel_
# values are used - which be automatically set from corresponding KERNEL_ env values.  Updates will be required
# to launch_kubernetes.py if new document sections (i.e., new k8s 'kind' objects) are introduced.
#
apiVersion: v1
kind: Pod
metadata:
  name: "{{ kernel_pod_name }}"
  namespace: "{{ kernel_namespace }}"
  labels:
    kernel_id: "{{ kernel_id }}"
    app: enterprise-gateway
    component: kernel
spec:
  restartPolicy: Never
  serviceAccountName: "{{ kernel_service_account_name }}"
# NOTE: that using runAsGroup requires that feature-gate RunAsGroup be enabled.
# WARNING: Only using runAsUser w/o runAsGroup or NOT enabling the RunAsGroup feature-gate
# will result in the new kernel pod's effective group of 0 (root)! although the user will
# correspond to the runAsUser value.  As a result, BOTH should be uncommented AND the feature-gate
# should be enabled to ensure expected behavior.  In addition, 'fsGroup: 100' is recommended so
# that /home/jovyan can be written to via the 'users' group (gid: 100) irrespective of the
# "kernel_uid" and "kernel_gid" values.
  {% if kernel_uid is defined or kernel_gid is defined %}
  securityContext:
    {% if kernel_uid is defined %}
    runAsUser: {{ kernel_uid | int }}
    {% endif %}
    {% if kernel_gid is defined %}
    runAsGroup: {{ kernel_gid | int }}
    {% endif %}
    fsGroup: 100
  {% endif %}
  containers:
  - env:
    - name: EG_RESPONSE_ADDRESS
      value: "{{ eg_response_address }}"
    - name: KERNEL_LANGUAGE
      value: "{{ kernel_language }}"
    - name: KERNEL_SPARK_CONTEXT_INIT_MODE
      value: "{{ kernel_spark_context_init_mode }}"
    - name: KERNEL_NAME
      value: "{{ kernel_name }}"
    - name: KERNEL_USERNAME
      value: "{{ kernel_username }}"
    - name: KERNEL_ID
      value: "{{ kernel_id }}"
    - name: KERNEL_NAMESPACE
      value: "{{ kernel_namespace }}"
    image: "{{ kernel_image }}"
    name: "{{ kernel_pod_name }}"
    {% if kernel_working_dir is defined %}
    workingDir: "{{ kernel_working_dir }}"
    {% endif %}
    volumeMounts:
# Define any "unconditional" mounts here, followed by "conditional" mounts that vary per client
    {% if kernel_volume_mounts is defined %}
      {% for volume_mount in kernel_volume_mounts %}
    - {{ volume_mount }}
      {% endfor %}
    {% endif %}
  volumes:
# Define any "unconditional" volumes here, followed by "conditional" volumes that vary per client
  {% if kernel_volumes is defined %}
    {% for volume in kernel_volumes %}
  - {{ volume }}
    {% endfor %}
  {% endif %}
```



此消息格式存在于较高级别，但未在zeromq中描述有线级别的实际*实现*。消息规范的规范实现是我们的`Session`类。

注意

本部分仅与协议的非Python使用者有关。Python使用者应该简单地在中导入和使用Wire协议的实现`jupyter_client.session.Session`。

https://jupyter-client.readthedocs.io/en/latest/messaging.html



本笔记本描述了如何将另一个前端连接到与笔记本关联的IPython内核。当前此处给出的命令特定于IPython内核。

内核和前端通过基于ZeroMQ / JSON的消息传递协议进行通信，该协议允许多个前端（甚至不同类型）与单个内核进行通信。

[https://jupyter-notebook.readthedocs.io/en/latest/examples/Notebook/Connecting%20with%20the%20Qt%20Console.html#Manual-connection](https://jupyter-notebook.readthedocs.io/en/latest/examples/Notebook/Connecting with the Qt Console.html#Manual-connection)





![image-20200107102728787](C:\Users\xxrib\AppData\Roaming\Typora\typora-user-images\image-20200107102728787.png)

每次刷新jupyter notebook页面都会发送一个请求

![image-20200107105123130](C:\Users\xxrib\AppData\Roaming\Typora\typora-user-images\image-20200107105123130.png)

来将当前连接升级成websocket连接

![image-20200107105224202](C:\Users\xxrib\AppData\Roaming\Typora\typora-user-images\image-20200107105224202.png)

![image-20200107105235338](C:\Users\xxrib\AppData\Roaming\Typora\typora-user-images\image-20200107105235338.png)



只要将请求的/api/kernels/${kernel_id}统一即可以连接到同一内核

如何让其他记事本连接到同一内核下？

![image-20200107114851484](C:\Users\xxrib\AppData\Roaming\Typora\typora-user-images\image-20200107114851484.png)