## Spring概述（10）

### 1、什么是spring?

> Spring是一个轻量级的IoC和AOP容器框架。是为Java应用程序提供基础性服务的一套框架，目的是用于简化企业应用程序的开发，它使得开发者只需要关心业务需求。常见的配置方式有三种：基于XML的配置、基于注解的配置、基于Java的配置。
>
> 主要由以下几个模块组成：
>
> Spring Core：核心类库，提供IOC服务；
> Spring Context：提供框架式的Bean访问方式，以及企业级功能（JNDI、定时任务等）；
> Spring AOP：AOP服务；
> Spring DAO：对JDBC的抽象，简化了数据访问异常的处理；
> Spring ORM：对现有的ORM框架的支持；
> Spring Web：提供了基本的面向Web的综合特性，例如多方文件上传；
> Spring MVC：提供面向Web应用的Model-View-Controller实现。

### 2、Spring框架的设计目标，设计理念，和核心是什么

>  设计目标:一站式、轻量级、应用开发框架。最终目标是简化应用开发的编程模型。
> 设计理念：Spring 抽象了许多在应用开发中遇到的共性问题，支持POJO和使用JavaBean的开发方式，使应用面向接口开发，充分支持OO（面向对象）的设计方法。
>
> IoC容器实现的依赖反转，把依赖关系的管理从Java对象中解放出来，交给了IoC容器（或者说是Spring框架）来完成，从而完成了对象之间的关系解耦：原来的对象-对象的关系，转化为对象-IoC容器-对象的关系，通过这种对象-IoC容器-对象的关系，更体现出IoC容器对应用的平台作用。
> 
> 核心, Core Context Beans

### 3、Spring的优缺点是什么？

> 1，降低了组件之间的耦合性，实现了软件各层之间的解耦。
> 2，可以使用容器提供的众多服务，如事务管理，消息服务等。
> 3，容器提供单例模式支持。
> 4，容器提供了AOP技术，利用它可以很容易实现一些拦截，如权限拦截，运行期监控等。
> 5，容器提供了众多的辅助类，能够加快应用的开发。
> 6，spring对于主流的应用框架提供了很好的支持，例如mybatis等。
> 7，spring属于低入侵式设计。
> 8，独立于各种应用服务器。
> 9，spring的DI机制降低了业务对象替换的复杂性。
> 10，spring的高度开放性，并不强制应用完全依赖于它，开发者可以自由选择spring的部分或者全部。

> 使用了大量的反射机制，反射机制非常占用内存。

### 4、Spring有哪些应用场景

> 应用场景：JavaEE企业应用开发，包括SSH、SSM等
>
> Spring价值：
>
> Spring是非侵入式的框架，目标是使应用程序代码对框架依赖最小化；
> Spring提供一个一致的编程模型，使应用直接使用POJO开发，与运行环境隔离开来；
> Spring推动应用设计风格向面向对象和面向接口开发转变，提高了代码的重用性和可测试性；

5、Spring由哪些模块组成？
6、Spring 框架中都用到了哪些设计模式？
7、详细讲解一下核心容器（spring context应用上下文) 模块
8、Spring框架中有哪些不同类型的事件
9、Spring 应用程序有哪些不同组件？
10、使用 Spring 有哪些方式？

**Spring控制反转(IOC)（13）**
1、什么是Spring IOC 容器？
2、控制反转(IoC)有什么作用
3、IOC的优点是什么？
4、Spring IoC 的实现机制
5、Spring 的 IoC支持哪些功能
6、BeanFactory 和 ApplicationContext有什么区别？
7、Spring 如何设计容器的，BeanFactory和ApplicationContext的关系详解
8、ApplicationContext通常的实现是什么？
9、什么是Spring的依赖注入？
10、依赖注入的基本原则
11、依赖注入有什么优势
12、有哪些不同类型的依赖注入实现方式？
13、构造器依赖注入和 Setter方法注入的区别

**Spring Beans（19）**
1、什么是Spring beans？
2、一个 Spring Bean 定义 包含什么？
3、如何给Spring 容器提供配置元数据？Spring有几种配置方式
4、Spring配置文件包含了哪些信息
5、Spring基于xml注入bean的几种方式
6、你怎样定义类的作用域？
7、解释Spring支持的几种bean的作用域
8、Spring框架中的单例bean是线程安全的吗？
9、Spring如何处理线程并发问题？
10、解释Spring框架中bean的生命周期
11、哪些是重要的bean生命周期方法？ 你能重载它们吗？
12、什么是Spring的内部bean？什么是Spring inner beans？
13、在 Spring中如何注入一个java集合？
14、什么是bean装配？
15、什么是bean的自动装配？
16、解释不同方式的自动装配，spring 自动装配 bean 有哪些方式？
17、使用@Autowired注解自动装配的过程是怎样的？
18、自动装配有哪些局限性？
19、你可以在Spring中注入一个null 和一个空字符串吗？

**Spring注解（8）**
1、什么是基于Java的Spring注解配置? 给一些注解的例子
2、怎样开启注解装配？
3、@Component, @Controller, @Repository, @Service 有何区别？
4、@Required 注解有什么作用
5、@Autowired 注解有什么作用
6、@Autowired和@Resource之间的区别
7、@Qualifier 注解有什么作用
8、@RequestMapping 注解有什么用？

**Spring数据访问（14）**
1、解释对象/关系映射集成模块
2、在Spring框架中如何更有效地使用JDBC？
3、解释JDBC抽象和DAO模块
4、spring DAO 有什么用？
5、spring JDBC API 中存在哪些类？
6、JdbcTemplate是什么
7、使用Spring通过什么方式访问Hibernate？使用 Spring 访问 Hibernate 的方法有哪些？
8、如何通过HibernateDaoSupport将Spring和Hibernate结合起来？
9、Spring支持的事务管理类型， spring 事务实现方式有哪些？
10、Spring事务的实现方式和实现原理
11、说一下Spring的事务传播行为
12、说一下 spring 的事务隔离？
13、Spring框架的事务管理有哪些优点？
14、你更倾向用那种事务管理类型？

**Spring面向切面编程(AOP)（13）**
1、什么是AOP
2、Spring AOP and AspectJ AOP 有什么区别？AOP 有哪些实现方式？
3、JDK动态代理和CGLIB动态代理的区别
4、如何理解 Spring 中的代理？
5、解释一下Spring AOP里面的几个名词
6、Spring在运行时通知对象
7、Spring只支持方法级别的连接点
8、在Spring AOP 中，关注点和横切关注的区别是什么？在 spring aop 中 concern 和 cross-cutting concern 的不同之处
9、Spring通知有哪些类型？
10、什么是切面 Aspect？
11、解释基于XML Schema方式的切面实现
12、解释基于注解的切面实现
13、有几种不同类型的自动代理？

### 具体答案详见：

**什么是spring?**
Spring是一个轻量级Java开发框架，最早有Rod Johnson创建，目的是为了解决企业级应用开发的业务逻辑层和其他各层的耦合问题。它是一个分层的JavaSE/JavaEE full-stack（一站式）轻量级开源框架，为开发Java应用程序提供全面的基础架构支持。Spring负责基础架构，因此Java开发者可以专注于应用程序的开发。

Spring最根本的使命是解决企业级应用开发的复杂性，即简化Java开发。

Spring可以做很多事情，它为企业级开发提供给了丰富的功能，但是这些功能的底层都依赖于它的两个核心特性，也就是依赖注入（dependency injection，DI）和面向切面编程（aspect-oriented programming，AOP）。

为了降低Java开发的复杂性，Spring采取了以下4种关键策略：

1、基于POJO的轻量级和最小侵入性编程；
2、通过依赖注入和面向接口实现松耦合；
3、基于切面和惯例进行声明式编程；
4、通过切面和模板减少样板式代码。

**Spring框架的设计目标，设计理念，和核心是什么**
Spring设计目标：Spring为开发者提供一个一站式轻量级应用开发平台；

Spring设计理念：在JavaEE开发中，支持POJO和JavaBean开发方式，使应用面向接口开发，充分支持OO（面向对象）设计方法；Spring通过IoC容器实现对象耦合关系的管理，并实现依赖反转，将对象之间的依赖关系交给IoC容器，实现解耦；

Spring框架的核心：IoC容器和AOP模块。通过IoC容器管理POJO对象以及他们之间的耦合关系；通过AOP以动态非侵入的方式增强服务。

IoC让相互协作的组件保持松散的耦合，而AOP编程允许你把遍布于应用各层的功能分离出来形成可重用的功能组件。