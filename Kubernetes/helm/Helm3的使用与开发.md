>  Helm是Kubernetes的软件包（或资源）管理工具，最近发布了Helm的3.0版本，提供了一些新特性，在使用上相比之前的版本更加简单、方便，比如：
>
>  - 移除Tiller，安装chart前无需执行init命令（包括安装tiller和初始化本地仓库），相对地也不再需要与Tiller交互，而是直接通过ApiServer进行安装操作
>  - 支持使用JSONSchema校验values
>  - 兼容v2版本

```bash
$helm --help
The Kubernetes package manager

Common actions for Helm:

- helm search:    search for charts
- helm pull:      download a chart to your local directory to view
- helm install:   upload the chart to Kubernetes
- helm list:      list releases of charts
```

> v3版本不再需要Tiller，而是通过ApiServer与k8s交互，可以设置环境变量`KUBECONFIG`来指定存有ApiServre的地址与token的配置文件地址，默认为`~/.kube/config`

```bash
# 生成chart文件
$helm create foo
Creating foo

# 打包
$helm package foo
Successfully packaged chart and saved it to: /home/test/helm/foo-0.1.0.tgz

# 安装
$helm install foo ./foo-0.1.0.tgz
NAME: foo
LAST DEPLOYED: Sat Dec  7 21:05:33 2019
NAMESPACE: default
STATUS: deployed
REVISION: 1
NOTES:
1. Get the application URL by running these commands:
  export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=foo,app.kubernetes.io/instance=foo" -o jsonpath="{.items[0].metadata.name}")
  echo "Visit http://127.0.0.1:8080 to use your application"
  kubectl --namespace default port-forward $POD_NAME 8080:80

# 查询release
$helm ls
NAME	NAMESPACE	REVISION	UPDATED                                	STATUS  	CHART    	APP VERSION
foo 	default  	1       	2019-12-07 21:05:33.355624435 +0800 CST	deployed	foo-0.1.0	1.16.0     

# 删除release
$helm delete foo
release "foo" uninstalled
```

repo相关操作

```bash
# 添加仓库
$helm repo add {仓库名字} {仓库地址}
"{仓库名字}" has been added to your repositories

# 查询仓库列表
$helm repo list
NAME  	    URL                                                            
{仓库名字}	{仓库地址}

# 查询chart包
$helm search repo

# 删除仓库
$helm repo remove {仓库名字}
"{仓库名字}" has been removed from your repositories
```

