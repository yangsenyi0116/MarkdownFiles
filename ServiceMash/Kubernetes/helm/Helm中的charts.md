# Charts

Helm使用一种称为*Charts*的包装格式。Charts是描述一组相关的Kubernetes资源的文件的集合。单个Charts可能用于部署简单的内容（例如内存缓存pod）或复杂的内容（例如带有HTTP服务器，数据库，缓存等的完整Web应用程序堆栈）

将Charts创建为放在特定目录树中的文件，然后可以将它们打包到要部署的版本化存档中。

本文档说明了Charts格式，并提供了使用Helm构建Charts的基本指南。

## Charts文件结构

Charts被组织为目录内文件的集合。目录名称是Charts的名称（不包含版本信息）。因此，描述WordPress的Charts将存储在`wordpress/`目录中。

在此目录中，Helm将期望与以下内容匹配的结构：

```text
wordpress/
  Chart.yaml          # A YAML file containing information about the Charts
  LICENSE             # OPTIONAL: A plain text file containing the license for the Charts
  README.md           # OPTIONAL: A human-readable README file
  values.yaml         # The default configuration values for this Charts
  values.schema.json  # OPTIONAL: A JSON Schema for imposing a structure on the values.yaml file
  Charts/             # A directory containing any Charts upon which this Charts depends.
  crds/               # Custom Resource Definitions
  templates/          # A directory of templates that, when combined with values,
                      # will generate valid Kubernetes manifest files.
  templates/NOTES.txt # OPTIONAL: A plain text file containing short usage notes
```

Helm储备使用的`Charts/`，`crds/`和`templates/`目录，并列出文件名。其他文件将保留原样。

## Charts.YAML文件

该`Chart.yaml`文件是Charts所必需的。它包含以下字段：

```yaml
apiVersion: The Charts API version (required)
name: The name of the Charts (required)
version: A SemVer 2 version (required)
kubeVersion: A SemVer range of compatible Kubernetes versions (optional)
description: A single-sentence description of this project (optional)
type: It is the type of Charts (optional)
keywords:
  - A list of keywords about this project (optional)
home: The URL of this project's home page (optional)
sources:
  - A list of URLs to source code for this project (optional)
dependencies: # A list of the Charts requirements (optional)
  - name: The name of the Charts (nginx)
    version: The version of the Charts ("1.2.3")
    repository: The repository URL ("https://example.com/Charts") or alias ("@repo-name")
    condition: (optional) A yaml path that resolves to a boolean, used for enabling/disabling Charts (e.g. subCharts1.enabled )
    tags: # (optional)
      - Tags can be used to group Charts for enabling/disabling together
    enabled: (optional) Enabled bool determines if Charts should be loaded
    import-values: # (optional)
      - ImportValues holds the mapping of source values to parent key to be imported. Each item can be a string or pair of child/parent sublist items.
    alias: (optional) Alias usable alias to be used for the Charts. Useful when you have to add the same Charts multiple times
maintainers: # (optional)
  - name: The maintainer's name (required for each maintainer)
    email: The maintainer's email (optional for each maintainer)
    url: A URL for the maintainer (optional for each maintainer)
icon: A URL to an SVG or PNG image to be used as an icon (optional).
appVersion: The version of the app that this contains (optional). This needn't be SemVer.
deprecated: Whether this Charts is deprecated (optional, boolean)
```

其他字段将被静默忽略。

### Chartss and Versioning

Every Charts must have a version number. A version must follow the [SemVer 2](https://semver.org/spec/v2.0.0.html) standard.

与Helm Classic不同，Kubernetes Helm使用版本号作为发行标记。存储库中的软件包通过名称加上版本来标识。

例如，将`nginx`版本字段设置为的Charts`version: 1.2.3` 将命名为：

```text
nginx-1.2.3.tgz
```

### The `apiVersion` Field

The `apiVersion` field should be `v2` for Helm Chartss that require at least Helm 3.

 Chartss supporting previous Helm versions have an `apiVersion` set to `v1` and are still installable by Helm 3.

### The `appVersion` Field

请注意，该`appVersion`字段与该`version`字段无关。这是指定应用程序版本的一种方式。例如，`drupal` Charts可能有一个`appVersion: 8.2.1`，指示Charts中包含的Drupal版本（默认）是`8.2.1`。此字段仅供参考，对Charts版本计算没有影响。

### 弃用Charts

在“Charts存储库”中管理Charts时，有时必须弃用Charts。中的可选`deprecated`字段`Chart.yaml`可用于将Charts标记为已弃用。如果将存储库中Charts的**最新**版本标记为已弃用，则将整个Charts视为已弃用。以后可以通过发布未标记为不推荐使用的较新版本来重用Charts名称。弃用Charts的工作流程为：[kubernetes / charts](https://github.com/helm/charts)项目，其后为：

1. 更新Charts`Chart.yaml`以将Charts标记为已弃用，从而增加版本
2. 在Charts存储库中发布新的Charts版本
3. 从源存储库中删除Charts（例如git）

### Charts类型s

该`type`字段定义Charts的类型。有两种类型：`application` 和`library`。应用程序是默认类型，它是可以完全操作的标准Charts。的[库或辅助Charts](https://github.com/helm/charts/tree/master/incubator/common)提供了实用程序或函数的Charts生成器。库图与应用程序图不同，因为它没有资源对象，因此无法安装。

**注意：**应用程序Charts可以用作库Charts。通过将类型设置为启用此功能`library`。然后，该Charts将呈现为库Charts，在其中可以利用所有实用程序和功能。Charts的所有资源对象都不会呈现。

## Chart许可，自述文件和注释

Chart还可以包含描述Chart的安装，配置，用法和许可证的文件。

许可证是一个纯文本文件，其中包含Chart的 [许可证](https://en.wikipedia.org/wiki/Software_license)。该Chart可以包含许可证，因为它可能在模板中具有编程逻辑，因此将不仅是配置。如果需要，Chart还可以为应用程序安装单独的许可证。

Chart的自述文件应采用Markdown（README.md）格式，并且通常应包含：

- Chart提供的应用程序或服务的描述
- 运行Chart的任何先决条件或要求
- 中的选项说明`values.yaml`和默认值
- 与Chart的安装或配置有关的任何其他信息

该Chart还可以包含一个简短的纯文本`templates/NOTES.txt`文件，该文件将在安装后以及查看发行版状态时打印出来。该文件被评估为[模板](https://helm.sh/docs/topics/charts/#templates-and-values)，可用于显示使用说明，后续步骤或与Chart发布有关的任何其他信息。例如，可以提供用于连接到数据库或访问Web UI的指令。由于在运行`helm install`或时将此文件打印到STDOUT `helm status`，因此建议保持内容简短并指向自述文件以获取更多详细信息。

## Chart依存关系

在Helm中，一个Chart可能取决于许多其他Chart。这些依赖项可以使用`dependencies`字段中的字段进行动态链接，也可以`Chart.yaml`引入`charts/`目录中并手动进行管理。



### 与`dependencies`现场管理依赖关系

当前Chart所需的Chart在`dependencies`字段中定义为列表 。

```yaml
dependencies:
  - name: apache
    version: 1.2.3
    repository: https://example.com/charts
  - name: mysql
    version: 3.2.1
    repository: https://another.example.com/charts
```

- 该`name`字段是所需Chart的名称。
- 该`version`字段是所需Chart的版本。
- 该`repository`字段是Chart存储库的完整URL。请注意，您还必须使用`helm repo add`本地添加该存储库。
- 您可以使用存储库的名称代替URL

```console
$ helm repo add fantastic-charts https://fantastic-charts.storage.googleapis.com
dependencies:
  - name: awesomeness
    version: 1.0.0
    repository: "@fantastic-charts"
```

定义依赖项后，便可以运行`helm dependency update`，它将使用依赖项文件将所有指定的Chart下载到您的 `charts/`目录中。

```console
$ helm dep up foochart
Hang tight while we grab the latest from your chart repositories...
...Successfully got an update from the "local" chart repository
...Successfully got an update from the "stable" chart repository
...Successfully got an update from the "example" chart repository
...Successfully got an update from the "another" chart repository
Update Complete. Happy Helming!
Saving 2 charts
Downloading apache from repo https://example.com/charts
Downloading mysql from repo https://another.example.com/charts
```

当`helm dependency update`检索Chart，将其存储为图档案的`charts/`目录。因此，对于上面的示例，可以期望在Chart目录中看到以下文件：

```text
charts/
  apache-1.2.3.tgz
  mysql-3.2.1.tgz
```

#### 依赖项中的别名字段

除上述其他字段外，每个需求条目还可以包含可选字段`alias`。

为依赖关系图添加别名将使用别名作为新依赖关系的名称将Chart置于依赖关系中。

`alias`如果他们需要访问其他名称的Chart，则可以使用。

```yaml
# parentchart/Chart.yaml

dependencies:
  - name: subchart
    repository: http://localhost:10191
    version: 0.1.0
    alias: new-subchart-1
  - name: subchart
    repository: http://localhost:10191
    version: 0.1.0
    alias: new-subchart-2
  - name: subchart
    repository: http://localhost:10191
    version: 0.1.0
```

在上面的示例中，我们总共获得3个依赖项`parentchart`：

```text
subchart
new-subchart-1
new-subchart-2
```

手动实现此目的的方法是通过`charts/`使用相同的名称多次复制/粘贴目录中的同一Chart 。

#### 依赖项中的标签和条件字段

除上述其他字段外，每个需求条目还可以包含可选字段`tags`和`condition`。

默认情况下会加载所有Chart。如果存在`tags`或`condition`字段，则将对它们进行评估并用于控制所应用Chart的负载。

条件-条件字段包含一个或多个YAML路径（以逗号分隔）。如果此路径存在于最高父级的值中并解析为布尔值，则将基于该布尔值启用或禁用Chart。仅评估列表中找到的第一个有效路径，如果不存在路径，则该条件无效。

标签-标签字段是与该Chart关联的YAML标签列表。在顶级父项的值中，可以通过指定标签和布尔值来启用或禁用所有带有标签的Chart。

```yaml
# parentchart/Chart.yaml

dependencies:
      - name: subchart1
        repository: http://localhost:10191
        version: 0.1.0
        condition: subchart1.enabled, global.subchart1.enabled
        tags:
          - front-end
          - subchart1

      - name: subchart2
        repository: http://localhost:10191
        version: 0.1.0
        condition: subchart2.enabled,global.subchart2.enabled
        tags:
          - back-end
          - subchart2
# parentchart/values.yaml

subchart1:
  enabled: true
tags:
  front-end: false
  back-end: true
```

在上面的示例中，所有带有标签的Chart`front-end`都将被禁用，但是由于`subchart1.enabled`路径在父级的值中评估为“ true”，因此条件将覆盖`front-end`标签并被`subchart1`启用。

由于`subchart2`标记为，`back-end`且该标记的值为`true`， `subchart2`将启用。还应注意，尽管`subchart2`已指定条件，但父级值中没有相应的路径和值，因此该条件无效。

##### 将CLI与标签和条件一起使用

该`--set`参数可以照常用于更改标签和条件值。

```console
helm install --set tags.front-end=true --set subchart2.enabled=false
```

##### 标签和条件解析

- **条件（在值中设置时）始终会覆盖标签。**存在的第一个条件路径获胜，而该Chart的后续条件路径将被忽略。
- 标签被评估为“如果Chart的任何标签为真，则启用Chart”。
- 标签和条件值必须设置在顶级父项的值中。
- `tags:`值中的键必须是顶级键。`tags:` 当前不支持全局和嵌套表。

#### 通过依赖项导入子值

在某些情况下，希望允许子Chart的值传播到父Chart并作为通用默认值共享。使用该`exports`格式的另一个好处是，它将使将来的工具能够内省用户可设置的值。

可以使用YAML列表在父Chart的`dependencies`字段中指定包含要导入的值的键`input-values`。列表中的每个项目都是从子Chart的`exports`字段导入的键。

要导入`exports`键中未包含的值，请使用 [子代父](https://helm.sh/docs/topics/charts/#using-the-child-parent-format)格式。两种格式的示例如下所述。

##### 使用导出格式

如果子Chart的`values.yaml`文件`exports`的根部包含一个字段，则可以通过指定要导入的键，将其内容直接导入到父级的值中，如下例所示：

```yaml
# parent's Chart.yaml file

dependencies:
  - name: subchart
    repository: http://localhost:10191
    version: 0.1.0
    import-values:
      - data
# child's values.yaml file

exports:
  data:
    myint: 99
```

由于我们`data`在导入列表中指定了密钥，因此Helm在`exports`子Chart的 字段中查找`data`密钥并导入其内容。

最终的父值将包含我们的导出字段：

```yaml
# parent's values

myint: 99
```

请注意，父键`data`未包含在父键的最终值中。如果您需要指定父键，请使用“子父键”格式。

##### 使用儿童父母格式

要访问`exports`子Chart的值的键中未包含的值，您将需要指定要导入的值的源键（`child`）和父图的值（`parent`）中的目标路径。

在`import-values`下面指示Helm的例子采取在发现的任何值`child:`路径，并将其复制到父的值在指定的路径 `parent:`

```yaml
# parent's Chart.yaml file

dependencies:
  - name: subchart1
    repository: http://localhost:10191
    version: 0.1.0
    ...
    import-values:
      - child: default.data
        parent: myimports
```

在上面的示例中，`default.data`在子Chart1的值中找到的值将被导入到`myimports`父Chart的值的键中，如下所示：

```yaml
# parent's values.yaml file

myimports:
  myint: 0
  mybool: false
  mystring: "helm rocks!"
# subchart1's values.yaml file

default:
  data:
    myint: 999
    mybool: true
```

父Chart的结果值为：

```yaml
# parent's final values

myimports:
  myint: 999
  mybool: true
  mystring: "helm rocks!"
```

父级的最终值现在包含从subchart1导入的`myint`和`mybool`字段。

### 通过`charts/`目录手动管理依赖项

如果需要对依赖性进行更多控制，则可以通过将依赖性Chart复制到`charts/` 目录中来明确表示这些依赖性。

依赖项可以是Chart存档（`foo-1.2.3.tgz`）或未打包的Chart目录。但是其名称不能以`_`或开头`.`。Chart加载器将忽略此类文件。

例如，如果WordPressChart依赖于ApacheChart，则WordPressChart的`charts/` 目录中将提供（正确版本的）ApacheChart：

```yaml
wordpress:
  Chart.yaml
  # ...
  charts/
    apache/
      Chart.yaml
      # ...
    mysql/
      Chart.yaml
      # ...
```

上面的示例显示了WordPressChart如何通过将这些Chart包含在`charts/`目录中来表达其对Apache和MySQL的依赖性。

**提示：** *要将依赖项放入`charts/`目录，请使用以下`helm pull`命令*

### 使用依赖项的操作方面

以上各节说明了如何指定Chart依赖关系，但这如何影响使用`helm install`和的Chart安装`helm upgrade`？

假设一个名为“ A”的Chart创建了以下Kubernetes对象

- 命名空间“ A-Namespace”
- statefulset“ A-StatefulSet”
- 服务“ A-服务”

此外，A依赖于创建对象的ChartB

- 命名空间“ B-Namespace”
- 复制集“ B-ReplicaSet”
- 服务“ B服务”

在安装/升级ChartA之后，将创建/修改单个Helm版本。该发行版将按以下顺序创建/更新上述所有Kubernetes对象：

- 名称空间
- B命名空间
- 状态集
- B-复制集
- 服务
- B服务

这是因为当Helm安装/升级Chart时，Chart中的Kubernetes对象及其所有依赖项都是

- 聚集成一个集合；然后
- 按类型排序，再按名称排序；接着
- 以该顺序创建/更新。

因此，将使用Chart的所有对象及其依存关系创建一个发行版。

Kubernetes类型的安装顺序由kind_sorter.go中的枚举InstallOrder给出（请参阅[Helm源文件](https://github.com/helm/helm/blob/484d43913f97292648c867b56768775a55e4bba6/pkg/releaseutil/kind_sorter.go)）。

## 模板和值

Helm Chart模板以[Go模板语言](https://golang.org/pkg/text/template/)编写，另外还增加[了Sprig库中](https://github.com/Masterminds/sprig)的50个左右的附加模板函数以及其他一些[专用函数](https://helm.sh/docs/howto/charts_tips_and_tricks/)。

所有模板文件都存储在Chart的`templates/`文件夹中。当Helm渲染Chart时，它将通过模板引擎传递该目录中的每个文件。

模板的值通过两种方式提供：

- Chart开发人员可以`values.yaml`在Chart内部提供一个文件。该文件可以包含默认值。
- Chart用户可以提供包含值的YAML文件。可以在命令行上使用提供`helm install`。

当用户提供自定义值时，这些值将覆盖Chart`values.yaml`文件中的值。

### 模板文件

模板文件遵循编写Go模板的标准约定（有关详细信息，请参见 [text / template Go软件包文档](https://golang.org/pkg/text/template/)）。示例模板文件可能如下所示：

```yaml
apiVersion: v1
kind: ReplicationController
metadata:
  name: deis-database
  namespace: deis
  labels:
    app.kubernetes.io/managed-by: deis
spec:
  replicas: 1
  selector:
    app.kubernetes.io/name: deis-database
  template:
    metadata:
      labels:
        app.kubernetes.io/name: deis-database
    spec:
      serviceAccount: deis-database
      containers:
        - name: deis-database
          image: {{ .Values.imageRegistry }}/postgres:{{ .Values.dockerTag }}
          imagePullPolicy: {{ .Values.pullPolicy }}
          ports:
            - containerPort: 5432
          env:
            - name: DATABASE_STORAGE
              value: {{ default "minio" .Values.storage }}
```

上面的示例大致基于 https://github.com/deis/charts，是Kubernetes复制控制器的模板。它可以使用以下四个模板值（通常在`values.yaml`文件中定义）：

- `imageRegistry`：Docker镜像的源注册表。
- `dockerTag`：泊坞窗映像的标签。
- `pullPolicy`：Kubernetes拉动政策。
- `storage`：存储后端，其默认设置为 `"minio"`

所有这些值均由模板作者定义。Helm不需要或规定参数。

要查看许多工作Chart，请查看[Kubernetes Charts项目](https://github.com/helm/charts)

### 预定义值

可以从模板中的对象访问通过`values.yaml`文件（或通过`--set`标志）提供的值`.Values`。但是，您还可以在模板中访问其他预定义的数据。

以下值是预定义的，可用于每个模板，并且不能被覆盖。与所有值一样，名称*区分大小写*。

- `Release.Name`：版本名称（而非Chart）
- `Release.Namespace`：Chart发布到的名称空间。
- `Release.Service`：进行发布的服务。
- `Release.IsUpgrade`：如果当前操作是升级或回滚，则将其设置为true。
- `Release.IsInstall`：如果当前操作是安装，则将其设置为true。
- `Chart`：的内容`Chart.yaml`。因此，可以通过获取Chart版本，`Chart.Version`而维护者位于中`Chart.Maintainers`。
- `Files`：类似于地图的对象，其中包含Chart中的所有非特殊文件。这不会给您访问模板的权限，但是会给您访问存在的其他文件的权限（除非使用排除了它们`.helmignore`）。可以使用`{{ index .Files "file.name" }}`或使用`{{ .Files.Get name }}`或`{{ .Files.GetString name }}`功能来访问文件。您也可以访问该文件的内容，`[]byte`使用`{{ .Files.GetBytes }}`
- `Capabilities`：类似于地图的对象，其中包含有关Kubernetes的版本（`{{ .Capabilities.KubeVersion }}`以及支持的Kubernetes API版本（`{{ .Capabilities.APIVersions.Has "batch/v1" }}`）的信息

**注意：**任何未知`Chart.yaml`字段将被删除。它们将无法在`Chart`对象内部访问。因此，`Chart.yaml`不能用于将任意结构化的数据传递到模板中。不过，可以使用值文件。

### 值文件

考虑上一节中的模板，一个`values.yaml`提供必要值的文件如下所示：

```yaml
imageRegistry: "quay.io/deis"
dockerTag: "latest"
pullPolicy: "Always"
storage: "s3"
```

值文件的格式为YAML。Chart可能包括默认`values.yaml` 文件。Helm install命令允许用户通过提供其他YAML值来覆盖值：

```console
$ helm install --values=myvals.yaml wordpress
```

以这种方式传递值时，它们将合并到默认值文件中。例如，考虑一个`myvals.yaml`看起来像这样的文件：

```yaml
storage: "gcs"
```

将其与`values.yaml`Chart中的合并后，生成的结果将为：

```yaml
imageRegistry: "quay.io/deis"
dockerTag: "latest"
pullPolicy: "Always"
storage: "gcs"
```

请注意，只有最后一个字段被覆盖。

**注意：**Chart内包含的默认值文件*必须*命名为 `values.yaml`。但是在命令行上指定的文件可以命名为任何东西。

**注意：**如果`--set`在`helm install`或上使用了该标志`helm upgrade`，则这些值将在客户端简单地转换为YAML。

**注意：**如果值文件中存在任何必需的条目，则可以使用[“ required”功能](https://helm.sh/docs/howto/charts_tips_and_tricks/)将它们声明为Chart模板中[的必需项](https://helm.sh/docs/howto/charts_tips_and_tricks/)

然后，可以使用`.Values` 对象在模板内部访问以下任何一个值：

```yaml
apiVersion: v1
kind: ReplicationController
metadata:
  name: deis-database
  namespace: deis
  labels:
    app.kubernetes.io/managed-by: deis
spec:
  replicas: 1
  selector:
    app.kubernetes.io/name: deis-database
  template:
    metadata:
      labels:
        app.kubernetes.io/name: deis-database
    spec:
      serviceAccount: deis-database
      containers:
        - name: deis-database
          image: {{ .Values.imageRegistry }}/postgres:{{ .Values.dockerTag }}
          imagePullPolicy: {{ .Values.pullPolicy }}
          ports:
            - containerPort: 5432
          env:
            - name: DATABASE_STORAGE
              value: {{ default "minio" .Values.storage }}
```

### 范围，依赖关系和价值观

值文件可以声明顶级Chart以及该Chart`charts/`目录中包含的任何Chart的值。或者，换句话说，值文件可以为Chart及其任何依赖项提供值。例如，上面的演示WordPressChart同时具有`mysql`和`apache`作为依赖项。值文件可以为所有这些组件提供值：

```yaml
title: "My WordPress Site" # Sent to the WordPress template

mysql:
  max_connections: 100 # Sent to MySQL
  password: "secret"

apache:
  port: 8080 # Passed to Apache
```

较高级别的Chart可以访问下面定义的所有变量。因此，WordPressChart可以访问MySQL密码`.Values.mysql.password`。但是较低级别的Chart无法访问父Chart中的内容，因此MySQL将无法访问该`title`属性。就此而言，它也无法访问 `apache.port`。

值已命名空间，但修剪了命名空间。因此，对于WordPressChart，它可以访问MySQL密码字段`.Values.mysql.password`。但是对于MySQLChart，值的范围已减小，名称空间前缀已删除，因此它将看到密码字段为`.Values.password`。

#### Global Values

从2.0.0-Alpha.2开始，Helm支持特殊的“全局”值。考虑上一个示例的修改后的版本：

```yaml
title: "My WordPress Site" # Sent to the WordPress template

global:
  app: MyWordPress

mysql:
  max_connections: 100 # Sent to MySQL
  password: "secret"

apache:
  port: 8080 # Passed to Apache
```

上面添加了`global`带有值的部分`app: MyWordPress`。该值可用于*所有*Chart`.Values.global.app`。

例如，该`mysql`模板可以访问`app`如`{{ .Values.global.app }}`，所以可以在`apache`Chart。实际上，上面的值文件是这样重新生成的：

```yaml
title: "My WordPress Site" # Sent to the WordPress template

global:
  app: MyWordPress

mysql:
  global:
    app: MyWordPress
  max_connections: 100 # Sent to MySQL
  password: "secret"

apache:
  global:
    app: MyWordPress
  port: 8080 # Passed to Apache
```

这提供了一种与所有子Chart共享一个顶级变量的方法，这对于诸如设置`metadata`标签之类的属性很有用。

如果子图声明了全局变量，则该全局将*向下* （传递到子图的子图），而不*向上*传递到父图。子Chart无法影响父Chart的值。

同样，父Chart的全局变量优先于子图中的全局变量。



### 模式文件

有时，Chart维护者可能希望在其值上定义一个结构。这可以通过在`values.schema.json`文件中定义架构来完成。模式表示为[JSON模式](https://json-schema.org/)。它可能看起来像这样：

```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "properties": {
    "image": {
      "description": "Container Image",
      "properties": {
        "repo": {
          "type": "string"
        },
        "tag": {
          "type": "string"
        }
      },
      "type": "object"
    },
    "name": {
      "description": "Service name",
      "type": "string"
    },
    "port": {
      "description": "Port",
      "minimum": 0,
      "type": "integer"
    },
    "protocol": {
      "type": "string"
    }
  },
  "required": [
    "protocol",
    "port"
  ],
  "title": "Values",
  "type": "object"
}
```

该模式将应用于值以对其进行验证。调用以下任何命令时，都会进行验证：

- `helm install`
- `helm upgrade`
- `helm lint`
- `helm template`

`values.yaml`满足此架构要求的文件示例可能如下所示：

```yaml
name: frontend
protocol: https
port: 443
```

请注意，该模式不仅适用于文件`.Values`，还适用于最终对象`values.yaml`。这意味着以下`yaml`Chart是有效的，因为该Chart是通过`--set`以下所示的适当选项安装的。

```yaml
name: frontend
protocol: https
helm install --set port=443
```

此外，将`.Values`根据*所有*子Chart模式检查最终对象。这意味着父Chart无法规避对子Chart的限制。这也可以反向执行-如果子图具有子`values.yaml`图文件中未满足的要求，则父图*必须*满足这些限制才能生效。

### 参考文献

在编写模板，值和架构文件时，有一些标准参考可以帮助您。

- [转到模板](https://godoc.org/text/template)
- [额外的模板功能](https://godoc.org/github.com/Masterminds/sprig)
- [YAML格式](https://yaml.org/spec/)
- [JSON模式](https://json-schema.org/)

## 自定义资源定义（CRD）

Kubernetes提供了一种声明新类型的Kubernetes对象的机制。使用CustomResourceDefinitions（CRD），Kubernetes开发人员可以声明自定义资源类型。

在Helm3中，CRD被视为一种特殊的对象。它们已安装在Chart的其余部分之前，并且受到某些限制。

CRD YAML文件应放在`crds/`Chart内的目录中。多个CRD（由YAML起始和结束标记分隔）可以放在同一文件中。Helm将尝试将CRD目录中的*所有*文件加载到Kubernetes中。

CRD文件*无法模板化*。它们必须是纯YAML文档。

当Helm安装新Chart时，它将上载CRD，暂停直到API服务器提供CRD为止，然后启动模板引擎，呈现其余Chart，然后将其上载到Kubernetes。由于此顺序，CRD信息`.Capabilities`在Helm模板的对象中可用，并且Helm模板可以创建在CRD中声明的对象的新实例。

例如，如果您的Chart`CronTab`在`crds/`目录中具有CRD，则可以在目录中创建`CronTab`此类的实例`templates/`：

```text
crontabs/
  Chart.yaml
  crds/
    crontab.yaml
  templates/
    mycrontab.yaml
```

该`crontab.yaml`文件必须包含没有模板指令的CRD：

```yaml
kind: CustomResourceDefinition
metadata:
  name: crontabs.stable.example.com
spec:
  group: stable.example.com
  versions:
    - name: v1
      served: true
      storage: true
  scope: Namespaced
  names:
    plural: crontabs
    singular: crontab
    kind: CronTab
```

然后，模板`mycrontab.yaml`可以创建一个新的`CronTab`模板（照常使用模板）：

```yaml
apiVersion: stable.example.com
kind: CronTab
metadata:
  name: {{ .Values.name }}
spec:
   # ...
```

Helm将确保`CronTab`在Kubernetes API服务器上进行安装之前，已经安装了该类型并且可以从Kubernetes API服务器获得该类型 `templates/`。

### CRD的局限性

与Kubernetes中的大多数对象不同，CRD是全局安装的。因此，Helm在管理CRD时采取非常谨慎的方法。CRD受以下限制：

- 永远不会重新安装CRD。如果Helm确定`crds/` 目录中的CRD 已经存在（无论版本如何），Helm将不会尝试安装或升级。
- CRD永远不会在升级或回滚时安装。Helm将仅在安装操作时创建CRD。
- CRD永远不会被删除。删除CRD会自动删除群集中所有名称空间中所有CRD的内容。因此，Helm将不会删除CRD。

鼓励想要升级或删除CRD的操作员手动仔细地进行此操作。

## 使用Helm管理Chart

该`helm`工具有几个用于处理Chart的命令。

它可以为您创建一个新Chart：

```console
$ helm create mychart
Created mychart/
```

编辑Chart后，`helm`可以将其打包到Chart归档中：

```console
$ helm package mychart
Archived mychart-0.1.-.tgz
```

您还可以`helm`用来帮助您查找Chart格式或信息方面的问题：

```console
$ helm lint mychart
No issues found
```

## Chart存储库

甲*Chart存储库*是一个HTTP服务器，其容纳一个或多个封装的Chart。虽然`helm`可以用来管理本地Chart目录，但是在共享Chart时，首选的机制是Chart存储库。

可以提供YAML文件和tar文件并可以回答GET请求的任何HTTP服务器都可以用作存储库服务器。Helm团队已经测试了一些服务器，包括启用了网站模式的Google Cloud Storage和启用了网站模式的S3。

存储库的主要特征是存在一个称为的特殊文件，该文件 `index.yaml`具有该存储库提供的所有软件包的列表以及允许检索和验证这些软件包的元数据。

在客户端，使用`helm repo`命令管理存储库。但是，Helm不提供用于将Chart上传到远程存储库服务器的工具。这是因为这样做会给实施服务器增加实质性要求，并因此增加了建立存储库的障碍。

## Chart入门包

该`helm create`命令带有一个可选`--starter`选项，可让您指定“入门Chart”。

入门仅仅是常规Chart，但位于中 `$XDG_DATA_HOME/helm/starters`。作为Chart开发人员，您可以编写专门用作入门的Chart。此类Chart的设计应考虑以下注意事项：

- 在`Chart.yaml`将被覆盖由发电机。
- 用户将期望修改此类Chart的内容，因此文档应说明用户如何进行修改。
- 所有出现的``都将替换为指定的Chart名称，以便可以将入门Chart用作模板。

当前，添加Chart的唯一方法`$XDG_DATA_HOME/helm/starters`是在其中手动复制Chart。在Chart的文档中，您可能需要解释该过程.