# Kubectl 常用命令

> **小提示：** 所有命令前都可以加上 `watch` 命令来观察状态的实时变化，如：`watch kubectl get pods --all-namespaces`

## 查看组件状态

```bash
kubectl get cs
```

## 查看环境信息

```bash
kubectl cluster-info
```

## 查看 Node

```bash
kubectl get nodes -o wide
```

## 查看集群配置

```bash
kubectl -n kube-system get cm kubeadm-config -oyaml
```

## 运行容器

```bash
kubectl run nginx --image=nginx --replicas=2 --port=80
```

## 暴露服务

```bash
kubectl expose deployment nginx --port=80 --type=LoadBalancer
```

## 查看命名空间

```bash
kubectl get namespace
```

## 创建命名空间

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: development
```

## 查看容器

```bash
kubectl get pods -o wide
kubectl get deployment -o wide
```

## 查看服务

```bash
kubectl get service -o wide
```

## 查看详情

```bash
kubectl describe pod <Pod Name>
kubectl describe deployment <Deployment Name>
kubectl describe service <Service Name>
```

## 查看日志

```bash
kubectl logs -f <Pod Name>
```

## 删除容器和服务

```bash
kubectl delete deployment <Deployment Name>
kubectl delete service <Service Name>
```

## 配置方式运行

```bash
kubectl create -f <YAML>
```

## 配置方式删除

```bash
kubectl delete -f <YAML>
```

## 查看配置

```bash
kubeadm config view
kubectl config view
```

## 查看 Ingress

```bash
kubectl get ingress
```

## 查看持久卷

```bash
kubectl get pv
```

## 查看持久卷消费者

```bash
kubectl get pvc
```

## 查看 ConfigMap

```bash
kubectl get cm <ConfigMap Name>
```

## 修改 ConfigMap

```bash
kubectl edit cm <ConfigMap Name>
```