## 术语

- **节点：** Kubernetes 集群中的服务器
- **集群：** Kubernetes 管理的一组服务器集合
- **边界路由器：** 为局域网和 Internet 路由数据包的路由器，执行防火墙保护局域网络
- **集群网络：** 遵循 Kubernetes 网络模型实现集群内的通信的具体实现，比如 Flannel 和 Calico
- **服务：** Kubernetes 的服务 (Service) 是使用标签选择器标识的一组 Pod Service (Deployment)。 **除非另有说明，否则服务的虚拟 IP 仅可在集群内部访问**

## [#](https://www.funtl.com/zh/service-mesh-kubernetes/Ingress-统一访问入口.html#内部访问方式-clusterip)内部访问方式 ClusterIP

ClusterIP 服务是 Kubernetes 的默认服务。它给你一个集群内的服务，集群内的其它应用都可以访问该服务。集群外部无法访问它。在某些场景下我们可以使用 Kubernetes 的 Proxy 模式来访问服务，比如调试服务时。

![img](https://www.funtl.com/assets1/Lusifer_2019060601200001.png)

## [#](https://www.funtl.com/zh/service-mesh-kubernetes/Ingress-统一访问入口.html#三种外部访问方式)三种外部访问方式

### [#](https://www.funtl.com/zh/service-mesh-kubernetes/Ingress-统一访问入口.html#nodeport)NodePort

NodePort 服务是引导外部流量到你的服务的最原始方式。NodePort，正如这个名字所示，**在所有节点（虚拟机）上开放一个特定端口**，任何发送到该端口的流量都被转发到对应服务。

NodePort 服务特征如下：

- 每个端口只能是一种服务
- 端口范围只能是 30000-32767（可调）
- 不在 YAML 配置文件中指定则会分配一个默认端口

> **建议：** 不要在生产环境中使用这种方式暴露服务，大多数时候我们应该让 Kubernetes 来选择端口

![img](https://www.funtl.com/assets1/Lusifer_2019060601200002.png)

### [#](https://www.funtl.com/zh/service-mesh-kubernetes/Ingress-统一访问入口.html#loadbalancer)LoadBalancer

LoadBalancer 服务是暴露服务到 Internet 的标准方式。所有通往你指定的端口的流量都会被转发到对应的服务。它没有过滤条件，没有路由等。这意味着你几乎可以发送任何种类的流量到该服务，像 HTTP，TCP，UDP，WebSocket，gRPC 或其它任意种类。

![img](https://www.funtl.com/assets1/Lusifer_2019060601200003.png)

### [#](https://www.funtl.com/zh/service-mesh-kubernetes/Ingress-统一访问入口.html#ingress)Ingress

Ingress 事实上不是一种服务类型。相反，它处于多个服务的前端，扮演着 “智能路由” 或者集群入口的角色。你可以用 Ingress 来做许多不同的事情，各种不同类型的 Ingress 控制器也有不同的能力。它允许你基于路径或者子域名来路由流量到后端服务。

Ingress 可能是暴露服务的最强大方式，但同时也是最复杂的。Ingress 控制器有各种类型，包括 Google Cloud Load Balancer， Nginx，Contour，Istio，等等。它还有各种插件，比如 cert-manager (它可以为你的服务自动提供 SSL 证书)/

如果你想要使用同一个 IP 暴露多个服务，这些服务都是使用相同的七层协议（典型如 HTTP），你还可以获取各种开箱即用的特性（比如 SSL、认证、路由等等）

![img](https://www.funtl.com/assets1/Lusifer_2019060601200004.png)

## [#](https://www.funtl.com/zh/service-mesh-kubernetes/Ingress-统一访问入口.html#什么是-ingress)什么是 Ingress

通常情况下，Service 和 Pod 的 IP 仅可在集群内部访问。集群外部的请求需要通过负载均衡转发到 Service 在 Node 上暴露的 NodePort 上，然后再由 kube-proxy 通过边缘路由器 (edge router) 将其转发给相关的 Pod 或者丢弃。而 Ingress 就是为进入集群的请求提供路由规则的集合

Ingress 可以给 Service 提供集群外部访问的 URL、负载均衡、SSL 终止、HTTP 路由等。为了配置这些 Ingress 规则，集群管理员需要部署一个 Ingress Controller，它监听 Ingress 和 Service 的变化，并根据规则配置负载均衡并提供访问入口。

## [#](https://www.funtl.com/zh/service-mesh-kubernetes/Ingress-统一访问入口.html#使用-nginx-ingress-controller)使用 Nginx Ingress Controller

本次实践的主要目的就是将入口统一，不再通过 LoadBalancer 等方式将端口暴露出来，而是使用 Ingress 提供的反向代理负载均衡功能作为我们的唯一入口。通过以下步骤操作仔细体会。

> **注意：** 下面包含资源配置的步骤都是自行创建 YAML 配置文件通过 `kubectl create -f ` 和 `kubectl delete -f ` 部署和删除

### [#](https://www.funtl.com/zh/service-mesh-kubernetes/Ingress-统一访问入口.html#部署-tomcat)部署 Tomcat

部署 Tomcat 但仅允许在内网访问，我们要通过 Ingress 提供的反向代理功能路由到 Tomcat 之上

```yaml
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  name: tomcat-app
spec:
  replicas: 2
  template:
    metadata:
      labels:
        name: tomcat
    spec:
      containers:
      - name: tomcat
        image: tomcat
        ports:
        - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: tomcat-http
spec:
  ports:
    - port: 8080
      targetPort: 8080
  # ClusterIP, NodePort, LoadBalancer
  type: LoadBalancer
  selector:
    name: tomcat
```

### 安装 Nginx Ingress Controller

Ingress Controller 有许多种，我们选择最熟悉的 Nginx 来处理请求，其它可以参考 [官方文档](https://kubernetes.io/docs/concepts/services-networking/ingress-controllers/)

- 下载 Nginx Ingress Controller 配置文件

```bash
wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/static/mandatory.yaml
```

修改配置文件，找到配置如下位置 (搜索 `serviceAccountName`) 在下面增加一句 `hostNetwork: true`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-ingress-controller
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
spec:
  # 可以部署多个实例
  replicas: 1
  selector:
    matchLabels:
      app.kubernetes.io/name: ingress-nginx
      app.kubernetes.io/part-of: ingress-nginx
  template:
    metadata:
      labels:
        app.kubernetes.io/name: ingress-nginx
        app.kubernetes.io/part-of: ingress-nginx
      annotations:
        prometheus.io/port: "10254"
        prometheus.io/scrape: "true"
    spec:
      serviceAccountName: nginx-ingress-serviceaccount
      # 增加 hostNetwork: true，意思是开启主机网络模式，暴露 Nginx 服务端口 80
      hostNetwork: true
      containers:
        - name: nginx-ingress-controller
          image: quay.io/kubernetes-ingress-controller/nginx-ingress-controller:0.24.1
          args:
            - /nginx-ingress-controller
            - --configmap=$(POD_NAMESPACE)/nginx-configuration
            - --tcp-services-configmap=$(POD_NAMESPACE)/tcp-services
            - --udp-services-configmap=$(POD_NAMESPACE)/udp-services
            - --publish-service=$(POD_NAMESPACE)/ingress-nginx
// 以下代码省略...
```

### 部署 Ingress

Ingress 翻译过来是入口的意思，说白了就是个 API 网关（想想之前学的 Zuul 和 Spring Cloud Gateway）

```yaml
apiVersion: networking.k8s.io/v1beta1
kind: Ingress
metadata:
  name: nginx-web
  annotations:
    # 指定 Ingress Controller 的类型
    kubernetes.io/ingress.class: "nginx"
    # 指定我们的 rules 的 path 可以使用正则表达式
    nginx.ingress.kubernetes.io/use-regex: "true"
    # 连接超时时间，默认为 5s
    nginx.ingress.kubernetes.io/proxy-connect-timeout: "600"
    # 后端服务器回转数据超时时间，默认为 60s
    nginx.ingress.kubernetes.io/proxy-send-timeout: "600"
    # 后端服务器响应超时时间，默认为 60s
    nginx.ingress.kubernetes.io/proxy-read-timeout: "600"
    # 客户端上传文件，最大大小，默认为 20m
    nginx.ingress.kubernetes.io/proxy-body-size: "10m"
    # URL 重写
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  # 路由规则
  rules:
  # 主机名，只能是域名，修改为你自己的
  - host: k8s.test.com
    http:
      paths:
      - path:
        backend:
          # 后台部署的 Service Name，与上面部署的 Tomcat 对应
          serviceName: tomcat-http
          # 后台部署的 Service Port，与上面部署的 Tomcat 对应
          servicePort: 8080
```