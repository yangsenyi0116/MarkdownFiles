```java
@Override
public void refresh() throws BeansException, IllegalStateException {
   synchronized (this.startupShutdownMonitor) {
      // Prepare this context for refreshing.
      //刷新前的预处理;
      prepareRefresh();

      // Tell the subclass to refresh the internal bean factory.
       //获取BeanFactory；默认实现是DefaultListableBeanFactory，在创建容器的时候创建的
      ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

      // Prepare the bean factory for use in this context.
       //BeanFactory的预准备工作（BeanFactory进行一些设置，比如context的类加载器，BeanPostProcessor和XXXAware自动装配等）
      prepareBeanFactory(beanFactory);

      try {
         // Allows post-processing of the bean factory in context subclasses.
           //BeanFactory准备工作完成后进行的后置处理工作
         postProcessBeanFactory(beanFactory);

         // Invoke factory processors registered as beans in the context.
          //执行BeanFactoryPostProcessor的方法；
         invokeBeanFactoryPostProcessors(beanFactory);

         // Register bean processors that intercept bean creation.
          //注册BeanPostProcessor（Bean的后置处理器），在创建bean的前后等执行
         registerBeanPostProcessors(beanFactory);

         // Initialize message source for this context.
          //初始化MessageSource组件（做国际化功能；消息绑定，消息解析）；
         initMessageSource();

         // Initialize event multicaster for this context.
          //初始化事件派发器
         initApplicationEventMulticaster();

         // Initialize other special beans in specific context subclasses.
          //子类重写这个方法，在容器刷新的时候可以自定义逻辑；如创建Tomcat，Jetty等WEB服务器
         onRefresh();

         // Check for listener beans and register them.
          //注册应用的监听器。就是注册实现了ApplicationListener接口的监听器bean，这些监听器是注册到ApplicationEventMulticaster中的
         registerListeners();

         // Instantiate all remaining (non-lazy-init) singletons.
          //初始化所有剩下的非懒加载的单例bean
         finishBeanFactoryInitialization(beanFactory);

         // Last step: publish corresponding event.
           //完成context的刷新。主要是调用LifecycleProcessor的onRefresh()方法，并且发布事件（ContextRefreshedEvent）
         finishRefresh();
      }

      ....
   }
}
```

## prepareRefresh

表示在真正做refresh操作之前需要准备做的事情：

- 设置Spring容器的启动时间，
- 开启活跃状态，撤销关闭状态，。
- 初始化context environment（上下文环境）中的占位符属性来源。
- 验证环境信息里一些必须存在的属性

## ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory()

让这个类（AbstractApplicationContext）的子类刷新内部bean工厂。

- AbstractRefreshableApplicationContext容器：实际上就是重新创建一个bean工厂，并设置工厂的一些属性。
- GenericApplicationContext容器：获取创建容器的就创建的bean工厂，并且设置工厂的ID.

## prepareBeanFactory

上一步已经把工厂建好了，但是还不能投入使用，因为工厂里什么都没有，还需要配置一些东西。

> Configure the factory's standard context characteristics, such as the context's ClassLoader and post-processors.

他说配置这个工厂的标准环境，比如context的类加载器和post-processors后处理器。

```java
protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
   // Tell the internal bean factory to use the context's class loader etc.
   //设置BeanFactory的类加载器
   beanFactory.setBeanClassLoader(getClassLoader());
   //设置支持表达式解析器
   beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
   beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

   // Configure the bean factory with context callbacks.
   //添加部分BeanPostProcessor【ApplicationContextAwareProcessor】
   beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
   //设置忽略的自动装配的接口EnvironmentAware、EmbeddedValueResolverAware、xx,因为ApplicationContextAwareProcessor#invokeAwareInterfaces已经把这5个接口的实现工作做了
   beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
   beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
   beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
   beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
   beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
   beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

   // BeanFactory interface not registered as resolvable type in a plain factory.
   // MessageSource registered (and found for autowiring) as a bean.
   //注册可以解析的自动装配；我们能直接在任何组件中自动注入：BeanFactory、ResourceLoader、ApplicationEventPublisher、ApplicationContext
   //其他组件中可以通过 @autowired 直接注册使用
   beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
   beanFactory.registerResolvableDependency(ResourceLoader.class, this);
   beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
   beanFactory.registerResolvableDependency(ApplicationContext.class, this);

   // Register early post-processor for detecting inner beans as ApplicationListeners.
    //添加BeanPostProcessor【ApplicationListenerDetector】后置处理器，在bean初始化前后的一些工作
   beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

   // Detect a LoadTimeWeaver and prepare for weaving, if found.
   if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
      beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
      // Set a temporary ClassLoader for type matching.
      beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
   }

   // Register default environment beans.
    //给BeanFactory中注册一些能用的组件；
   if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
       //环境信息ConfigurableEnvironment
      beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
   }
   if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
       //系统属性，systemProperties【Map<String, Object>】
      beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
   }
   if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
       //系统环境变量systemEnvironment【Map<String, Object>】
      beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
   }
}
```

## postProcessBeanFactory

上面对bean工厂进行了许多配置，现在需要对bean工厂进行一些处理。不同的Spring容器做不同的操作。比如GenericWebApplicationContext容器的操作会在BeanFactory中添加ServletContextAwareProcessor用于处理ServletContextAware类型的bean初始化的时候调用setServletContext或者setServletConfig方法(跟ApplicationContextAwareProcessor原理一样)。

> GenericWebApplicationContext#postProcessBeanFactory源码

```java
protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    beanFactory.addBeanPostProcessor(new BootstrapContextAwareProcessor(this.bootstrapContext));
    beanFactory.ignoreDependencyInterface(BootstrapContextAware.class);
    beanFactory.registerResolvableDependency(BootstrapContext.class, this.bootstrapContext);
    BootstrapContext var10002 = this.bootstrapContext;
    beanFactory.registerResolvableDependency(WorkManager.class, var10002::getWorkManager);
}
```

## invokeBeanFactoryPostProcessors

先介绍两个接口：

- BeanFactoryPostProcessor：用来修改Spring容器中已经存在的bean的定义，使用ConfigurableListableBeanFactory对bean进行处理
- BeanDefinitionRegistryPostProcessor：继承BeanFactoryPostProcessor，作用跟BeanFactoryPostProcessor一样，只不过是使用BeanDefinitionRegistry对bean进行处理

在Spring容器中找出实现了BeanFactoryPostProcessor接口的processor并执行。Spring容器会委托给PostProcessorRegistrationDelegate的invokeBeanFactoryPostProcessors方法执行。