## 1. 请问JAVA应用服务器都有那些？

BEA WebLogic Server，

IBM WebSphere Application Server，

Oracle9i Application Server

jBoss，

Tomcat



## 2. 请问在什么情况下回使用assert？

assertion (断言)在软件开发中是一种常用的调试方式，很多开发语言中都支持这种机制。在实现中，assertion就是在程序中的一条语句，它对一个 boolean表达式进行检查，一个正确程序必须保证这个boolean表达式的值为true；如果该值为false，说明程序已经处于不正确的状态下，系统将给出警告或退出。一般来说，assertion用于保证程序最基本、关键的正确性。assertion检查通常在开发和测试时开启。为了提高性能，在软件发布后，assertion检查通常是关闭的。



## 3. 1分钟之内只能处理1000个请求，你怎么实现，手撕代码?

限流的几种方法：计数器，滑动窗口、漏桶法、令牌桶



## 4. 请问如何在链接里不输入项目名称的情况下启动项目？

可在tomcat配置虚拟目录。



## 5. 请说明一下JSP中的静态包含和动态包含的有哪些区别？

静态包含是通过JSP的include指令包含页面，动态包含是通过JSP标准动作<jsp:forward>包含页面。静态包含是编译时包含，如果包含的页面不存在则会产生编译错误，而且两个页面的"contentType"属性应保持一致，因为两个页面会合二为一，只产生一个class文件，因此被包含页面发生的变动再包含它的页面更新前不会得到更新。动态包含是运行时包含，可以向被包含的页面传递参数，包含页面和被包含页面是独立的，会编译出两个class文件，如果被包含的页面不存在，不会产生编译错误，也不影响页面其他部分的执行。

例如：<%-- 静态包含 --%>
<%@ include file="..." %>
<%-- 动态包含 --%>
<jsp:include page="...">
<jsp:param name="..." value="..." />
</jsp:include>



## 6. 请说一下表达式语言（EL）的隐式对象以及该对象的作用

EL的隐式对象包括：pageContext、initParam（访问上下文参数）、param（访问请求参数）、paramValues、header（访问请求头）、headerValues、cookie（访问cookie）、applicationScope（访问application作用域）、sessionScope（访问session作用域）、requestScope（访问request作用域）、pageScope（访问page作用域）。



## 7.  请谈一谈JSP有哪些内置对象？以及这些对象的作用分别是什么？

JSP有9个内置对象：
\- request：封装客户端的请求，其中包含来自GET或POST请求的参数；
\- response：封装服务器对客户端的响应；
\- pageContext：通过该对象可以获取其他对象；
\- session：封装用户会话的对象；
\- application：封装服务器运行环境的对象；
\- out：输出服务器响应的输出流对象；
\- config：Web应用的配置对象；
\- page：JSP页面本身（相当于Java程序中的this）；
\- exception：封装页面抛出异常的对象。

如果用Servlet来生成网页中的动态内容无疑是非常繁琐的工作，另一方面，所有的文本和HTML标签都是硬编码，即使做出微小的修改，都需要进行重新编译。JSP解决了Servlet的这些问题，它是Servlet很好的补充，可以专门用作为用户呈现视图（View），而Servlet作为控制器（Controller）专门负责处理用户请求并转发或重定向到某个页面。基于Java的Web开发很多都同时使用了Servlet和JSP。JSP页面其实是一个Servlet，能够运行Servlet的服务器（Servlet容器）通常也是JSP容器，可以提供JSP页面的运行环境，Tomcat就是一个Servlet/JSP容器。第一次请求一个JSP页面时，Servlet/JSP容器首先将JSP页面转换成一个JSP页面的实现类，这是一个实现了JspPage接口或其子接口HttpJspPage的Java类。JspPage接口是Servlet的子接口，因此每个JSP页面都是一个Servlet。转换成功后，容器会编译Servlet类，之后容器加载和实例化Java字节码，并执行它通常对Servlet所做的生命周期操作。对同一个JSP页面的后续请求，容器会查看这个JSP页面是否被修改过，如果修改过就会重新转换并重新编译并执行。如果没有则执行内存中已经存在的Servlet实例。



## 8. 说说weblogic中一个Domain的缺省目录结构?比如要将一个简单的helloWorld.jsp放入何目录下,然后在浏览器上就可打入主机？

端口号//helloword.jsp就可以看到运行结果了? 又比如这其中用到了一个自己写的javaBean该如何办?
Domain 目录服务器目录applications，将应用目录放在此目录下将可以作为应用访问，如果是Web应用，应用目录需要满足Web应用目录要求，jsp文件可以直接放在应用目录中，Javabean需要放在应用目录的WEB-INF目录的classes目录中，设置服务器的缺省应用将可以实现在浏览器上无需输入应用名。



## 9. 请说明一下jsp有哪些动作? 这些动作的作用又分别是什么?

JSP 共有以下6种基本动作 jsp:include：在页面被请求的时候引入一个文件。 jsp:useBean：寻找或者实例化一个JavaBean。
jsp:setProperty：设置JavaBean的属性。 jsp:getProperty：输出某个JavaBean的属性。 jsp:forward：把请求转到一个新的页面。

jsp:plugin：根据浏览器类型为Java插件生成OBJECT或EMBED标记。



## 10. 请详细说明一下Request对象的主要方法是什么？

setAttribute(String name,Object)：设置名字为name的request的参数值
getAttribute(String name)：返回由name指定的属性值
getAttributeNames()：返回request对象所有属性的名字集合，结果是一个枚举的实例
getCookies()：返回客户端的所有Cookie对象，结果是一个Cookie数组
getCharacterEncoding()：返回请求中的字符编码方式
getContentLength()：返回请求的Body的长度
getHeader(String name)：获得HTTP协议定义的文件头信息
getHeaders(String name)：返回指定名字的request Header的所有值，结果是一个枚举的实例
getHeaderNames()：返回所以request Header的名字，结果是一个枚举的实例
getInputStream()：返回请求的输入流，用于获得请求中的数据
getMethod()：获得客户端向服务器端传送数据的方法
getParameter(String name)：获得客户端传送给服务器端的有name指定的参数值
getParameterNames()：获得客户端传送给服务器端的所有参数的名字，结果是一个枚举的实例
getParameterValues(String name)：获得有name指定的参数的所有值
getProtocol()：获取客户端向服务器端传送数据所依据的协议名称
getQueryString()：获得查询字符串
getRequestURI()：获取发出请求字符串的客户端地址
getRemoteAddr()：获取客户端的IP地址
getRemoteHost()：获取客户端的名字
getSession([Boolean create])：返回和请求相关Session
getServerName()：获取服务器的名字
getServletPath()：获取客户端所请求的脚本文件的路径
getServerPort()：获取服务器的端口号
removeAttribute(String name)：删除请求中的一个属性



## 11. 请简要说明一下四种会话跟踪技术分别是什么？

会话作用域ServletsJSP 页面描述
page否是代表与一个页面相关的对象和属性。一个页面由一个编译好的 Java servlet 类（可以带有任何的 include 指令，但是没有 include 动作）表示。这既包括 servlet 又包括被编译成 servlet 的 JSP 页面request是是代表与 Web 客户机发出的一个请求相关的对象和属性。一个请求可能跨越多个页面，涉及多个 Web 组件（由于forward 指令和 include 动作的关系）session是是代表与用于某个 Web 客户机的一个用户体验相关的对象和属性。一个 Web 会话可以也经常会跨越多个客户机请求application是是代表与整个 Web 应用程序相关的对象和属性。这实质上是跨越整个 Web 应用程序，包括多个页面、请求和会话的一个全局作用域。



## 12. 请简要说明一下JSP和Servlet有哪些相同点和不同点？另外他们之间的联系又是什么呢？

JSP 是Servlet技术的扩展，本质上是Servlet的简易方式，更强调应用的外表表达。JSP编译后是”类servlet”。Servlet和JSP最主要的不同点在于，Servlet的应用逻辑是在Java文件中，并且完全从表示层中的HTML里分离开来。而JSP的情况是Java和HTML可以组合成一个扩展名为.jsp的文件。JSP侧重于视图，Servlet主要用于控制逻辑



## 13. 请说明一下JSP的内置对象以及该对象的使用方法。

request表示HttpServletRequest对象。它包含了有关浏览器请求的信息，并且提供了几个用于获取cookie, header, 和session数据的有用的方法。
response表示HttpServletResponse对象，并提供了几个用于设置送回浏览器的响应的方法（如cookies,头信息等）
out对象是javax.jsp.JspWriter的一个实例，并提供了几个方法使你能用于向浏览器回送输出结果。
pageContext表示一个javax.servlet.jsp.PageContext对象。它是用于方便存取各种范围的名字空间、servlet相关的对象的API，并且包装了通用的servlet相关功能的方法。
session表示一个请求的javax.servlet.http.HttpSession对象。Session可以存贮用户的状态信息
applicaton 表示一个javax.servle.ServletContext对象。这有助于查找有关servlet引擎和servlet环境的信息
config表示一个javax.servlet.ServletConfig对象。该对象用于存取servlet实例的初始化参数。
page表示从该页面产生的一个servlet实例



## 14. 请说明一下web.xml文件中可以配置哪些内容？

web.xml用于配置Web应用的相关信息，如：监听器（listener）、过滤器（filter）、 Servlet、相关参数、会话超时时间、安全验证方式、错误页面等，下面是一些开发中常见的配置：

①配置Spring上下文加载监听器加载Spring配置文件并创建IoC容器：
```xml
<context-param>
<param-name>contextConfigLocation</param-name>
<param-value>classpath:applicationContext.xml</param-value>
</context-param>

<listener>
<listener-class>
org.springframework.web.context.ContextLoaderListener
</listener-class>
</listener>
```
②配置Spring的OpenSessionInView过滤器来解决延迟加载和Hibernate会话关闭的矛盾：

```xml
<filter>
<filter-name>openSessionInView</filter-name>
<filter-class>
org.springframework.orm.hibernate3.support.OpenSessionInViewFilter
</filter-class>
</filter>

<filter-mapping>
<filter-name>openSessionInView</filter-name>
<url-pattern>/*</url-pattern>
</filter-mapping>
```
③配置会话超时时间为10分钟：
```xml
<session-config>
<session-timeout>10</session-timeout>
</session-config>
```
④配置404和Exception的错误页面：
```xml
<error-page>
<error-code>404</error-code>
<location>/error.jsp</location>
</error-page>
```
```xml
<error-page>
<exception-type>java.lang.Exception</exception-type>
<location>/error.jsp</location>
</error-page>
```
⑤配置安全认证方式：

```xml
<security-constraint>
<web-resource-collection>
<web-resource-name>ProtectedArea</web-resource-name>
<url-pattern>/admin/*</url-pattern>
<http-method>GET</http-method>
<http-method>POST</http-method>
</web-resource-collection>
<auth-constraint>
<role-name>admin</role-name>
</auth-constraint>
</security-constraint>

<login-config>
<auth-method>BASIC</auth-method>
</login-config>

<security-role>
<role-name>admin</role-name>
</security-role>
```

## 15. 请谈谈你对Javaweb开发中的监听器的理解？

Java Web开发中的监听器（listener）就是application、session、request三个对象创建、销毁或者往其中添加修改删除属性时自动执行代码的功能组件，如下所示：
①ServletContextListener：对Servlet上下文的创建和销毁进行监听。
②ServletContextAttributeListener：监听Servlet上下文属性的添加、删除和替换。
③HttpSessionListener：对Session的创建和销毁进行监听。

session的销毁有两种情况：1). session超时（可以在web.xml中通过<session-config>/<session-timeout>标签配置超时时间）；2). 通过调用session对象的invalidate()方法使session失效。
④HttpSessionAttributeListener：对Session对象中属性的添加、删除和替换进行监听。
⑤ServletRequestListener：对请求对象的初始化和销毁进行监听。
⑥ServletRequestAttributeListener：对请求对象属性的添加、删除和替换进行监听。



## 16.  请问过滤器有哪些作用？以及过滤器的用法又是什么呢?

Java Web开发中的过滤器（filter）是从Servlet 2.3规范开始增加的功能，并在Servlet 2.4规范中得到增强。对Web应用来说，过滤器是一个驻留在服务器端的Web组件，它可以截取客户端和服务器之间的请求与响应信息，并对这些信息进行过滤。当Web容器接受到一个对资源的请求时，它将判断是否有过滤器与这个资源相关联。如果有，那么容器将把请求交给过滤器进行处理。在过滤器中，你可以改变请求的内容，或者重新设置请求的报头信息，然后再将请求发送给目标资源。当目标资源对请求作出响应时候，容器同样会将响应先转发给过滤器，在过滤器中你可以对响应的内容进行转换，然后再将响应发送到客户端。

常见的过滤器用途主要包括：对用户请求进行统一认证、对用户的访问请求进行记录和审核、对用户发送的数据进行过滤或替换、转换图象格式、对响应内容进行压缩以减少传输量、对请求或响应进行加解密处理、触发资源访问事件、对XML的输出应用XSLT等。
和过滤器相关的接口主要有：Filter、FilterConfig和FilterChain。