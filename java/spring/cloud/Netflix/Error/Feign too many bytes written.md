导入依赖

```xml
<!-- https://mvnrepository.com/artifact/io.github.openfeign/feign-httpclient -->
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-httpclient</artifactId>
    <version>10.7.4</version>
</dependency>

```

application.yaml配置

```yaml
feign:
	httpclient:
		enable: true
```

