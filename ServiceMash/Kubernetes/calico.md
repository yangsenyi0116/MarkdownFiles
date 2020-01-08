Kubernetes 中可选的 CNI 插件如下：

- Flannel
- Calico
- Canal
- Weave

### 什么时Calico

> Calico 为容器和虚拟机提供了安全的网络连接解决方案，并经过了大规模生产验证（在公有云和跨数千个集群节点中），可与 Kubernetes，OpenShift，Docker，Mesos，DC / OS 和 OpenStack 集成。
>
> Calico 还提供网络安全规则的动态实施。使用 Calico 的简单策略语言，您可以实现对容器，虚拟机工作负载和裸机主机端点之间通信的细粒度控制

### 安装网络插件Calico

参考官方文档安装：https://docs.projectcalico.org/v3.7/getting-started/kubernetes/

```bash
# 在 Master 节点操作即可
kubectl apply -f https://docs.projectcalico.org/v3.7/manifests/calico.yaml
```

确认是否安装成功

```bash
watch kubectl get pods --all-namespaces
```