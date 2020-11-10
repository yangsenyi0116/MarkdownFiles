# springboot源码解析（一）：启动过程

## 1.springboot入口程序

```java
@SpringBootApplication
public class StartupApplication {
    public static void main(String[] args) {
        SpringApplication.run(StartupApplication.class, args);
    }
}
```

当程序开始执行之后，会调用`SpringApplication`的构造方法，惊醒某些参数的设置

```java
//创建一个新的实例，这个应用程序的上下文将要从指定的来源加载Bean
public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
	//资源初始化资源加载其，默认为null
    this.resourceLoader = resourceLoader;
    //	断言主要加载资源类不能为null,否则报错
	Assert.notNull(primarySources, "PrimarySources must not be null");
    //初始化主要加载资源类集合去去重
	this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
    //推断当前web应用程序，一共有三种: NONE,SERVLET,REACTIVE
	this.webApplicationType = WebApplicationType.deduceFromClasspath();
    //设置应用上下文初始化器，从"META-INF/spring.factories"读取ApplicationContextInitializer类的实例名称及和并去重，并进行set去重，一共7个
	setInitializers((Collection)getSpringFactoriesInstances(ApplicationContextInitializer.class));
    //设置监听器，从"META_INF/spring.factories"读取ApplicationListener类的实例名称并去重，并惊醒set去重，一共11个
	setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
	//推断主入口应用类，同故宫当前调用栈，获取Main方法所在类，并复制给mainApplicationClass
    this.mainApplicationClass = deduceMainApplicationClass();
	}
```

在上述构造方法中，有一个判断应用类型的方法，用来判断当前应用程序的类型

```java
static WebApplicationType deduceFromClasspath() {
		if (ClassUtils.isPresent(WEBFLUX_INDICATOR_CLASS, null)
				&& !ClassUtils.isPresent(WEBMVC_INDICATOR_CLASS, null)
				&& !ClassUtils.isPresent(JERSEY_INDICATOR_CLASS, null)) {
			return WebApplicationType.REACTIVE;
		}
		for (String className : SERVLET_INDICATOR_CLASSES) {
			if (!ClassUtils.isPresent(className, null)) {
				return WebApplicationType.NONE;
			}
		}
		return WebApplicationType.SERVLET;
	}


public enum WebApplicationType {

	/**
	 * The application should not run as a web application and should not start an
	 * embedded web server.
	 * 非web项目
	 */
	NONE,

	/**
	 * The application should run as a servlet-based web application and should start an
	 * embedded servlet web server.
	 * servlet web 项目
	 */
	SERVLET,

	/**
	 * The application should run as a reactive web application and should start an
	 * embedded reactive web server.
	 * 响应式web项目
	 */
	REACTIVE;
}
```

springboot启动的运行方法，可以看到主要是各种运行环境的准备工作

```java
public ConfigurableApplicationContext run(String... args) {
    	//1. 创建并启动记时监控类
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
    	//2. 初始化应用上下文和异常报告集合
		ConfigurableApplicationContext context = null;
		Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
    	//3. 设置系统属性"java.awt.headless"的值，默认为true，用于运行headless服务器，进行简单的图像处理，用于在缺少显示屏，键盘或者鼠标时的系统配置，很多监控工具如jconsole需要将该值设置为true
		configureHeadlessProperty();
    	//4. 创建所有spring运行监听器并发布应用启动事件，简单说的话就是获取SpringApplicationRunListener类型的实例(EventPublishingRunListener对象),并封装进SpringApplicationRunListener对象，然后范文这个SpringApplicationRunListeners对象。说的再简单点，getRunListener就是准备好了运行时监听器EventPublishingRunListener
		SpringApplicationRunListeners listeners = getRunListeners(args);
		listeners.starting();
		try {
            //5. 初始化默认应用参数类
			ApplicationArguments applicationArguments = new DefaultApplicationArguments(
					args);
            //6.根据运行监听器和应用参数来准备spring环境
			ConfigurableEnvironment environment = prepareEnvironment(listeners,
					applicationArguments);
            //将要忽略的bean的参数打开
			configureIgnoreBeanInfo(environment);
            //7.创建banner打印类
			Banner printedBanner = printBanner(environment);
            // 8.创建应用上下文，可以理解为创建一个容器
			context = createApplicationContext();
            // 9.准备异常报告器，用来支持报告关于启动的错误
			exceptionReporters = getSpringFactoriesInstances(
					SpringBootExceptionReporter.class,
					new Class[] { ConfigurableApplicationContext.class }, context);
            // 10. 准备应用上下文，该步骤包含一个非常关键的操作，将启动类注入容器，为后续开启自动化提供基础
			prepareContext(context, environment, listeners, applicationArguments,
					printedBanner);
            // 11. 刷新应用上下文
			refreshContext(context);
            // 12. 应用上下文刷新后置处理，做一些扩展功能
			afterRefresh(context, applicationArguments);
            // 13. 停止记时监控类
			stopWatch.stop();
            // 14. 输出日志记录执行主类名、时间信息
			if (this.logStartupInfo) {
				new StartupInfoLogger(this.mainApplicationClass)
						.logStarted(getApplicationLog(), stopWatch);
			}
            // 15. 发布应用上下文启动监听事件
			listeners.started(context);
            // 16. 执行所有的Runner执行器
			callRunners(context, applicationArguments);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, listeners);
			throw new IllegalStateException(ex);
		}

		try {
            // 17. 发布应用上下文就绪事件
			listeners.running(context);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, null);
			throw new IllegalStateException(ex);
		}
    	// 18. 返回应用上下文
		return context;
	}
```

下面详细介绍各个启动的环节

1. 创建并启动即使监控类，可以看到记录当前任务的名称，默认是空字符串，然后记录当前springboot应用启动的开始时间

```java
StopWatch stopWatch = new StopWatch();
stopWatch.start();
//详细源码
public void start(String taskName) throws IllegalStateException {
		if (this.currentTaskName != null) {
			throw new IllegalStateException("Can't start StopWatch: it's already running");
		}
		this.currentTaskName = taskName;
		this.startTimeMillis = System.currentTimeMillis();
	}
```

2. 初始化应用上下文和异常报告集合

```java
ConfigurableApplicationContext context = null;
Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
```

3. 设置系统属性`java.awt.headless`

```java
/*
java.awt.headless模式是在缺少显示屏、键盘或者鼠标的系统配置
当配置了如下属性之后，引用程序可以执行如下操作：
	1. 创建轻量级组件
	2. 收集关于可用字体、字体指标和字体设置的信息
	3. 设置颜色来选软准备图片
	4. 创建和获取图像，为渲染准备秃瓢
	5. 使用java.awt.PrintJob,java.awt.pringt.*和javax.pringt.*类莉的方法进行打印
	*/
private void configureHeadlessProperty() {
		System.setProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS, System.getProperty(
				SYSTEM_PROPERTY_JAVA_AWT_HEADLESS, Boolean.toString(this.headless)));
	}
```

4. 创建所有spring运行监听器并发布启动应用事件

```java
SpringApplicationRunListeners listeners = getRunListeners(args);
listeners.starting();

//创建spring监听器
	private SpringApplicationRunListeners getRunListeners(String[] args) {
		Class<?>[] types = new Class<?>[] { SpringApplication.class, String[].class };
		return new SpringApplicationRunListeners(logger, getSpringFactoriesInstances(
				SpringApplicationRunListener.class, types, this, args));
	}

S	pringApplicationRunListeners(Log log,
			Collection<? extends SpringApplicationRunListener> listeners) {
		this.log = log;
		this.listeners = new ArrayList<>(listeners);
	}
//循环遍历获取监听器
    @Override
    public void starting() {
        this.initialMulticaster.multicastEvent(
            new ApplicationStartingEvent(this.application, this.args));
    }
//ApplicationStartingEvent是springboot框架最早执行的监听器，在该监听器执行started方法时，会继续发布时间，只要是基于spring的时间机制
	@Override
	public void multicastEvent(final ApplicationEvent event, @Nullable ResolvableType eventType) {
		ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
		for (final ApplicationListener<?> listener : getApplicationListeners(event, type)) {
            //获取线程池，如果为空则同步处理，这里线程池为空，还未初始化
			Executor executor = getTaskExecutor();
			if (executor != null) {
                //异步发送事件
				executor.execute(() -> invokeListener(listener, event));
			}
			else {
                //同步发送事件
				invokeListener(listener, event);
			}
		}
	}
```

5. 初始化默认应用参数类

```java
ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);

	public DefaultApplicationArguments(String[] args) {
		Assert.notNull(args, "Args must not be null");
		this.source = new Source(args);
		this.args = args;
	}
```

6. 根据运行监听器和应用参数来准备spring环境

```java
ConfigurableEnvironment environment = prepareEnvironment(listeners,applicationArguments);
	//详环境的准备
	private ConfigurableEnvironment prepareEnvironment(
			SpringApplicationRunListeners listeners,
			ApplicationArguments applicationArguments) {
		// Create and configure the environment
        // 获取或者创建应用环境
		ConfigurableEnvironment environment = getOrCreateEnvironment();
        // 配置应用环境，配置propertySource和avtiveProfiles
		configureEnvironment(environment, applicationArguments.getSourceArgs());
        // listeners环境准备，广播ApplicationEnviromentPreparedEvent
		listeners.environmentPrepared(environment);
        //将环境绑定给当前应用程序
		bindToSpringApplication(environment);
        //对当前的环境类型进行判断，如果不一致进行转换
		if (!this.isCustomEnvironment) {
			environment = new EnvironmentConverter(getClassLoader())
					.convertEnvironmentIfNecessary(environment, deduceEnvironmentClass());
		}
        // 配置propertySource对与自己的递归依赖
		ConfigurationPropertySources.attach(environment);
		return environment;
	}

	//获取或者创建应用环境，根据应用程序的类型可以分为servlet环境、标准环境(特殊的非web环境)和响应式环境
	private ConfigurableEnvironment getOrCreateEnvironment() {
       	//存在则直接返回
		if (this.environment != null) {
			return this.environment;
		}
        //根据webApplicationType创建对应的Environment
		switch (this.webApplicationType) {
		case SERVLET:
			return new StandardServletEnvironment();
		case REACTIVE:
			return new StandardReactiveWebEnvironment();
		default:
			return new StandardEnvironment();
		}
	}
	//配置应用环境
	protected void configureEnvironment(ConfigurableEnvironment environment,
			String[] args) {
		if (this.addConversionService) {
			ConversionService conversionService = ApplicationConversionService
					.getSharedInstance();
			environment.setConversionService(
					(ConfigurableConversionService) conversionService);
		}
        //配置property source
		configurePropertySources(environment, args);
        //配置profiles
		configureProfiles(environment, args);
	}
```

7. 创建beans的打印类

```java
Banner printedBanner = printBanner(environment);
	//打印类的详细操作过程
	private Banner printBanner(ConfigurableEnvironment environment) {
		if (this.bannerMode == Banner.Mode.OFF) {
			return null;
		}
		ResourceLoader resourceLoader = (this.resourceLoader != null)
				? this.resourceLoader : new DefaultResourceLoader(getClassLoader());
		SpringApplicationBannerPrinter bannerPrinter = new SpringApplicationBannerPrinter(
				resourceLoader, this.banner);
		if (this.bannerMode == Mode.LOG) {
			return bannerPrinter.print(environment, this.mainApplicationClass, logger);
		}
		return bannerPrinter.print(environment, this.mainApplicationClass, System.out);
	}
```

8. 创建应用的上下文根据不同的应用类型初始化不同的上下文应用

```java
context = createApplicationContext();

protected ConfigurableApplicationContext createApplicationContext() {
		Class<?> contextClass = this.applicationContextClass;
		if (contextClass == null) {
			try {
				switch (this.webApplicationType) {
				case SERVLET:
					contextClass = Class.forName(DEFAULT_SERVLET_WEB_CONTEXT_CLASS);
					break;
				case REACTIVE:
					contextClass = Class.forName(DEFAULT_REACTIVE_WEB_CONTEXT_CLASS);
					break;
				default:
					contextClass = Class.forName(DEFAULT_CONTEXT_CLASS);
				}
			}
			catch (ClassNotFoundException ex) {
				throw new IllegalStateException(
						"Unable create a default ApplicationContext, "
								+ "please specify an ApplicationContextClass",
						ex);
			}
		}
		return (ConfigurableApplicationContext) BeanUtils.instantiateClass(contextClass);
	}
```

9. 准备异常报告器

```java
exceptionReporters = getSpringFactoriesInstances(
					SpringBootExceptionReporter.class,
					new Class[] { ConfigurableApplicationContext.class }, context);

private <T> Collection<T> getSpringFactoriesInstances(Class<T> type,
			Class<?>[] parameterTypes, Object... args) {
		ClassLoader classLoader = getClassLoader();
		// Use names and ensure unique to protect against duplicates
		Set<String> names = new LinkedHashSet<>(
				SpringFactoriesLoader.loadFactoryNames(type, classLoader));
		List<T> instances = createSpringFactoriesInstances(type, parameterTypes,
				classLoader, args, names);
		AnnotationAwareOrderComparator.sort(instances);
		return instances;
	}
```

10. 准备应用上下文

```java
prepareContext(context, environment, listeners, applicationArguments,
					printedBanner);

private void prepareContext(ConfigurableApplicationContext context,
			ConfigurableEnvironment environment, SpringApplicationRunListeners listeners,
			ApplicationArguments applicationArguments, Banner printedBanner) {
		context.setEnvironment(environment);
		postProcessApplicationContext(context);
		applyInitializers(context);
		listeners.contextPrepared(context);
		if (this.logStartupInfo) {
			logStartupInfo(context.getParent() == null);
			logStartupProfileInfo(context);
		}
		// Add boot specific singleton beans
		ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
		beanFactory.registerSingleton("springApplicationArguments", applicationArguments);
		if (printedBanner != null) {
			beanFactory.registerSingleton("springBootBanner", printedBanner);
		}
		if (beanFactory instanceof DefaultListableBeanFactory) {
			((DefaultListableBeanFactory) beanFactory)
					.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
		}
		// Load the sources
		Set<Object> sources = getAllSources();
		Assert.notEmpty(sources, "Sources must not be empty");
		load(context, sources.toArray(new Object[0]));
		listeners.contextLoaded(context);
	}
```

11. 刷新应用上下文
```java
refreshContext(context);

private void refreshContext(ConfigurableApplicationContext context) {
		refresh(context);
		if (this.registerShutdownHook) {
			try {
				context.registerShutdownHook();
			}
			catch (AccessControlException ex) {
				// Not allowed in some environments.
			}
		}
	}

//spring中的refresh
	@Override
	public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			// Prepare this context for refreshing.
			prepareRefresh();

			// Tell the subclass to refresh the internal bean factory.
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

			// Prepare the bean factory for use in this context.
			prepareBeanFactory(beanFactory);

			try {
				// Allows post-processing of the bean factory in context subclasses.
				postProcessBeanFactory(beanFactory);

				// Invoke factory processors registered as beans in the context.
				invokeBeanFactoryPostProcessors(beanFactory);

				// Register bean processors that intercept bean creation.
				registerBeanPostProcessors(beanFactory);

				// Initialize message source for this context.
				initMessageSource();

				// Initialize event multicaster for this context.
				initApplicationEventMulticaster();

				// Initialize other special beans in specific context subclasses.
				onRefresh();

				// Check for listener beans and register them.
				registerListeners();

				// Instantiate all remaining (non-lazy-init) singletons.
				finishBeanFactoryInitialization(beanFactory);

				// Last step: publish corresponding event.
				finishRefresh();
			}

			catch (BeansException ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Exception encountered during context initialization - " +
							"cancelling refresh attempt: " + ex);
				}

				// Destroy already created singletons to avoid dangling resources.
				destroyBeans();

				// Reset 'active' flag.
				cancelRefresh(ex);

				// Propagate exception to caller.
				throw ex;
			}

			finally {
				// Reset common introspection caches in Spring's core, since we
				// might not ever need metadata for singleton beans anymore...
				resetCommonCaches();
			}
		}
	}
```
12. 应用上下文刷新后置处理，做一些扩展功能
```java
afterRefresh(context, applicationArguments);

	//空方法 用来扩展
	protected void afterRefresh(ConfigurableApplicationContext context,
			ApplicationArguments args) {
	}
```
13. 停止记时监控类

```java
stopWatch.stop();

	public void stop() throws IllegalStateException {
		if (this.currentTaskName == null) {
			throw new IllegalStateException("Can't stop StopWatch: it's not running");
		}
		long lastTime = System.currentTimeMillis() - this.startTimeMillis;
		this.totalTimeMillis += lastTime;
		this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);
		if (this.keepTaskList) {
			this.taskList.add(this.lastTaskInfo);
		}
		++this.taskCount;
		this.currentTaskName = null;
	}
```

14. 输出日志记录执行主类名、时间信息

```java
if (this.logStartupInfo) {
				new StartupInfoLogger(this.mainApplicationClass)
						.logStarted(getApplicationLog(), stopWatch);
			}
```

15. 发布应用上下文启动监听事件

```java
listeners.started(context);

	public void started(ConfigurableApplicationContext context) {
		for (SpringApplicationRunListener listener : this.listeners) {
			listener.started(context);
		}
	}

	@Override
	public void started(ConfigurableApplicationContext context) {
		context.publishEvent(
				new ApplicationStartedEvent(this.application, this.args, context));
	}
```

16. 执行所有的Runner执行器

```java
callRunners(context, applicationArguments);

	private void callRunners(ApplicationContext context, ApplicationArguments args) {
		List<Object> runners = new ArrayList<>();
		runners.addAll(context.getBeansOfType(ApplicationRunner.class).values());
		runners.addAll(context.getBeansOfType(CommandLineRunner.class).values());
		AnnotationAwareOrderComparator.sort(runners);
		for (Object runner : new LinkedHashSet<>(runners)) {
			if (runner instanceof ApplicationRunner) {
				callRunner((ApplicationRunner) runner, args);
			}
			if (runner instanceof CommandLineRunner) {
				callRunner((CommandLineRunner) runner, args);
			}
		}
	}
```

17. 发布应用上下文就绪事件

```java
listeners.running(context);

	public void running(ConfigurableApplicationContext context) {
		for (SpringApplicationRunListener listener : this.listeners) {
			listener.running(context);
		}
	}

	@Override
	public void running(ConfigurableApplicationContext context) {
		context.publishEvent(
				new ApplicationReadyEvent(this.application, this.args, context));
	}
```

18. 返回应用上下文

  