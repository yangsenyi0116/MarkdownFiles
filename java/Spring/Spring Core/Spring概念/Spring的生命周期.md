![img](F:\OneDrive\MarkDown\images\181453414212066.png)

![img](F:\OneDrive\MarkDown\images\181454040628981.png)

1. 实例化BeanFactoryPostProcessor实现类
2. 执行BeanFactoryPostProcessor的postProcessBeanFactory方法
3. 实例化BeanPostProcessor实现类
4. 实例化InstantiationAwareBeanPostProcessorAdapter实现类
5. 执行InstantiationAwareBeanPostProcessor的postProcessBeforeInstantiation方法
6. 执行Bean的构造器
7. 执行InstantiationAwareBeanPostProcessor的postProcessPropertyValues方法
8. 为Bean注入属性
9. 执行BeanNameAware的setBeanName方法
10. 调用BeanFactoryAware的setBeanFactory方法
11. 执行BeanPostProcessor的postProcessBeforeInitializtion方法
12. 调用InitializingBean的afterPropertiesSet方法
13. 调用<bean>的init-method属性指定的初始化方法
14. 执行BeanPostProcessor的postProcessAfterInitialization方法
15. 执行InstantiationAwareBeanPostProcessor的postProcessAfterInitialization方法
16. 容器初始化成功，正常调用后，销毁容器
17. 调用DiposibleBean的destory方法
18. 调用<bean>的destory-method属性指定的初始化方法



上边划分一下

Bean的完整生命周期经历了一下几种方法调用

1. Bean自身的方法： Bean本身调用的方法和通过配置文件中<bean>的init-method和destory-method指定的方法
2. Bean级生命周期接口方法：这个包括了BeanNameAware、BeanFactoryAware、InitializingBean和DiposableBean这些接口方法
3. 容器及生命周期接口方法：这个包括了InstantiationAwareBeanPostProcessor和BeanPostProcessor这两个接口实现，一般称他们为后置处理器
4. 工厂后处理器接口方法： 这个包括了AspectJWeavingEnabler，ConfigurationClassPostProcessor，CustomAutowireConfigurer等等非常有用的工厂后处理器接口的方法。工厂后处理器也是容器级的。在应用上下文装配配置文件之后立即调用



## JavaGuide

1. 实例化Bean对象
2. 设置对象属性
3. 检查Aware相关接口并设置相关依赖
4. BeanPostProcessor前置处理
5. 检查是否有InitializingBean以决定是否调用afterPropertiesSet方法
6. 检查是否有配置的自定义的init-method
7. BeanPostProcessor后置处理
8. 注册必要的Destruction相关回调接口
9. 使用
10. 是否实现DisposableBean接口
11. 是否配置有自定义的destory方法





- Bean容器找到配置文件中 Spring Bean 的定义。
- Bean容器利用Java Reflection API创建一个Bean的实例。
- 如果涉及到一些属性值 利用set方法设置一些属性值。
- 如果Bean实现了BeanNameAware接口，调用setBeanName()方法，传入Bean的名字。
- 如果Bean实现了BeanClassLoaderAware接口，调用setBeanClassLoader()方法，传入ClassLoader对象的实例。
- 如果Bean实现了BeanFactoryAware接口，调用setBeanClassLoader()方法，传入ClassLoader对象的实例。
- 与上面的类似，如果实现了其他*Aware接口，就调用相应的方法。
- 如果有和加载这个Bean的Spring容器相关的BeanPostProcessor对象，执行postProcessBeforeInitialization()方法
- 如果Bean实现了InitializingBean接口，执行afterPropertiesSet()方法。
- 如果Bean在配置文件中的定义包含init-method属性，执行指定的方法。
- 如果有和加载这个Bean的Spring容器相关的BeanPostProcessor对象，执行postProcessAfterInitialization()方法
- 当要销毁Bean的时候，如果Bean实现了DisposableBean接口，执行destroy()方法。
- 当要销毁Bean的时候，如果Bean在配置文件中的定义包含destroy-method属性，执行指定的方法。





## Bean 的初始化过程

  一个类同时实现了 `BeanNameAware, BeanFactoryAware, ApplicationContextAware, BeanPostProcessor, InitializingBean, DisposableBean`接口时，`Bean` 的初始化过程为：.

1. 调用 `BeanNameAware` 的 `setBeanName` 方法
2. 调用 `BeanFactoryAware` 的 `setBeanFactory` 方法
3. 调用 `ApplicationContextAware` 的 `setApplicationContext`
4. 调用 `InitializingBean` 的 `afterPropertiesSet` 或者没有实现这个接口，但指定了`@Bean(initMethod="不加括号的方法名")`，会执行这个方法
5. 调用 `BeanPostProcessor` 的 `postProcessBeforeInitialization` 方法
6. 调用 `BeanPostProcessor` 的 `postProcessAfterInitialization` 方法
7. `Bean` 初始化完成，可以被使用
8. 容器关闭前，调用 `DisposableBean` 的 `destroy` 方法

  但是在整个系统中，`BeanPostProcessor`的实现类只需要有一个即可，`Spring` 检测到它的存在时，每个 `Bean` 被初始化时，都会调用它的方法。注意，是所有 Bean 都会调用它的方法。
  如果一个类除`BeanPostProcessor`外，实现了其他的接口，有另外一个类单独实现了`BeanPostProcessor`接口，那么上面的初始化过程中，第 4 步和第 5 步调换位置。