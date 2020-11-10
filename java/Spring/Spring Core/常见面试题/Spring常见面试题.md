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





## BeanFactory和FactoryBean的区别

BeanFactory和FactoryBean是两个容易混淆的概念，很多人喜欢问两者之间的区别，其实两者之间并无内在联系。

- BeanFactory接口：IoC容器的顶级接口，是IoC容器的最基础实现，也是访问Spring容器的根接口，负责对bean的创建，访问等工作。
- FactoryBean接口：可以返回bean的实例的工厂bean，通过实现该接口可以对bean进行一些额外的操作，例如根据不同的配置类型返回不同类型的bean，简化xml配置等。在使用上也有些特殊，BeanFactory接口中有一个字符常量`String FACTORY_BEAN_PREFIX = "&";` 当我们去获取BeanFactory类型的bean时，如果beanName不加&则获取到对应bean的实例；如果beanName加上&，则获取到BeanFactory本身的实例；FactoryBean接口对应Spring框架来说占有重要的地位，Spring本身就提供了70多个FactoryBean的实现。他们隐藏了实例化一些复杂的细节，给上层应用带来了便利。从Spring3.0开始，FactoryBean开始支持泛型。



## Bean的作用域和生命周期

##### 1.Bean的作用域

- singleton：单例Bean只在容器中存在一个实例，在Spring内部通过HashMap来维护单例bean的缓存
- prototype：每次索取bean时都会创建一个全新的Bean
- request：每次请求都会创建一个全新Bean，该类型作用于Web类型的Spring容器
- session：每个会话创建一个全新Bean，该类型作用于Web类型的Spring容器
- globalSession：类似于session作用域，只是其用于portlet环境的web应用。如果在非portlet环境将视为session作用域
- 总结：以上就是spring中bean的作用域，其中singleton，prototype属于Spring bean的基本作作用域，request，session，globalSession属于web应用环境的作用域，必须有web应用环境的支持

##### 2.Bean的生命周期

------

![img](https:////upload-images.jianshu.io/upload_images/13657367-a1f1437466c7e090.png?imageMogr2/auto-orient/strip|imageView2/2/w/744/format/webp)

20180905094458523.png

------

1. IoC容器启动
2. 实例化bean
3. 如果Bean实现了BeanNameAware接口，则调用setBeanName(String name)返回beanName，该方法不是设置beanName，而只是让Bean获取自己在BeanFactory配置中的名字
4. 如果Bean实现BeanFactoryAware接口，会回调该接口的setBeanFactory(BeanFactory beanFactory)方法，传入该Bean的BeanFactory，这样该Bean就获得了自己所在的BeanFactory
5. 如果Bean实现了ApplicationContextAware接口，则调用该接口的setApplicationContext(ApplicationContext  applicationContext)方法，设置applicationContext
6. 如果有Bean实现了BeanPostProcessor接口，则调用该接口的postProcessBeforeInitialzation(Object bean，String beanName)方法，将此BeanPostProcessor应用于给定的新bean实例
7. 如果Bean实现了InitializingBean接口，则会回调该接口的afterPropertiesSet()方法
8. 如果Bean配置了init-method方法，则会执行init-method配置的方法
9. 如果Bean实现了BeanPostProcessor接口，则会回调该接口的postProcessAfterInitialization(Object bean，String beanName)方法
10. 到此为止，spring中的bean已经可以使用了，这里又涉及到了bean的作用域问题，对于singleton类型的bean，Spring会将其缓存;对于prototype类型的bean，不缓存，每次都创建新的bean的实例
11. 容器关，如果Bean实现了DisposableBean接口，则会回调该接口的destroy()方法销毁bean，
12. 如果用户配置了定destroy-method，则调用自定义方法销毁bean



## Spring BeanPostProcessor和BeanFactoryPostProcessor的区别

- BeanPostProcessor接口：后置bean处理器，允许自定义修改新的bean实例，应用程序上下文可以在其bean定义中自动检测BeanPostProcessor类型的bean，并将它们应用于随后创建的任何bean。（例如：配置文件中注册了一个自定义BeanPostProcessor类型的bean，一个User类型的bean，应用程序上下文会在创建User实例之后对User应用BeanPostProcessor）。
- BeanFactoryPostProcessor接口：后置工厂处理器，允许自定义修改应用程序上下文的bean定义，调整bean属性值。应用程序上下文可以在其bean定义中自动检测BeanFactoryPostProcessor，并在创建任何非BeanFactoryPostProcessor类型bean之前应用它们（例如：配置文件中注册了一个自定义BeanFactoryPostProcessor类型的bean，一个User类型的bean，应用程序上下文会在创建User实例之前对User应用BeanFactoryPostProcessor）。

------

| 对比                   | BeanFactoryPostProcessor    | BeanPostProcessor                                            |
| ---------------------- | --------------------------- | ------------------------------------------------------------ |
| 回调时间               | Bean实例化完成之前          | Bean实例化完成之后                                           |
| 是否可修改bean定义信息 | 是                          | 否                                                           |
| 是否可修改bean实例信息 | 否                          | 是                                                           |
| 是否支持排序接口       | 是                          | 是                                                           |
| 方法级别               | ApplicationContext级别      | ApplicationContext级别                                       |
| 实现方式               | 注册自定义BeanPostProcessor | 注册自定义BeanFactoryPostProcessor，实现BeanFactoryAware接口 |

## IOC容器的启动过程

IoC容器的启动过程为：加载资源文件、解析资源文件、注册BeanDefinition，我们再来看一个更为详细的流程图（该流程图只列举了比较重要的步骤）。



![img](assets/13657367-1dc66cfa29baf3a6.webp)

## Spring从缓存中获取单例bean

```java
/** Cache of singleton objects: bean name to bean instance. */
/** 缓存beanName和bean实例 key-->beanName,value-->beanInstance */
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

/** Cache of singleton factories: bean name to ObjectFactory. */
/** 缓存beanName和beanFactory key-->beanName,value-->beanFactory */
private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

/** Cache of early singleton objects: bean name to bean instance. */
/** 缓存beanName和bean实例 key-->beanName,value-->beanInstance 该缓存主要为了解决bean的循环依赖引用 */
private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

/** Set of registered singletons, containing the bean names in registration order. */
/** 缓存所有注册的单例beanName */
private final Set<String> registeredSingletons = new LinkedHashSet<>(256);
```

## Spring解析构造函数

###### 2.1 判断有无显式指定参数,如果有则优先使用,如xmlBeanFactory.getBean("cat", "美美",3);

###### 2.2 优先尝试从缓存中获取,spring对参数的解析过程是比较复杂也耗时的,所以这里先尝试从缓存中获取已经解析过的构造函数参数

###### 2.3 缓存中不存在,则需要解析构造函数参数,以确定使用哪一个构造函数来进行实例化

一个类可能存在多个构造函数，如Dog(String name,int age);Dog(String name);Dog(int age)等等，所以需要解析对构造函数进行解析，以确定使用哪一个构造函数。

###### 2.4 使用指定的构造函数(如果有的话)。

这里说的指定构造函数,并不是我们在配置文件中指定的构造函数,而是通过解析SmartInstantiationAwareBeanPostProcessor得出的构造函数。
 参见`AbstractAutowireCapableBeanFactory-->determineConstructorsFromBeanPostProcessors(beanClass, beanName),`就是在本方法被调用之前执行的解析操作,即便解析出来的构造函数不为空,但是大家要注意,candidates是个数组,下一步依然还是要对candidates进行解析,以确定使用哪一个构造函数进行实例化。

###### 2.5 如果指定的构造函数不存在,则根据方法访问级别,获取该bean所有的构造函数

需要注意，该步骤获取的是类的构造函数，而不是在配置文件中的构造函数。

###### 2.6 对构造函数按照参数个数和参数类型进行排序,参数最多的构造函数会排在第一位

###### 2.7 循环所有bean类中的构造函数,解析确定使用哪一个构造函数

首先因为构造函数已经按照参数的个数排序，参数个数最多的排在最前面，所以判断如若解析出来的构造函数个数小于BeanDefinition中的构造函数个数，那么肯定不会使用该构造函数进行实例化，那么循环会继续。
 其次将解析到的构造函数封装至ArgumentsHolder对象。
 最后通过构造函数参数权重对比,得出最适合使用的构造函数。

###### 2.8 处理异常，缓存解析过的构造函数。

###### 2.9 获取实例化策略并执行实例化

同样这里也会有反射或CGLIB实例化bean，具体的细节，上一节已经分析过。

###### 2.10 返回BeanWrapper包装类

## Spring通过工厂方法实例化bean

1、判断是实例工厂还是静态工厂方法
 静态工厂方法是没有factoryBeanName的，所以如果factoryBeanName不为null，则一定是实例工厂方法，否则就是静态工厂方法；且如是实例工厂需要获取工厂的bean实例，已被后续实例化使用

2、判断有无显式指定参数,如果有则优先使用,如xmlBeanFactory.getBean("cat", "美美",3);

3、从缓存中加载工厂方法和构造函数参数

4、未能从缓存中加载工厂方法和构造函数参数，则解析并确定应该使用哪一个工厂方法实例化，并解析构造函数参数

