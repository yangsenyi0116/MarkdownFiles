## 一般信息

### URL+身份认证

目前可以使用以下URL访问该API：https://api.openstreetmap.org/

根据API测试软件时，您应该考虑使用**https://master.apis.dev.openstreetmap.org/**而不是live-api。您的实时服务帐户不在同一数据库中，因此您可能需要测试服务的新用户名和密码。请在浏览器中访问该页面进行注册

对API进行的所有更新，创建或删除数据的调用都必须由经过身份验证和授权的用户进行。身份验证通过使用使用用户名和密码的`HTTP Basic`身份验证或使用`OAuth`进行

### 错误码

##### HTTP状态码400（错误请求）

如果您正在访问API的cgimap版本，则当OAuth失败并显示“错误的OAuth请求”时，将返回此错误代码。

##### HTTP状态码401（未经授权）

登录失败。

##### HTTP状态码403（禁止）

登录成功，但用户已被阻止，应用程序应显示错误消息（如有必要，将进行翻译），如果它具有最终用户UI，则提供一种简单的方法来访问openstreetmap.org并查看其中的任何消息。



### 元素

> 通过API调用可以创建，读取，更新和删除[构成](https://wiki.openstreetmap.org/wiki/Elements) OpenStreetMap的地图数据的三个基本[元素](https://wiki.openstreetmap.org/wiki/Elements)。它们各自以XML格式返回或期望元素的数据。

### 变更集

> 对一个或多个元素的每次修改都必须引用一个开放的[变更集](https://wiki.openstreetmap.org/wiki/API_v0.6#Changesets_2)。

### 标签

> 每个元素和变更集可以具有任意数量的标签。标签是一对键值对Unicode字符串，每个字符串最多255个完整的unicode字符（不是字节）。

### 最大字符串长度

>API的当前rails实现将对象，变更集和用户首选项标签以及关系成员角色的键和值字符串的长度限制为最多255个字符。
>
>注意：限制实际上是255个字符，而不是256个字符。

## API调用

### GET 

#### `/api/versions`

返回此实例支持的API版本的列表。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<osm generator="OpenStreetMap server" copyright="OpenStreetMap and contributors" attribution="http://www.openstreetmap.org/copyright" license="http://opendatacommons.org/licenses/odbl/1-0/">
  <api>
    <version>0.6</version>
  </api>
</osm>
```

#### `/api/capabilities`

该API调用旨在提供有关当前API的功能和限制的信息。

##### response

```xml
<?xml version="1.0" encoding="UTF-8"?>
<osm version="0.6" generator="OpenStreetMap server" copyright="OpenStreetMap and contributors" attribution="http://www.openstreetmap.org/copyright" license="http://opendatacommons.org/licenses/odbl/1-0/">
  <api>
    <version minimum="0.6" maximum="0.6"/>
    <area maximum="0.25"/>
    <note_area maximum="25"/>
    <tracepoints per_page="5000"/>
    <waynodes maximum="2000"/>
    <changesets maximum_elements="10000"/>
    <timeout seconds="300"/>
    <status database="online" api="online" gpx="online"/>
  </api>
  <policy>
    <imagery>
      <blacklist regex=".*\.google(apis)?\..*/(vt|kh)[\?/].*([xyz]=.*){3}.*"/>
      <blacklist regex="http://xdworld\.vworld\.kr:8080/.*"/>
      <blacklist regex=".*\.here\.com[/:].*"/>
    </imagery>
  </policy>
</osm>
```

- 版权，所有权和许可：参考法律信息

API：

- 最低和最高版本是服务器将接受的API调用版本。
- 面积最大值是API调用可以查询的最大面积（以平方度为单位）。
- 每页跟踪点是单个GPS跟踪中的最大点数。（可能不正确）
- 航路点最大值是一条路可能包含的最大节点数。
- 变更集的最大值是变更集中可以包含的最大组合节点，方式和关系的数量。
- status元素针对每个数据库，API和GPX API 返回*online*，*readonly*或*offline*。数据库字段是信息性字段，API / GPX-API字段指示客户端是希望读取和写入请求正常工作（*联机*），还是希望读取请求正常工作（*只读*）还是不希望读取请求正常工作（*脱机*）。

政策：

- 图像黑名单列出了所有航空和地图资源，由于版权原因，OSM不允许使用这些资源。编辑者不得将这些资源显示为背景层。

笔记:

- 请注意，URL是无版本的。为了方便起见，服务器也支持请求`/api/0.6/capabilities`，以便客户端可以对所有请求使用相同的URL前缀`http：/.../ api / 0.6`。
- 元素和关系成员ID当前依赖于实现（仅限于64位带符号整数），这应该不是问题：-)。

#### `/api/0.6/map`

通过边界框检索地图数据

以下命令返回：

- 给定边界框内的所有节点以及引用它们的任何关系。
- 所有引用至少一个给定边界框内的节点的方法，引用它们的所有关系（这些方法）以及该方法可以引用的边界框外的任何节点。
- 由于上述规则，引用了其中一个节点，方式或关系的所有关系。（不会**不**递归应用，见下面的说明。）

具体URL

```url
GET /api/0.6/map?bbox=left,bottom,right,top
```

- `*left*` 是边框左侧（最西端）的经度。
- `*bottom*` 是边界框底部（最南端）的纬度。
- `*right*` 是边界框的右侧（最东端）的经度。
- `*top*` 是边界框顶部（最北端）的纬度。

##### 错误码

###### HTTP状态码400（错误请求）

当超过任何节点/路径/关系限制时，尤其是如果调用将返回超过50000个节点时。有关此代码的其他用途，请参见上文。

###### HTTP状态码509（超出带宽限制）

“错误：您下载了太多数据。请稍后重试。

#### `/api/0.6/permissions`

检索权限

返回授予当前API连接的权限。

- 如果未授权API客户端，则将返回空白权限列表。
- 如果API客户端使用基本身份验证，则权限列表将包含所有权限。
- 如果API客户端使用OAuth，则该列表将包含用户实际授予的权限。

```url
GET /api/0.6/permissions
```

##### 响应

返回包含许可标签的单个许可元素

```xml
 <?xml version="1.0" encoding="UTF-8"?>
 <osm version="0.6" generator="OpenStreetMap server">
   <permissions>
     <permission name="allow_read_prefs"/>
     ...
     <permission name="allow_read_gpx"/>
     <permission name="allow_write_gpx"/>
   </permissions>
 </osm>
 
```

当前，以下权限可以出现在结果中，与OAuth应用程序定义中使用的权限直接对应：

- allow_read_prefs（读取用户首选项）
- allow_write_prefs（修改用户首选项）
- allow_write_diary（创建日记条目，评论并结交朋友）
- allow_write_api（修改地图）
- allow_read_gpx（读取专用GPS跟踪）
- allow_write_gpx（上传GPS跟踪）
- allow_write_notes（修改注释）

#### `api/0.6/changeset/#id?include_discussion=true`

以OSM-XML格式返回具有给定`ID`的变更集。

```xml
<osm>
  <changeset id="10" user="fred" uid="123" created_at="2008-11-08T19:07:39+01:00" open="true" min_lon="7.0191821" min_lat="49.2785426" max_lon="7.0197485" max_lat="49.2793101">
    <tag k="created_by" v="JOSM 1.61"/>
    <tag k="comment" v="Just adding some streetnames"/>
    ...
    <discussion>
     <comment date="2015-01-01T18:56:48Z" uid="1841" user="metaodi">
       <text>Did you verify those street names?</text>
     </comment>
     <comment date="2015-01-01T18:58:03Z" uid="123" user="fred">
       <text>sure!</text>
     </comment>
     ...
   </discussion>
 </changeset>
</osm>

```

##### 参量

###### ID

要检索的变更集的ID

###### （**新**）include_discussion

指示结果是否应包含变更集讨论。如果将此参数设置为任何值，则返回讨论。如果为空或省略，则不会进行讨论。

##### 响应

返回包含更改集标记的单个更改集元素，其内容类型为`text / xml`

##### 错误码

###### HTTP状态码404（未找到）



如果找不到具有给定ID的变更集

- 该`UID`自动通过API V0.5到V0.6 API产生的过渡可能无法使用的变更？
- 空的变更集将缺少边界框属性。
- 变更集边界框是一个矩形，其中包含在此变更集中更改的所有对象的边界框。不一定是最小的矩形。
- 此API调用仅返回有关变更集本身的信息，而不返回对该变更集中的元素进行的实际更改。要访问此信息，请使用*下载* API调用。

#### `/api/0.6/changeset/#id/download`

下载返回描述与变更集关联的所有变更的[OsmChange](https://wiki.openstreetmap.org/wiki/OsmChange)文档。

##### 参量

###### ID

请求OsmChange的变更集的ID。

##### 响应

内容类型为`text / xml`的OsmChange XML 。

##### 错误码

###### HTTP状态码404（未找到）



如果找不到具有给定ID的变更集

- 只要变更集是打开的，调用此方法的结果就可能会更改。
- OsmChange中的元素按时间戳和版本号排序。

#### `/api/0.6/changesets`

这是用于查询变更集的API方法。它支持按不同条件查询。

在给出多个查询的地方，结果将是那些符合所有要求的查询。返回文档的内容是变更集及其标签。要获取与变更集关联的完整变更集，请分别对每个变更集ID 使用*下载*方法。

为了支持回退和我们发现的变更集的其他用途，可能需要对上述基本查询进行修改和扩展。

该调用最多返回100个符合条件的变更集，它返回created_at [[1\]](https://wiki.openstreetmap.org/wiki/API_v0.6#cite_note-1)排序的最新变更集。

##### 参量

###### bbox = min_lon，min_lat，max_lon，max_lat（W，S，E，N）

在给定的边界框中查找变更集

###### user =＃uid **或** display_name =＃name

使用给定的用户ID或显示名称按用户查找变更集。两者都提供是错误的。

###### time= T1

查找在T1之后*关闭的*变更集

###### time= T1，T2

查找在T1之后*关闭*并在T2之前*创建的*变更集。换句话说，在给定时间范围T1至T2内*某个时间*打开的任何变更集。

###### open=true

仅查找仍处于*打开状态的*变更集，但不包括已关闭或已达到变更集元素限制的变更集（目前为[[](https://wiki.openstreetmap.org/wiki/API_v0.6#cite_note-2) 10.000 []](https://wiki.openstreetmap.org/wiki/API_v0.6#cite_note-2) 10.000 ）

###### 已closed=true

仅查找已*关闭*或已达到元素限制的变更集

###### changesets =＃cid {，＃cid}

查找具有指定ID的变更集（自[2013-12-05开始](https://github.com/openstreetmap/openstreetmap-website/commit/1d1f194d598e54a5d6fb4f38fb569d4138af0dc8)）

时间格式：[此Ruby函数](http://www.ruby-doc.org/stdlib/libdoc/date/rdoc/DateTime.html#method-c-parse)将解析的任何内容。默认值是'-4712-01-01T00：00：00 + 00：00'; 这是朱利安天数第0天。

##### 响应

返回按创建日期排序的所有变更集的列表。所述`<OSM>`如果有任何结果的查询元素可以是空的。响应以`text / xml`的内容类型发送。

##### 错误码

###### HTTP状态码400（错误请求）- `文本/纯文本`

参数格式错误。返回一条解释错误的文本消息。特别是，尝试同时提供UID和显示名称作为用户查询参数将导致此错误。

###### HTTP状态码404（未找到）

如果找不到具有给定`uid`或`display_name的`用户。

##### 笔记

- 仅返回公共用户的变更集。
- 最多返回100个变更集

#### `/api/0.6/[node|way|relation]/#id`

返回元素的XML表示形式。

##### 例

**/api/0.6/node/12345**

将获得ID为12345的节点

##### 响应

表示元素的XML，包装在`<osm>`元素中：

```xml
<osm>
 <node id="123" lat="..." lon="..." version="142" changeset="12" user="fred" uid="123" visible="true" timestamp="2005-07-30T14:27:12+01:00">
   <tag k="note" v="Just a node"/>
   ...
 </node>
</osm>
```

##### 错误码

###### HTTP状态码404（未找到）

当找不到具有给定id的元素时

###### HTTP状态码410（已消失）

如果该元素已被删除

#### `/api/0.6/[node|way|relation]/#id/history`

历史记录

检索元素的所有旧版本。

##### 错误码

- HTTP状态码404（未找到）

  当找不到具有给定id的元素时

#### `/api/0.6/[node|way|relation]/#id/#version`

检索元素的特定版本。

##### 错误码

###### HTTP状态码403（禁止）

当元素的版本不可用时（由于编辑）

###### HTTP状态码404（未找到）

当找不到具有给定id的元素时

#### `/api/0.6/[nodes|ways|relations]?#parameters`

允许用户一次获取多个元素。

**[nodes|ways|relations]=comma separated list**

URL中的参数必须相同（例如/api/0.6/nodes?nodes=123,456,789）

可以选择在小写的“ v”字符后提供每个对象的版本号，例如/api/0.6/nodes?nodes=421586779v1,421586779v2

##### 错误码

###### HTTP状态码400（错误请求）

请求格式错误（参数缺失或错误）

###### HTTP状态码404（未找到）

如果找不到元素之一（通过“未找到”表示数据库中不存在该元素，如果删除了该对象，则将返回该对象，并带有visible =“ false”属性）

###### HTTP状态码414（请求URI太大）

如果URI太长（在未指定版本的情况下，URI中的字符数大于8213，对于10位ID的字符数大于725）

#### `/api/0.6/[node|way|relation]/#id/relations`

返回一个XML文档，其中包含使用给定元素的所有（未删除）关系。

##### 笔记

- 如果元素不存在，则没有错误。
- 如果该元素不存在或未在任何关系中使用，则返回一个空XML文档（除了`<osm>`元素之外）

#### `/api/0.6/node/#id/ways`

返回一个XML文档，其中包含使用给定节点的所有（未删除）方法。

##### 笔记

- 如果该节点不存在，则没有错误。
- 如果该节点不存在或未以任何方式使用，则返回一个空的XML文档（除了`<osm>`元素之外）

#### `/api/0.6/[way|relation]/#id/full`

此API调用检索方式或关系以及它引用的所有其他元素

- 在某种程度上，它将返回指定的方式以及该方式引用的所有节点的完整XML。
- 对于关系，它将返回以下内容：
  - 关系本身
  - 关系的所有节点，方式和关系
  - 加上上一步中使用的所有节点
  - 相同的递归逻辑不适用于关系。这意味着：如果关系r1包含方式w1和关系r2，并且w1包含节点n1和n2，而r2包含节点n3，则对r1的“完整”请求将为您提供r1，r2，w1，n1和n2。不是n3。

##### 错误码

###### HTTP状态码404（未找到）

当找不到具有给定id的元素时

###### HTTP状态码410（已消失）

如果该元素已被删除

#### `/api/0.6/trackpoints?bbox=left,bottom,right,top&page=pageNumber`

使用它来检索给定边界框内的GPS跟踪点（格式为GPX格式）。

哪里：

- `*left*`，`*bottom*`，`*right*`，并`*top*`使用相同的方法，因为它们是在命令检索节点，方式和关系。
- `*pageNumber*`指定要返回的每组5,000点或*页面*。由于该命令一次返回的点数不超过5,000，因此必须递增此参数，并再次发送命令（使用相同的边界框），以便检索包含5,000以上的边界框的所有点点。当该参数为0（零）时，该命令返回前5,000个点；当它为1时，该命令返回点5001-1000，依此类推。

##### 例子

检索边界框的前5,000个点：

```
https://api.openstreetmap.org/api/0.6/trackpoints?bbox=0,51.5,0.25,51.75&page=0
```

检索相同边界框的下5,000个点（5,001–10,000点）：

```
https://api.openstreetmap.org/api/0.6/trackpoints?bbox=0,51.5,0.25,51.75&page=1
```

##### 响应

- 此响应未包装在Osm Xml父元素中。
- 文件格式为GPX 1.0版，而不是当前版本。验证您的工具是否支持它。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.0" creator="OpenStreetMap.org" xmlns="http://www.topografix.com/GPX/1/0">
	<trk>
		<name>20190626.gpx</name>
		<desc>Footpaths near Blackweir Pond, Epping Forest</desc>
		<url>https://api.openstreetmap.org/user/John%20Leeming/traces/3031013</url>
		<trkseg>
			<trkpt lat="51.6616100" lon="0.0534560">
				<time>2019-06-26T14:27:58Z</time>
			</trkpt>
			...
		</trkseg>
		...
	</trk>
	...
</gpx>
```

#### `/api/0.6/gpx/#id/details`

下载元数据

使用它来访问有关GPX文件的元数据。如果文件标记为公共，则无需身份验证即可使用。否则，只能由所有者帐户使用，并且需要HTTP基本身份验证。

示例“详细信息”响应：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<osm version="0.6" generator="OpenStreetMap server">
	<gpx_file id="836619" name="track.gpx" lat="52.0194" lon="8.51807" user="Hartmut Holzgraefe" visibility="public" pending="false" timestamp="2010-10-09T09:24:19Z">
		<description>PHP upload test</description>
		<tag>test</tag>
		<tag>php</tag>
	</gpx_file>
</osm>
```

#### `/api/0.6/gpx/#id/data`

下载数据

使用此下载完整的GPX文件。如果文件标记为公共，则无需身份验证即可使用。否则，只能由所有者帐户使用并需要认证。

响应将是上载的确切文件。

#### `/api/0.6/user/gpx_files`

使用它来获取已认证用户拥有的GPX跟踪列表：需要认证。
示例“详细信息”响应：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<osm version="0.6" generator="OpenStreetMap server">
	<gpx_file id="836619" name="track.gpx" lat="52.0194" lon="8.51807" user="Hartmut Holzgraefe" visibility="public" pending="false" timestamp="2010-10-09T09:24:19Z">
		<description>PHP upload test</description>
		<tag>test</tag>
		<tag>php</tag>
	</gpx_file>
</osm>
```

#### `/api/0.6/notes/feed`

获取区域内注释的RSS提要。

**网址：** `https://api.openstreetmap.org/api/0.6/notes/feed?bbox=*left*,*bottom*,*right*,*top*`

**返回类型：** application / xml

### PUT

#### `/api/0.6/changeset/create`

变更集创建请求的有效负载是此变更集的元数据。请求的主体必须包含一个或多个`changeset`元素，这些元素可选地包含任意数量的标签（例如'comment'，'created_by'，...）。所有`changeset`元素都必须包含在`osm`元素中。

```xml
<osm>
  <changeset>
    <tag k="created_by" v="JOSM 1.61"/>
    <tag k="comment" v="Just adding some streetnames"/>
    ...
  </changeset>
  ...
</osm>
```

##### 响应

新创建的变更集的ID，其内容类型为`text / plain`

##### 错误码

###### HTTP状态码400（错误请求）

解析XML时出现错误

###### HTTP状态码405（不允许使用方法）

如果该请求不是HTTP PUT请求

#### `/api/0.6/changeset/#id`

用于更新变更集上的标签，例如变更集`comment=foo`.

有效负载应为OSM文档，其中包含单个变更集的新版本。边界框，更新时间和其他属性将被忽略，并且无法通过此方法进行更新。仅此调用中提供的那些标记保留在变更集对象中。有关更新边界框的信息，请参见*expand_bbox*方法。

```
<osm>
  <changeset>
    <tag k="comment" v="Just adding some streetnames and a restaurant"/>
  </changeset>
</osm>
```

##### 参量

###### ID

要更新的变更集的ID。发出此API调用的用户必须与创建变更集的用户相同

##### 响应

包含变更集新版本的OSM文档，其内容类型为`text / xml`

##### 错误码

###### HTTP状态码400（错误请求）

解析XML时出现错误

###### HTTP状态码404（未找到）如果找不到具有给定ID的变更集

###### HTTP状态码405（不允许使用方法）

如果该请求不是HTTP PUT请求

###### HTTP状态码409（冲突）- `文本/纯文本`

如果所讨论的变更集已被关闭（由用户本身或由于自动关闭功能而关闭）。返回以下格式的消息：“ `变更集#id在#closed_at已关闭。` ”

或者，如果尝试更新变更集的用户与创建变更集的用户不同



> 不变的标签必须重复才能不被删除。

#### `/api/0.6/changeset/#id/close`

关闭变更集。更改集可能已经关闭，而所有者没有发出此API调用。在这种情况下，将返回错误代码。

##### 参量

###### ID

要关闭的变更集的ID。发出此API调用的用户必须与创建变更集的用户相同。

##### 响应

成功关闭变更集后，未返回任何内容（HTTP状态代码200）

##### 错误码

###### HTTP状态码404（未找到）

如果找不到具有给定ID的变更集

###### HTTP状态码405（不允许使用方法）

如果该请求不是HTTP PUT请求

###### HTTP状态码409（冲突）- `文本/纯文本`

如果所讨论的变更集已被关闭（由用户本身或由于自动关闭功能而关闭）。返回以下格式的消息：“ `变更集#id在#closed_at已关闭。` ”

或者，如果尝试更新变更集的用户与创建变更集的用户不同

#### ` /api/0.6/[node|way|relation]/create`

创建指定类型的新元素。请注意，整个请求应包装在

```xml
<osm>...</osm>
```

element.

A Node:

```xml
<osm>
 <node changeset="12" lat="..." lon="...">
   <tag k="note" v="Just a node"/>
   ...
 </node>
</osm>
```

A Way:

```xml
<osm>
 <way changeset="12">
   <tag k="note" v="Just a way"/>
   ...
   <nd ref="123"/>
   <nd ref="4345"/>
   ...
 </way>
</osm>
```

A Relation:

```xml
<osm>
 <relation changeset="12">
   <tag k="note" v="Just a relation"/>
   ...
   <member type="node" role="stop" ref="123"/>
   <member type="way" ref="234"/>
 </relation>
</osm>
```

如果提供了多个元素，则仅创建第一个。其余的将被丢弃（此行为与更改集的创建不同）。

##### 响应

新创建的元素的ID（内容类型为`text / plain`）

##### 错误码

###### HTTP状态码400（错误请求）- `文本/纯文本`

解析XML时出现错误。返回一条解释错误的文本消息。

缺少变更集ID时（不幸的是，错误消息不一致）

当节点在世界之外

当节点太多时

###### HTTP状态码405（不允许使用方法）

如果该请求不是HTTP PUT请求

###### HTTP状态码409（冲突）- `文本/纯文本`

如果所讨论的变更集已被关闭（由用户本身或由于自动关闭功能而关闭）。返回以下格式的消息：“ `变更集#id在#closed_at已关闭。` ”

或者，如果尝试更新变更集的用户与创建变更集的用户不同

###### HTTP状态代码412（前提条件失败）

当一种方式具有不存在或不可见的（即删除）节点：“ `对路＃（编号）要求（＃{} missing_ids），它要么不存在，要么是不可见的id为节点` ”

当一个关系中的元素不存在或不可见时：“ `由于ID为＃{element}且ID为＃{element.id}的ID为＃{id}的关系无法保存` ”

##### 笔记

- 这将更新更改集的边界框。
- 关系的*角色*属性是可选的。默认为空字符串。

#### `/api/0.6/[node|way|relation]/#id`

从现有元素更新数据。必须提供更新后元素的完整表示形式。任何仍保持不变的标签，路节点引用和关系成员也必须在更新中。还必须提供版本号。

##### 响应

返回内容类型为`text / plain`的新版本号。

##### 错误码

###### HTTP状态码400（错误请求）- `文本/纯文本`

解析XML时出现错误。返回一条解释错误的文本消息。如果您忘记传递Content-Length标头，也会发生这种情况。

缺少变更集ID时（不幸的是，错误消息不一致）

当节点在世界之外

当节点太多时

当提供的元素的版本与该元素的当前数据库版本不匹配时

###### HTTP状态码409（冲突）- `文本/纯文本`

如果所讨论的变更集已被关闭（由用户本身或由于自动关闭功能而关闭）。返回以下格式的消息：“ `变更集#id在#closed_at已关闭。` ”

或者，如果尝试更新变更集的用户与创建变更集的用户不同

###### HTTP状态码404（未找到）

当找不到具有给定id的元素时

###### HTTP状态代码412（前提条件失败）

当一种方式具有不存在或不可见的（即删除）节点：“ `对路＃（编号）要求（＃{} missing_ids），它要么不存在，要么是不可见的id为节点` ”

当一个关系中的元素不存在或不可见时：“ `由于ID为＃{element}且ID为＃{element.id}的ID为＃{id}的关系无法保存` ”

#### 笔记

- 这将更新更改集的边界框。

#### `/api/0.6/gpx/#id`

使用此更新GPX文件。仅可由所有者帐户使用。需要验证。
响应主体将为空。

### POST

#### `/api/0.6/changeset/#id/upload`

使用此API，可以将[OsmChange](https://wiki.openstreetmap.org/wiki/OsmChange)格式的文件上传到服务器。这保证可以在事务中运行。因此，要么所有更改都应用，要么不应用。

要上传OSC文件，它必须符合[OsmChange](https://wiki.openstreetmap.org/wiki/OsmChange)规范，但有以下区别：

- 每个元素都必须带有一个*changeset*和一个*version*属性，除非要创建一个不需要版本的元素，因为服务器会为您设置版本。的*变更*必须是一样的变更ID被上传到。

- OsmChange文档中的<delete>块可能具有*if-unused*属性（其值将被忽略）。如果存在此属性，则此块中的删除操作是有条件的，并且仅在要删除的对象未被另一个对象使用时才执行。没有*if-unused*，这种情况将导致错误，并且整个差异上传将失败。设置该属性还将导致已删除对象的删除不会产生错误。

- [OsmChange](https://wiki.openstreetmap.org/wiki/OsmChange)文档通常在每个元素上都有*用户*和*uid*属性。上传到API的文档中不需要这些。

##### 参量

###### ID

该差异所属变更集的ID。

###### 发布数据

OsmChange文件数据

##### 响应

如果成功应用差异，则将以以下格式返回XML（内容类型`text / xml`）

```xml
<diffResult generator="OpenStreetMap Server" version="0.6">
  <node|way|relation old_id="#" new_id="#" new_version="#"/>
  ...
</diffResult>
```

上载中的每个元素都有一个元素。请注意，当同一元素在输入中出现多次，然后在输出中出现多次时，这可能是违反直觉的。

|  属性  |        创建        |    修改    |  删除  |
| :----: | :----------------: | :--------: | :----: |
| old_id | 与上载的元素相同。 |            |        |
| new_id |        新ID        | 与上载相同 | 不存在 |
| 新版本 |       新版本       |   不存在   |        |

##### 错误码

###### HTTP状态码400（错误请求）- `文本/纯文本`

解析XML时出现错误。返回一条解释错误的文本消息。

当占位符ID缺失或不唯一时（对于循环关系引用，将发生这种情况）

###### HTTP状态码404（未找到）

如果找不到具有给定ID的变更集

或当差异包含无法找到给定ID的元素时

###### HTTP状态码405（不允许使用方法）

如果该请求不是HTTP POST请求

###### HTTP状态码409（冲突）- `文本/纯文本`

如果所讨论的变更集已被关闭（由用户本身或由于自动关闭功能而关闭）。返回以下格式的消息：“ `变更集#id在#closed_at已关闭。` ”

如果上传时最大 超出变更集的大小。返回以下格式的消息：“ `变更集#id在#closed_at已关闭。` ”

或者，如果尝试更新变更集的用户与创建变更集的用户不同

或者，如果差异包含具有变更集ID的元素，而该元素的变更集ID与差异上传到的变更集ID不匹配

或由于其中一个元素的创建，更新或删除操作而可能发生的任何错误消息

###### 其他状态码

由于其中一个元素的创建，更新或删除操作而可能发生的任何错误代码和相关消息

请参阅此页面中的相应部分

##### 笔记

- 处理在第一个错误处停止，因此，如果一个差异上传中存在多个冲突，则仅报告第一个问题。
- 有关更改集中允许的最大更改数量，请参考`/api/capabilities`-> *更改集* -> *maximum_elements*。
- 目前，Rails端口上的差异大小没有限制。CGImap将差异大小限制为50MB（未压缩大小）。
- 不允许向前引用占位符ID，API会拒绝该引用。

#### `/api/0.6/changeset/#id/subscribe`

订阅

订阅有关变更集的讨论，以接收有关新评论的通知。


**网址：** `https://api.openstreetmap.org/api/0.6/changeset/#id/subscribe `（[例如](https://api.openstreetmap.org/api/0.6/changeset/1000/subscribe)）
**返回类型：** application / xml

此请求需要以经过身份验证的用户身份完成。

##### 错误码

- HTTP状态码409（冲突）

  如果用户已经订阅了此变更集

#### `/api/0.6/changeset/#id/unsubscribe`

取消订阅变更集的讨论以停止接收通知。


**网址：** `https://api.openstreetmap.org/api/0.6/changeset/#id/unsubscribe `（[例如](https://api.openstreetmap.org/api/0.6/changeset/1000/unsubscribe)）
**返回类型：** application / xml

此请求需要以经过身份验证的用户身份完成。

##### 错误码

- HTTP状态码404（未找到）

  如果用户未订阅此变更集

##### 元素

OpenStreetMap中的所有三个基本元素（*Nodes*，*Ways*和*Relations*）都有创建，读取，更新和删除调用。这些调用非常相似，除了有效负载和一些特殊的错误消息，因此仅记录了一次。

#### `/api/0.6/[node|way|relation]/#id/#version/redact?redaction=#redaction_id`

这是最初为[ODbL许可证更改](https://wiki.openstreetmap.org/wiki/Open_Database_License)创建的API方法，以隐藏不接受新CT /许可证的用户的贡献。[DWG](https://wiki.openstreetmap.org/wiki/Data_working_group)现在使用它来隐藏包含数据隐私或侵犯版权的元素的旧版本。对元素#version的所有API检索请求都将返回HTTP错误403。

##### 笔记

- 仅允许具有主持人角色的OSM帐户（DWG和服务器管理员）使用
- \#redaction_id列在<https://www.openstreetmap.org/redactions>
- 可以在[源代码中](https://git.openstreetmap.org/rails.git/blob/HEAD:/app/controllers/old_controller.rb)找到更多信息
- 这是一个非常专业的电话

##### 错误代码

###### HTTP状态码400（错误请求）

“无法删除元素的当前版本，只能删除历史版本。”

#### `/api/0.6/gpx/create`

使用此文件上载GPX文件或GPX文件的存档。需要验证。

multipart / form-data HTTP消息中需要以下参数：

|  参数  |                             描述                             |
| :----: | :----------------------------------------------------------: |
|  文件  | 包含跟踪点的GPX文件。请注意，要成功处理，文件必须不仅包含航路点，还必须包含跟踪点（<trkpt>），并且跟踪点必须具有有效的时间戳。由于文件是异步处理的，因此即使无法处理文件，调用也将成功完成。该文件也可以是包含多个gpx文件的.tar，.tar.gz或.zip，尽管它将在上载日志中显示为单个条目。 |
|  描述  |                          跟踪描述。                          |
|  标签  |                    包含跟踪标记的字符串。                    |
|  上市  | 如果跟踪是公共的，则为1；否则为0。存在此属性仅是为了向后兼容-现在应改为使用可见性参数。如果还提供可见性，则将忽略此值。 |
| 能见度 | 以下之一：私有，公共，可跟踪，可识别（有关说明，请参见[OSM跟踪上载页面](https://www.openstreetmap.org/traces/mine)或[GPS跟踪的可见性](https://wiki.openstreetmap.org/wiki/Visibility_of_GPS_traces)） |

响应：

一个数字，代表新gpx的ID

### DELETE

#### ` /api/0.6/[node|way|relation]/#id`

期望要删除元素的有效XML表示形式。

```xml
<osm>
 <node id="..." version="..." changeset="..." lat="..." lon="..." />
</osm>
```

如果XML中的节点ID必须与URL中的ID相匹配，则版本必须与您下载的元素的版本相匹配，并且变更集必须与当前经过身份验证的用户拥有的开放变更集的ID相匹配。允许但不是必须在元素上具有标签，除了必需的经/纬标记外，如果没有纬/经，服务器会发出400错误的请求。

##### 响应

返回内容类型为`text / plain`的新版本号。

##### 错误码

###### HTTP状态码400（错误请求）- `文本/纯文本`

解析XML时出现错误。返回一条解释错误的文本消息。

缺少变更集ID时（不幸的是，错误消息不一致）

当节点在世界之外

当节点太多时

当提供的元素的版本与该元素的当前数据库版本不匹配时

###### HTTP状态码404（未找到）

当找不到具有给定id的元素时

###### HTTP状态码409（冲突）- `文本/纯文本`

如果所讨论的变更集已被关闭（由用户本身或由于自动关闭功能而关闭）。返回以下格式的消息：“ `变更集#id在#closed_at已关闭。` ”

或者，如果尝试更新变更集的用户与创建变更集的用户不同

###### HTTP状态码410（已消失）

如果该元素已经被删除

###### HTTP状态代码412（前提条件失败）

当仍然以某种方式使用`节点时`：`节点＃{id}仍以方式＃{way.id}使用。`

当节点仍然是关系的成员时：`关系＃{relation.id}仍使用节点＃{id}。`

当方法仍然是关系的成员时：`关系＃{relation.id}仍使用方法＃{id}。`

当一个关系仍是另一个关系的成员时：`在关系＃{relation.id}中使用关系＃{id}。`



请注意，由于OsmChange上载操作而返回时，错误消息包含虚假的复数“ s”，例如“ ...仍按方式使用...”，“ ...仍按关系使用...”，即使仅返回一种方式或关系ID，因为这意味着如果删除的对象是/是多个父对象的成员，则可以返回多个ID，这些ID用逗号分隔。

##### 笔记

- 在早期的API版本中，不需要有效负载。由于需要变更集ID和版本号，因此现在需要它

#### `/api/0.6/gpx/#id`

使用此删除GPX文件。仅可由所有者帐户使用。需要验证。
响应主体将为空。