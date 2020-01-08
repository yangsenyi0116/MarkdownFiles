## 下载版本

下载Istio发行版，其中包括安装文件，示例和 [istioctl](https://istio.io/docs/reference/commands/istioctl/)命令行实用程序。

1. 进入[Istio发布](https://github.com/istio/istio/releases/tag/1.4.2)页面，下载与您的操作系统相对应的安装文件。或者，在macOS或Linux系统上，您可以运行以下命令来自动下载并解压缩最新版本：

   ```bash
   $ curl -L https://istio.io/downloadIstio | sh -
   ```

   

2. 移至Istio软件包目录。例如，如果软件包为 `istio-1.4.2`：

   ```bash
   $ cd istio-1.4.2
   ```

   

   安装目录包含：

   - Kubernetes的安装YAML文件在 `install/kubernetes`
   - 中的示例应用程序 `samples/`
   - 目录中的客户端二进制文件。手动注入Envoy作为Sidecar代理时使用。[`istioctl`](https://istio.io/docs/reference/commands/istioctl)`bin/``istioctl`

3. `istioctl`在macOS或Linux系统上，将客户端添加到您的路径中：

   ```bash
   $ export PATH=$PWD/bin:$PATH
   ```

   

4. 使用bash或ZSH控制台时，可以选择启用[自动完成选项](https://istio.io/docs/ops/diagnostic-tools/istioctl#enabling-auto-completion)。



## 安装Istio

这些说明假定您是Istio的新手，它提供了简化的说明来安装Istio的内置`demo` [配置文件](https://istio.io/docs/setup/additional-setup/config-profiles/)。通过此安装，您可以快速开始评估Istio。如果您已经熟悉Istio或对安装其他配置配置文件或更高级的[部署模型](https://istio.io/docs/ops/deployment/deployment-models/)感兴趣，请按照[istioctl的说明](https://istio.io/docs/setup/install/istioctl)进行[安装](https://istio.io/docs/setup/install/istioctl)。



演示配置概要文件不适用于性能评估。它旨在通过高级别的跟踪和访问日志来展示Istio功能。

1. 安装`demo`配置文件

   ```
   $ istioctl manifest apply --set profile=demo
   ```

   

2. 通过确保已部署以下Kubernetes服务来验证安装，并确认`CLUSTER-IP`除了`jaeger-agent`服务之外，它们均具有合适的服务：

   ```bash
   $ kubectl get svc -n istio-system
   NAME                     TYPE           CLUSTER-IP       EXTERNAL-IP     PORT(S)                                                                                                                                   AGE
   grafana                  ClusterIP      172.21.211.123   <none>          3000/TCP                                                                                                                                     2m
   istio-citadel            ClusterIP      172.21.177.222   <none>          8060/TCP,15014/TCP                                                                                                                           2m
   istio-egressgateway      ClusterIP      172.21.113.24    <none>          80/TCP,443/TCP,15443/TCP                                                                                                                     2m
   istio-galley             ClusterIP      172.21.132.247   <none>          443/TCP,15014/TCP,9901/TCP                                                                                                                   2m
   istio-ingressgateway     LoadBalancer   172.21.144.254   52.116.22.242   15020:31831/TCP,80:31380/TCP,443:31390/TCP,31400:31400/TCP,15029:30318/TCP,15030:32645/TCP,15031:31933/TCP,15032:31188/TCP,15443:30838/TCP   2m
   istio-pilot              ClusterIP      172.21.105.205   <none>          15010/TCP,15011/TCP,8080/TCP,15014/TCP                                                                                                       2m
   istio-policy             ClusterIP      172.21.14.236    <none>          9091/TCP,15004/TCP,15014/TCP                                                                                                                 2m
   istio-sidecar-injector   ClusterIP      172.21.155.47    <none>          443/TCP,15014/TCP                                                                                                                            2m
   istio-telemetry          ClusterIP      172.21.196.79    <none>          9091/TCP,15004/TCP,15014/TCP,42422/TCP                                                                                                       2m
   jaeger-agent             ClusterIP      None             <none>          5775/UDP,6831/UDP,6832/UDP                                                                                                                   2m
   jaeger-collector         ClusterIP      172.21.135.51    <none>          14267/TCP,14268/TCP                                                                                                                          2m
   jaeger-query             ClusterIP      172.21.26.187    <none>          16686/TCP                                                                                                                                    2m
   kiali                    ClusterIP      172.21.155.201   <none>          20001/TCP                                                                                                                                    2m
   prometheus               ClusterIP      172.21.63.159    <none>          9090/TCP                                                                                                                                     2m
   tracing                  ClusterIP      172.21.2.245     <none>          80/TCP                                                                                                                                       2m
   zipkin                   ClusterIP      172.21.182.245   <none>          9411/TCP                                                                                                                                     2m
   ```

   

   

   如果您的群集在不支持外部负载均衡器（例如minikube）的环境中运行，则 `EXTERNAL-IP`of `istio-ingressgateway`将显示 ``。要访问网关，请使用服务的 `NodePort`，或使用端口转发。

   此外，还要确保相应Kubernetes荚部署，并有一个`STATUS`的`Running`：

   ```bash
   $ kubectl get pods -n istio-system
   NAME                                                           READY   STATUS      RESTARTS   AGE
   grafana-f8467cc6-rbjlg                                         1/1     Running     0          1m
   istio-citadel-78df5b548f-g5cpw                                 1/1     Running     0          1m
   istio-egressgateway-78569df5c4-zwtb5                           1/1     Running     0          1m
   istio-galley-74d5f764fc-q7nrk                                  1/1     Running     0          1m
   istio-ingressgateway-7ddcfd665c-dmtqz                          1/1     Running     0          1m
   istio-pilot-f479bbf5c-qwr28                                    1/1     Running     0          1m
   istio-policy-6fccc5c868-xhblv                                  1/1     Running     2          1m
   istio-sidecar-injector-78499d85b8-x44m6                        1/1     Running     0          1m
   istio-telemetry-78b96c6cb6-ldm9q                               1/1     Running     2          1m
   istio-tracing-69b5f778b7-s2zvw                                 1/1     Running     0          1m
   kiali-99f7467dc-6rvwp                                          1/1     Running     0          1m
   prometheus-67cdb66cbb-9w2hm                                    1/1     Running     0          1m
   ```

## 下一步

安装Istio后，您现在可以部署自己的应用程序或安装随附的示例应用程序之一。



该应用程序必须对所有HTTP通信使用HTTP / 1.1或HTTP / 2.0协议。不支持HTTP / 1.0。

如果使用部署应用程序`kubectl apply`，则[Istio边车注入器](https://istio.io/docs/setup/additional-setup/sidecar-injection/#automatic-sidecar-injection) 会自动将Envoy容器注入到您的应用程序容器中，如果它们是在标有的名称空间中启动的`istio-injection=enabled`：

```
$ kubectl label namespace <namespace> istio-injection=enabled
$ kubectl create -n <namespace> -f <your-app-spec>.yaml
```



在没有`istio-injection`标签的名称空间中，您可以 [`istioctl kube-inject`](https://istio.io/docs/reference/commands/istioctl/#istioctl-kube-inject) 在部署它们之前在应用程序pod中手动注入Envoy容器：

```
$ istioctl kube-inject -f <your-app-spec>.yaml | kubectl apply -f -
```



如果不确定从哪里开始，请 [部署Bookinfo示例](https://istio.io/docs/examples/bookinfo/) ，[该示例](https://istio.io/docs/examples/bookinfo/)将允许您评估Istio的功能，如流量路由，故障注入，速率限制等。然后探索您感兴趣的各种[Istio任务](https://istio.io/docs/tasks/)。

以下任务是初学者入门的好地方：

- [请求路由](https://istio.io/docs/tasks/traffic-management/request-routing/)
- [故障注入](https://istio.io/docs/tasks/traffic-management/fault-injection/)
- [交通转移](https://istio.io/docs/tasks/traffic-management/traffic-shifting/)
- [查询指标](https://istio.io/docs/tasks/observability/metrics/querying-metrics/)
- [可视化指标](https://istio.io/docs/tasks/observability/metrics/using-istio-dashboard/)
- [收集日志](https://istio.io/docs/tasks/observability/logs/collecting-logs/)
- [限速](https://istio.io/docs/tasks/policy-enforcement/rate-limiting/)
- [入口网关](https://istio.io/docs/tasks/traffic-management/ingress/ingress-control/)
- [访问外部服务](https://istio.io/docs/tasks/traffic-management/egress/egress-control/)
- [可视化您的网格](https://istio.io/docs/tasks/observability/kiali/)

下一步是自定义Istio并部署您自己的应用程序。在安装和自定义Istio以使其适合您的平台和预期用途之前，请检查以下资源：

- [部署模型](https://istio.io/docs/ops/deployment/deployment-models/)
- [部署最佳实践](https://istio.io/docs/ops/best-practices/deployment/)
- [吊舱要求](https://istio.io/docs/ops/deployment/requirements/)
- [一般安装说明](https://istio.io/docs/setup/)

## 卸载

卸载会删除RBAC权限，`istio-system`名称空间以及它下面的层次结构中的所有资源。可以忽略不存在的资源的错误，因为它们可能已被分层删除。

```
$ istioctl manifest generate --set profile=demo | kubectl delete -f -
```