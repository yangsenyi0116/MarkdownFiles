##  THREE BIG CONCEPTS

> A *Chart* is a Helm package. It contains all of the resource definitions necessary to run an application, tool, or service inside of a Kubernetes cluster. Think of it like the Kubernetes equivalent of a Homebrew formula, an Apt dpkg, or a Yum RPM file.
>
> A *Repository* is the place where charts can be collected and shared. It’s like Perl’s [CPAN archive](https://www.cpan.org/) or the [Fedora Package Database](https://admin.fedoraproject.org/pkgdb/), but for Kubernetes packages.
>
> A *Release* is an instance of a chart running in a Kubernetes cluster. One chart can often be installed many times into the same cluster. And each time it is installed, a new *release* is created. Consider a MySQL chart. If you want two databases running in your cluster, you can install that chart twice. Each one will have its own *release*, which will in turn have its own *release name*.

Helm将*图Charts安装到Kubernetes中，为每次安装创建一个新*版本*。要查找新图表，可以搜索Helm图表*存储库*。



## helm search

Helm带有强大的搜索命令。它可以用于搜索两种不同类型的源：

- `helm search hub`搜索[Helm Hub](https://hub.helm.sh/)，该中心包含来自许多不同存储库[的Helm](https://hub.helm.sh/)图表。
- `helm search repo`搜索已添加到本地头盔客户端（带有`helm repo add`）的存储库。该搜索是通过本地数据完成的，不需要公共网络连接。

您可以通过运行`helm search hub`以下命令找到公开可用的图表：

```bash
$ helm search hub wordpress
```

没有过滤器，将`helm search hub`显示所有可用图表。

使用`helm search repo`，您可以在已添加的存储库中找到图表的名称：

```bash
$ helm repo add brigade https://brigadecore.github.io/charts
"brigade" has been added to your repositories
$ helm search repo brigade
```

头盔搜索使用模糊字符串匹配算法，因此您可以键入部分单词或短语：

```bash
$ helm search repo kash
```

找到要安装的软件包后，就可以`helm install`用来安装它了。

## helm install

要安装新软件包，请使用`helm install`命令。简单来说，它包含两个参数：您选择的发行版名称和要安装的图表的名称。

```bash
$ helm install happy-panda stable/mariadb
```

在安装期间，`helm`客户端将打印有关创建了哪些资源，发布状态是什么以及是否可以或应该执行其他配置步骤的有用信息。

Helm不会等到所有资源都运行后才退出。许多图表需要大小超过600M的Docker映像，并且可能需要很长时间才能安装到集群中。

要跟踪发布的状态或重新读取配置信息，可以使用`helm status`：

```bash
$ helm status happy-panda
```

### 安装前自定义图表

。很多时候，您将需要自定义图表以使用首选配置。

要查看图表上可配置的选项，请使用`helm show values`：

```console
$ helm show values stable/mariadb
```

然后，您可以在YAML格式的文件中覆盖所有这些设置，然后在安装过程中传递该文件。

```console
$ echo '{mariadbUser: user0, mariadbDatabase: user0db}' > config.yaml
$ helm install -f config.yaml stable/mariadb
```

有两种在安装过程中传递配置数据的方式：

- `--values`（或`-f`）：指定带有替换的YAML文件。可以多次指定，最右边的文件优先
- `--set`：在命令行上指定替代。

如果同时使用这两个`--set`值，则将`--values`优先级更高的值合并到其中。指定的替代`--set`将保留在ConfigMap中。`--set`可以使用来查看给定版本的已查看值`helm get values `。`--set`可以`helm upgrade`使用`--reset-values`指定的值清除已清除的 值。

#### `--set`的格式和局限性 

该`--set`选项采用零个或多个名称/值对。最简单的用法是：`--set name=value`。相当于YAML：

```yaml
name: value
```

多个值用`,`字符分隔。所以`--set a=b,c=d`就变成了：

```yaml
a: b
c: d
```

支持更复杂的表达式。例如，`--set outer.inner=value` 翻译为：

```yaml
outer:
  inner: value
```

可以通过将值括在`{`和中来表示列表`}`。例如，`--set name={a, b, c}`转换为：

```yaml
name:
  - a
  - b
  - c
```

从Helm 2.5.0开始，可以使用数组索引语法访问列表项。例如，`--set servers[0].port=80`变为：

```yaml
servers:
  - port: 80
```

可以通过这种方式设置多个值。该行`--set servers[0].port=80,servers[0].host=example`变为：

```yaml
servers:
  - port: 80
    host: example
```

有时您需要在行中使用特殊字符`--set`。您可以使用反斜杠来转义字符。`--set name=value1\,value2`会变成：

```yaml
name: "value1,value2"
```

同样，您也可以转义点序列，当图表使用该`toYaml`函数解析注释，标签和节点选择器时，这可能会派上用场。的语法`--set nodeSelector."kubernetes\.io/role"=master` 变为：

```yaml
nodeSelector:
  kubernetes.io/role: master
```

使用很难表达深层嵌套的数据结构`--set`。鼓励图表设计人员`--set`在设计`values.yaml`文件格式时考虑使用情况。

### 更多安装方法

`helm install`命令可以从多个来源安装：

- 图表存储库（如上所述）
- 本地图表存档（`helm install foo foo-0.1.1.tgz`）
- 解压缩的图表目录（`helm install foo path/to/foo`）
- 完整网址（`helm install foo https://example.com/charts/foo-1.2.3.tgz`）

## 帮助升级”和“帮助回滚”：升级发行版，并在发生故障时恢复

发行新版本的图表时，或者要更改发行版的配置时，可以使用以下`helm upgrade`命令。

升级将采用现有版本，并根据您提供的信息对其进行升级。由于Kubernetes图表可能很大且很复杂，因此Helm尝试执行侵入性最小的升级。它将仅更新自上一发行版以来已更改的内容。

```console
$ helm upgrade -f panda.yaml happy-panda stable/mariadb
```

在上述情况下，`happy-panda`使用相同的图表但使用新的YAML文件升级了发行版：

```yaml
mariadbUser: user1
```

我们可以`helm get values`用来查看新设置是否生效。

```console
$ helm get values happy-panda
mariadbUser: user1
```



现在，如果某个版本在发行过程中未按计划进行，则可以使用轻松回滚到以前的发行版`helm rollback [RELEASE] [REVISION]`。

```console
$ helm rollback happy-panda 1
```



上面的内容将我们的“happy panda”版本还原到其第一个发行版本。发布版本是增量修订版。每次安装，升级或回滚时，修订版本号都会递增1。第一个修订版本号始终为1。我们可以`helm history [RELEASE]`用来查看特定发行版的修订版本号。

## 安装/升级/回滚的有用选项

您可以指定其他几个有用的选项，以在安装/升级/回滚期间自定义Helm的行为。请注意，这不是cli标志的完整列表。要查看所有标志的描述，只需运行`helm  --help`。

- `--timeout`：等待Kubernetes命令完成的秒数（默认为300（5分钟））
- `--wait`：等待，直到所有Pod都处于就绪状态，PVC绑定，部署的Pod处于最小状态（`Desired`减号`maxUnavailable`），Pod处于就绪状态，并且Services具有IP地址（如果为，则为Ingress `LoadBalancer`），然后将发布标记为成功。它将等待该`--timeout` 值。如果达到超时，版本将标记为`FAILED`。注意：在作为滚动更新策略一部分的Deployment `replicas`设置为1 `maxUnavailable`且未设置为0的情况下，`--wait`只要满足最小Pod就绪状态，它将返回就绪状态。
- `--no-hooks`：跳过命令的运行钩子
- `--recreate-pods`（仅适用于`upgrade`和`rollback`）：此标志将导致重新创建所有Pod（属于部署的Pod除外）。（在Helm3中弃用）

## HELM UNINSTALL

```console
$ helm uninstall happy-panda
```

这将从集群中删除发行版。您可以使用以下`helm list`命令查看所有当前已部署的发行版：

```console
$ helm list
```

在以前版本的Helm中，当删除发行版时，将保留其删除记录。在Helm 3中，删除也会删除发布记录。如果您希望保留删除发布记录，请使用`helm uninstall --keep-history`。使用`helm list --uninstalled`将仅显示带有`--keep-history`标志卸载的发行版。

该`helm list --all`标志将向您显示Helm保留的所有发布记录，包括失败或已删除项目的记录（如果`--keep-history`已指定）：

```console
$  helm list --all
```

请注意，由于默认情况下现在已删除发行版，因此不再可能回滚已卸载的资源。

## HELM REPO

Helm 3不再附带默认的图表存储库。该`helm repo`命令组提供用于添加，列出和删除存储库的命令。

您可以查看使用以下配置的存储库`helm repo list`：

```console
$ helm repo list
```

并且可以使用以下方式添加新的存储库`helm repo add`：

```console
$ helm repo add dev https://example.com/dev-charts
```

由于图表存储库经常更改，因此您可以通过运行随时确保您的Helm客户端是最新的`helm repo update`。

可以使用删除存储库`helm repo remove`。

##  创建自己的CHARTS

您可以使用以下`helm create`命令快速入门：

```console
$ helm create deis-workflow
Creating deis-workflow
```

现在在中有一个图表`./deis-workflow`。您可以编辑它并创建自己的模板。

在编辑图表时，您可以通过运行验证其格式正确 `helm lint`。

当需要打包图表以进行分发时，可以运行以下`helm package`命令：

```console
$ helm package deis-workflow
deis-workflow-0.1.0.tgz
```

现在可以通过`helm install`以下方式轻松安装该图表：

```console
$ helm install deis-workflow ./deis-workflow-0.1.0.tgz
```

打包的图表可以加载到图表存储库中。请参阅图表存储服务器的文档以了解如何上载。

注意：该`stable`存储库在[Kubernetes Charts GitHub存储](https://github.com/helm/charts)库上进行管理。该项目接受图表源代码，并（在审核之后）为您打包这些源代码。