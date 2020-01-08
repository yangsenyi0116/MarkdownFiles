

## 先决条件

要成功且正确地确保使用Helm，必须满足以下先决条件。

1. Kubernetes集群
2. 确定要应用于安装的安全性配置（如果有）
3. 安装和配置Helm。

### 安装Kubernetes或有权访问集群

- 您必须安装Kubernetes。对于Helm的最新版本，我们建议使用Kubernetes的最新稳定版本，在大多数情况下，它是第二最新的次要版本。
- 您还应该具有的本地配置副本`kubectl`。

注意：1.6之前的Kubernetes版本对基于角色的访问控制（RBAC）的支持有限或不支持。

## 安装头盔

下载Helm客户端的二进制版本。您可以使用类似的工具`homebrew`，或查看[官方发行版页面](https://github.com/helm/helm/releases)。

有关更多详细信息或其他选项，请参阅[安装指南](https://helm.sh/docs/intro/install/)。

## 初始化头盔图存储库

准备好头盔后，可以添加图表存储库。官方的Helm稳定图表是最受欢迎的起始位置：

```bash
$ helm repo add stable https://kubernetes-charts.storage.googleapis.com/
```

安装后，您将能够列出可以安装的图表：

```bash
helm search repo stable
```

## 安装示例图

要安装图表，可以运行`helm install`命令。Helm有几种查找和安装图表的方法，但是最简单的方法是使用官方 `stable`图表之一。

```bash
$ helm repo update              # Make sure we get the latest list of charts
$ helm install stable/mysql --generate-name
Released smiling-penguin
```

在上面的示例中，`stable/mysql`图表已发布，而我们的新版本的名称为`smiling-penguin`。

通过运行，可以轻松了解此MySQL图表的功能 `helm show chart stable/mysql`。或者，您可以运行`helm show all stable/mysql` 以获取有关图表的所有信息。

每当您安装图表时，都会创建一个新版本。因此，一个图表可以多次安装到同一群集中。而且每个都可以独立管理和升级。

## 了解版本

`helm list`功能将显示所有已部署版本的列表

## 卸载发行版

要卸载发行版，请使用以下`helm uninstall`命令：

```bash
$ helm uninstall smiling-penguin
Removed smiling-penguin
```

这`smiling-penguin`将从Kubernetes 卸载，这将删除与该发行版关联的所有资源以及发行历史记录。

如果`--keep-history`提供了该标志，则将保留发布历史记录。您将能够请求有关该版本的信息：

```bash
$ helm status smiling-penguin
Status: UNINSTALLED
...
```

由于Helm甚至在卸载后仍会跟踪您的发行版，因此您可以审核集群的历史记录，甚至可以取消删除发行版（使用`helm rollback`）。

## 阅读帮助文本

要了解有关可用的Helm命令的更多信息，请使用`helm help`或键入命令，后跟`-h`标志：

```bash
$ helm get -h
```