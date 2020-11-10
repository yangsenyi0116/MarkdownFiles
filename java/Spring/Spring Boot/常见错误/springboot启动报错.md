> # java.lang.NoSuchMethodError: javax.servlet.ServletContext.getVirtualServerName()Ljava



完整报错信息：

```

2019-09-26 18:47:15.107 ERROR [main] o.apache.catalina.core.ContainerBase - A child container failed during start
java.util.concurrent.ExecutionException: org.apache.catalina.LifecycleException: Failed to start component [NonLoginAuthenticator[StandardEngine[Tomcat].StandardHost[localhost].TomcatEmbeddedContext[]]]
    at java.util.concurrent.FutureTask.report(FutureTask.java:122)
    at java.util.concurrent.FutureTask.get(FutureTask.java:192)
    at org.apache.catalina.core.ContainerBase.startInternal(ContainerBase.java:916)
    at org.apache.catalina.core.StandardHost.startInternal(StandardHost.java:841)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1384)
    at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1374)
    at java.util.concurrent.FutureTask.run(FutureTask.java:266)
    at org.apache.tomcat.util.threads.InlineExecutorService.execute(InlineExecutorService.java:75)
    at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
    at org.apache.catalina.core.ContainerBase.startInternal(ContainerBase.java:909)
    at org.apache.catalina.core.StandardEngine.startInternal(StandardEngine.java:262)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    at org.apache.catalina.core.StandardService.startInternal(StandardService.java:421)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    at org.apache.catalina.core.StandardServer.startInternal(StandardServer.java:932)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    at org.apache.catalina.startup.Tomcat.start(Tomcat.java:459)
    at org.springframework.boot.web.embedded.tomcat.TomcatWebServer.initialize(TomcatWebServer.java:105)
    at org.springframework.boot.web.embedded.tomcat.TomcatWebServer.<init>(TomcatWebServer.java:86)
    at org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory.getTomcatWebServer(TomcatServletWebServerFactory.java:416)
    at org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory.getWebServer(TomcatServletWebServerFactory.java:180)
    at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.createWebServer(ServletWebServerApplicationContext.java:180)
    at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.onRefresh(ServletWebServerApplicationContext.java:153)
    at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:543)
    at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:141)
    at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:744)
    at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:391)
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:312)
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:1215)
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:1204)
    at com.DemoApplication.main(DemoApplication.java:23)
Caused by: org.apache.catalina.LifecycleException: Failed to start component [NonLoginAuthenticator[StandardEngine[Tomcat].StandardHost[localhost].TomcatEmbeddedContext[]]]
    at org.apache.catalina.util.LifecycleBase.handleSubClassException(LifecycleBase.java:440)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:198)
    at org.apache.catalina.core.StandardPipeline.startInternal(StandardPipeline.java:181)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:5063)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1384)
    at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1374)
    at java.util.concurrent.FutureTask.run(FutureTask.java:266)
    at org.apache.tomcat.util.threads.InlineExecutorService.execute(InlineExecutorService.java:75)
    at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
    at org.apache.catalina.core.ContainerBase.startInternal(ContainerBase.java:909)
    ... 29 common frames omitted
Caused by: java.lang.NoSuchMethodError: javax.servlet.ServletContext.getVirtualServerName()Ljava/lang/String;
    at org.apache.catalina.authenticator.AuthenticatorBase.startInternal(AuthenticatorBase.java:1220)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    ... 39 common frames omitted
2019-09-26 18:47:15.115 ERROR [main] o.apache.catalina.core.ContainerBase - A child container failed during start
java.util.concurrent.ExecutionException: org.apache.catalina.LifecycleException: A child container failed during start
    at java.util.concurrent.FutureTask.report(FutureTask.java:122)
    at java.util.concurrent.FutureTask.get(FutureTask.java:192)
    at org.apache.catalina.core.ContainerBase.startInternal(ContainerBase.java:916)
    at org.apache.catalina.core.StandardEngine.startInternal(StandardEngine.java:262)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    at org.apache.catalina.core.StandardService.startInternal(StandardService.java:421)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    at org.apache.catalina.core.StandardServer.startInternal(StandardServer.java:932)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    at org.apache.catalina.startup.Tomcat.start(Tomcat.java:459)
    at org.springframework.boot.web.embedded.tomcat.TomcatWebServer.initialize(TomcatWebServer.java:105)
    at org.springframework.boot.web.embedded.tomcat.TomcatWebServer.<init>(TomcatWebServer.java:86)
    at org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory.getTomcatWebServer(TomcatServletWebServerFactory.java:416)
    at org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory.getWebServer(TomcatServletWebServerFactory.java:180)
    at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.createWebServer(ServletWebServerApplicationContext.java:180)
    at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.onRefresh(ServletWebServerApplicationContext.java:153)
    at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:543)
    at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:141)
    at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:744)
    at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:391)
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:312)
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:1215)
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:1204)
    at com.DemoApplication.main(DemoApplication.java:23)
Caused by: org.apache.catalina.LifecycleException: A child container failed during start
    at org.apache.catalina.core.ContainerBase.startInternal(ContainerBase.java:928)
    at org.apache.catalina.core.StandardHost.startInternal(StandardHost.java:841)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1384)
    at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1374)
    at java.util.concurrent.FutureTask.run(FutureTask.java:266)
    at org.apache.tomcat.util.threads.InlineExecutorService.execute(InlineExecutorService.java:75)
    at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
    at org.apache.catalina.core.ContainerBase.startInternal(ContainerBase.java:909)
    ... 21 common frames omitted
Caused by: java.util.concurrent.ExecutionException: org.apache.catalina.LifecycleException: Failed to start component [NonLoginAuthenticator[StandardEngine[Tomcat].StandardHost[localhost].TomcatEmbeddedContext[]]]
    at java.util.concurrent.FutureTask.report(FutureTask.java:122)
    at java.util.concurrent.FutureTask.get(FutureTask.java:192)
    at org.apache.catalina.core.ContainerBase.startInternal(ContainerBase.java:916)
    ... 29 common frames omitted
Caused by: org.apache.catalina.LifecycleException: Failed to start component [NonLoginAuthenticator[StandardEngine[Tomcat].StandardHost[localhost].TomcatEmbeddedContext[]]]
    at org.apache.catalina.util.LifecycleBase.handleSubClassException(LifecycleBase.java:440)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:198)
    at org.apache.catalina.core.StandardPipeline.startInternal(StandardPipeline.java:181)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:5063)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1384)
    at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1374)
    at java.util.concurrent.FutureTask.run(FutureTask.java:266)
    at org.apache.tomcat.util.threads.InlineExecutorService.execute(InlineExecutorService.java:75)
    at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:134)
    at org.apache.catalina.core.ContainerBase.startInternal(ContainerBase.java:909)
    ... 29 common frames omitted
Caused by: java.lang.NoSuchMethodError: javax.servlet.ServletContext.getVirtualServerName()Ljava/lang/String;
    at org.apache.catalina.authenticator.AuthenticatorBase.startInternal(AuthenticatorBase.java:1220)
    at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
    ... 39 common frames omitted
2019-09-26 18:47:15.121 INFO  [main] o.a.coyote.http11.Http11NioProtocol - Pausing ProtocolHandler ["http-nio-8080"]
2019-09-26 18:47:15.122 INFO  [main] o.a.catalina.core.StandardService - Stopping service [Tomcat]
2019-09-26 18:47:15.124 INFO  [main] o.a.coyote.http11.Http11NioProtocol - Destroying ProtocolHandler ["http-nio-8080"]
2019-09-26 18:47:15.150 WARN  [main] o.s.b.w.s.c.AnnotationConfigServletWebServerApplicationContext - Exception encountered during context initialization - cancelling refresh attempt: org.springframework.context.ApplicationContextException: Unable to start web server; nested exception is org.springframework.boot.web.server.WebServerException: Unable to start embedded Tomcat
2019-09-26 18:47:15.160 INFO  [main] o.s.b.a.l.ConditionEvaluationReportLoggingListener -

Error starting ApplicationContext. To display the conditions report re-run your application with 'debug' enabled.
2019-09-26 18:47:15.167 ERROR [main] o.s.b.d.LoggingFailureAnalysisReporter -

***************************
APPLICATION FAILED TO START
***************************

Description:

An attempt was made to call a method that does not exist. The attempt was made from the following location:

    org.apache.catalina.authenticator.AuthenticatorBase.startInternal(AuthenticatorBase.java:1220)

The following method did not exist:

    javax.servlet.ServletContext.getVirtualServerName()Ljava/lang/String;

The method's class, javax.servlet.ServletContext, is available from the following locations:

    jar:file:/D:/myFiles/java/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar!/javax/servlet/ServletContext.class
    jar:file:/D:/myFiles/java/repository/org/apache/tomcat/embed/tomcat-embed-core/9.0.24/tomcat-embed-core-9.0.24.jar!/javax/servlet/ServletContext.class

It was loaded from the following location:

    file:/D:/myFiles/java/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar


Action:

Correct the classpath of your application so that it contains a single, compatible version of javax.servlet.ServletContext
```

重点在最后面的几行：
```

The method's class, javax.servlet.ServletContext, is available from the following locations:

    jar:file:/D:/myFiles/java/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar!/javax/servlet/ServletContext.class
    jar:file:/D:/myFiles/java/repository/org/apache/tomcat/embed/tomcat-embed-core/9.0.24/tomcat-embed-core-9.0.24.jar!/javax/servlet/ServletContext.class

It was loaded from the following location:

    file:/D:/myFiles/java/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar

Action:

Correct the classpath of your application so that it contains a single, compatible version of javax.servlet.ServletContext
```

 这意思是说，ServletContext这个类在下面的两个路径下都存在

一个是自己引入的：

`jar:file:/D:/myFiles/java/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar!/javax/servlet/ServletContext.class`

另一个，是Tomcat里面的：

`jar:file:/D:/myFiles/java/repository/org/apache/tomcat/embed/tomcat-embed-core/9.0.24/tomcat-embed-core-9.0.24.jar!`

最后一行提示，只能保留一个

 

如果把Tomcat引入的排除的话，还会报错：
```

2019-09-26 18:57:10.204 ERROR [main] o.s.boot.SpringApplication - Application run failed
org.springframework.context.ApplicationContextException: Unable to start web server; nested exception is org.springframework.context.ApplicationContextException: Unable to start ServletWebServerApplicationContext due to missing ServletWebServerFactory bean.
    at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.onRefresh(ServletWebServerApplicationContext.java:156)
    at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:543)
    at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:141)
    at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:744)
    at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:391)
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:312)
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:1215)
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:1204)
    at com.DemoApplication.main(DemoApplication.java:23)
Caused by: org.springframework.context.ApplicationContextException: Unable to start ServletWebServerApplicationContext due to missing ServletWebServerFactory bean.
    at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.getWebServerFactory(ServletWebServerApplicationContext.java:203)
    at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.createWebServer(ServletWebServerApplicationContext.java:179)
    at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.onRefresh(ServletWebServerApplicationContext.java:153)
    ... 8 common frames omitted
```
