1. 下载Gradlehttps://services.gradle.org/distributions/
2. 配置Gradle环境变量
3. 下载AspectJ（Spring实现了AOP）https://www.eclipse.org/aspectj/downloads.php
4. java -jar aspectj.jar
5. export path
6. 下载spring源码https://github.com/spring-projects/spring-framework|https://gitee.com/mirrors/Spring-Framework
7. 运行源码目录下的gradlew.bat
8. gradlew :spring-oxm:compileTestJava
9. gradlew :spring-core:compileTestJava
10. idea源码导入







> plugin with id 'java-test-fixtures' not found；
>
> 
>
> Gradle构建spring5.1源码
>
> Gradle版本不得低于5.6，不得高于6.0



> Gradle jar包下载缓慢
>
> 1. 在Gradle根目录`.\init.d\`下，新建`init.gradle`文件
>
>    ```groovy
>    allprojects{
>       repositories {
>           def REPOSITORY_URL = 'http://maven.aliyun.com/nexus/content/groups/public/'
>           all { ArtifactRepository repo ->
>               def url = repo.url.toString()
>               if ((repo instanceof MavenArtifactRepository) && (url.startsWith('https://repo1.maven.org/maven2') || url.startsWith('https://jcenter.bintray.com'))) {
>                   project.logger.lifecycle 'Repository ${repo.url} replaced by $REPOSITORY_URL .'
>                   remove repo            }
>           }
>           maven {
>               url REPOSITORY_URL        }
>       }}
>    
>    ```
>
>    
>
>    2. 修改源码目录下的`build.gradle`
>
>    ```groovy
>    allprojects {
>    	repositories {
>    		maven{ url 'http://maven.aliyun.com/nexus/content/groups/public/'}
>    	}
>    }
>    
>    ```
>
>    