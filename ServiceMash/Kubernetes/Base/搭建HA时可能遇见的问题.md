## Node无法加入

当我们使用 `kubeadm join` 命令将 Node 节点加入集群时，你会发现所有 `kubectl` 命令均不可用（呈现阻塞状态，并不会返回响应结果），我们可以在 Node 节点中通过 `kubeadm reset` 命令将 Node 节点下线，此时回到 Master 节点再使用 `watch kubectl get pods --all-namespaces` 可以看到下图中报错了，`coredns-xxx-xxx` 状态为 `CrashLoopBackOff`

### 解决方案

从上面的错误信息不难看出应该是出现了网络问题，而我们在安装过程中只使用了一个网络插件 **Calico** ，那么该错误是不是由 Calico 引起的呢？带着这个疑问我们去到 Calico 官网再看一下它的说明，官网地址：https://docs.projectcalico.org/v3.7/getting-started/kubernetes/

在它的 Quickstart 里有两段话（属于特别提醒），截图如下：

![img](assets/Lusifer_20190604013518.png)

上面这段话的主要意思是：当 `kubeadm` 安装完成后不要关机，继续完成后续的安装步骤；这也说明了安装 Kubernetes 的过程不要出现中断一口气搞定

![img](assets/Lusifer_20190604014207.png)

上面这段话的主要意思是：如果你的网络在 `192.168.0.0/16` 网段中，则必须选择一个不同的 Pod 网络；恰巧咱们的网络范围（我虚拟机的 IP 范围是 `192.168.141.0/24`）和该网段重叠 

遇到这个问题主要是因为虚拟机 IP 范围刚好和 Calico 默认网段重叠导致的

### 重置 Kubernetes

```bash
kubeadm reset
```

#### 删除 kubectl 配置

```bash
rm -fr ~/.kube/
```

#### 启用 IPVS

```bash
modprobe -- ip_vs
modprobe -- ip_vs_rr
modprobe -- ip_vs_wrr
modprobe -- ip_vs_sh
modprobe -- nf_conntrack_ipv4
```

#### 导出并修改配置文件

```bash
kubeadm config print init-defaults --kubeconfig ClusterConfiguration > kubeadm.yml
```

配置文件修改如下

```yaml
apiVersion: kubeadm.k8s.io/v1beta1
bootstrapTokens:
- groups:
  - system:bootstrappers:kubeadm:default-node-token
  token: abcdef.0123456789abcdef
  ttl: 24h0m0s
  usages:
  - signing
  - authentication
kind: InitConfiguration
localAPIEndpoint:
  advertiseAddress: 192.168.141.150
  bindPort: 6443
nodeRegistration:
  criSocket: /var/run/dockershim.sock
  name: kubernetes-master-01
  taints:
  - effect: NoSchedule
    key: node-role.kubernetes.io/master
---
apiServer:
  timeoutForControlPlane: 4m0s
apiVersion: kubeadm.k8s.io/v1beta1
certificatesDir: /etc/kubernetes/pki
clusterName: kubernetes
controlPlaneEndpoint: "192.168.141.200:6444"
controllerManager: {}
dns:
  type: CoreDNS
etcd:
  local:
    dataDir: /var/lib/etcd
imageRepository: registry.aliyuncs.com/google_containers
kind: ClusterConfiguration
kubernetesVersion: v1.14.2
networking:
  dnsDomain: cluster.local
  # 主要修改在这里，替换 Calico 网段为我们虚拟机不重叠的网段（这里用的是 Flannel 默认网段）
  podSubnet: "10.244.0.0/16"
  serviceSubnet: 10.96.0.0/12
scheduler: {}
---
apiVersion: kubeproxy.config.k8s.io/v1alpha1
kind: KubeProxyConfiguration
featureGates:
  SupportIPVSProxyMode: true
mode: ipvs
```

#### kubeadm 初始化

```bash
kubeadm init --config=kubeadm.yml --experimental-upload-certs | tee kubeadm-init.log
```

#### 配置 kubectl

```bash
# 配置 kubectl
mkdir -p $HOME/.kube
cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
chown $(id -u):$(id -g) $HOME/.kube/config

# 验证是否成功
kubectl get node
```

### 下载 Calico 配置文件并修改

```bash
wget https://docs.projectcalico.org/v3.7/manifests/calico.yaml
```

```bash
vi calico.yaml
```

修改第 611 行，将 `192.168.0.0/16` 修改为 `10.244.0.0/16`，可以通过如下命令快速查找

- 显示行号：`:set number`
- 查找字符：`/要查找的字符`，输入小写 `n` 下一个匹配项，输入大写 `N` 上一个匹配项

![img](assets/Lusifer_20190604022029.png)

#### 安装 Calico

```bash
kubectl apply -f calico.yaml
```

### 加入 Master 节点

```bash
# 示例如下，别忘记两个备用节点都要加入哦
kubeadm join 192.168.141.200:6444 --token abcdef.0123456789abcdef \
    --discovery-token-ca-cert-hash sha256:2ea8c138021fb1e184a24ed2a81c16c92f9f25c635c73918b1402df98f9c8aad \
    --experimental-control-plane --certificate-key a662b8364666f82c93cc5cd4fb4fabb623bbe9afdb182da353ac40f1752dfa4a
```

### 加入 Node 节点

```bash
# 示例如下
kubeadm join 192.168.141.200:6444 --token abcdef.0123456789abcdef \
    --discovery-token-ca-cert-hash sha256:2ea8c138021fb1e184a24ed2a81c16c92f9f25c635c73918b1402df98f9c8aad
```

### 验证是否可用

```bash
kubectl get node

# 输出如下，我们可以看到 Node 节点已经成功上线 ━━(￣ー￣*|||━━
NAME                   STATUS   ROLES    AGE     VERSION
kubernetes-master-01   Ready    master   19m     v1.14.2
kubernetes-master-02   Ready    master   4m46s   v1.14.2
kubernetes-master-03   Ready    master   3m23s   v1.14.2
kubernetes-node-01     Ready    <none>   74s     v1.14.2
```

```bash
watch kubectl get pods --all-namespaces

# 输出如下，coredns 也正常运行了
```

## k8s安装报错 Error: unknown flag: --experimental-upload-certs

**今天安装k8sV1.16的版本时候，执行突然发现命令不对，之前安装V1.15的时候是可以的，可能是版本升级的原因。**

### 解决：

**unknown flag:
--experimental-upload-certs，将–experimental-upload-certs 替换为 --upload-certs**

**注意：如果安装kubernetes 版本和下载的镜像版本不统一则会出现 timed out waiting for the condition 错误。中途失败或是想修改配置可以使用 kubeadm
reset 命令重置配置，再做初始化操作即可。**

## token忘了或者过期

### 1.先获取token

```bash
#如果过期可先执行此命令
kubeadm token create    #重新生成token
#列出token
kubeadm token list  | awk -F" " '{print $1}' |tail -n 1
```

### 2.获取CA公钥的哈希值

```bash
openssl x509 -pubkey -in /etc/kubernetes/pki/ca.crt | openssl rsa -pubin -outform der 2>/dev/null | openssl dgst -sha256 -hex | sed  's/^ .* //'
```

### 3.从节点加入集群

```bash
kubeadm join 192.168.40.8:6443 --token {token填这里}   --discovery-token-ca-cert-hash sha256:{哈希值填这里}
```

### 或者直接查看token

```bash
Kubeadm token list
```

