### 在Linux上使用curl安装Kubectl二进制文件

使用以下命令下载最新版本

```bash
curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl
```

要下载特定版本，请用特定版本替换`$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)`命令部分。

例如，要在Linux上下载版本v1.17.0，请输入：

```bash
curl -LO https://storage.googleapis.com/kubernetes-release/release/v1.17.0/bin/linux/amd64/kubectl
```

使kubectl二进制可执行文件。

```bash
chmod +x ./kubectl
```

将二进制文件移到您的PATH中。

```bash
sudo mv ./kubectl /usr/local/bin/kubectl
```

### 安装minikube

#### Linux

```bash
curl -Lo minikube https://github.com/kubernetes/minikube/releases/download/v1.6.2/minikube-linux-amd64 && chmod +x minikube && sudo mv minikube /usr/local/bin/
```

minikube启动脚本

```bash
minikube start --image-mirror-country cn \
    --iso-url=https://kubernetes.oss-cn-hangzhou.aliyuncs.com/minikube/iso/minikube-v1.6.0.iso \
    --registry-mirror=https://xxxxxx.mirror.aliyuncs.com \
    --vm-driver=none
```

