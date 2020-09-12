
### 项目配置方式

#### maven

> 编辑pom.xml,增加以下片段



```xml
   <repositories>
        <repository>
            <id>aliyunmaven</id>
            <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
        </repository>
    </repositories>
```

#### gradle

> 编辑build.gradle



```rust
 repositories {
        maven{ url 'http://maven.aliyun.com/nexus/content/groups/public/'}
        mavenCentral()
    }
```

### 全局配置方式

#### maven

> conf/settings.xml下mirros节点增加



```xml
<mirror>  
  <id>alimaven</id>  
  <name>aliyun maven</name>  
  <url>http://maven.aliyun.com/nexus/content/groups/public/</url>;  
  <mirrorOf>central</mirrorOf>          
</mirror>
```

#### gradle

> ```
> .gradle`目录下创建文件`init.gradle
> ```



```ruby
allprojects{
    repositories {
        def ALIYUN_REPOSITORY_URL = 'http://maven.aliyun.com/nexus/content/groups/public'
        def ALIYUN_JCENTER_URL = 'http://maven.aliyun.com/nexus/content/repositories/jcenter'
        all { ArtifactRepository repo ->
            if(repo instanceof MavenArtifactRepository){
                def url = repo.url.toString()
                if (url.startsWith('https://repo1.maven.org/maven2')) {
                    project.logger.lifecycle "Repository ${repo.url} replaced by $ALIYUN_REPOSITORY_URL."
                    remove repo
                }
                if (url.startsWith('https://jcenter.bintray.com/')) {
                    project.logger.lifecycle "Repository ${repo.url} replaced by $ALIYUN_JCENTER_URL."
                    remove repo
                }
            }
        }
        maven {
            url ALIYUN_REPOSITORY_URL
            url ALIYUN_JCENTER_URL
        }
    }
}
```

