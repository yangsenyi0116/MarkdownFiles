```groovy
//应用于gradle编译
buildscript {
    //定义
    ext{
        springBootVersion = "2.1.6.RELEASE"
    }
    repositories {
        maven {
            url 'http://maven.aliyun.com/nexus/content/groups/public/'
        }
        //maven中央仓库
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    }
}

plugins {
    id 'org.springframework.boot' version '2.3.0.RELEASE'
    id 'io.spring.dependency-management' version '1.0.9.RELEASE'
    id 'java'
}

group = 'com.kermi'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

repositories {
    mavenCentral()
}

dependencies {
    compile 'org.springframework.boot:spring-boot-starter'
    compile 'org.projectlombok:lombok'
    // Mysql依赖
    compile 'mysql:mysql-connector-java:8.0.18'
    // Security依赖
    compile 'org.springframework.boot:spring-boot-starter-security'
    // Mybatis-plus核心库
    compile 'com.baomidou:mybatis-plus-boot-starter:3.1.0'
    // 引入阿里数据库连接池
    compile 'com.alibaba:druid:1.1.6'
    // StringUtilS工具
    compile 'org.apache.commons:commons-lang3:3.5'
    // JSON工具
    compile 'com.alibaba:fastjson:1.2.45'
    //JWT依赖
    compile 'org.springframework.security:spring-security-jwt:1.0.9.RELEASE'
    compile 'io.jsonwebtoken:jjwt:0.9.0'

    testImplementation('org.springframework.boot:spring-boot-starter-test') {
        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
    }
}
test {
    useJUnitPlatform()
}
```

