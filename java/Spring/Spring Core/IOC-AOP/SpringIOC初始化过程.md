```java
AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(JobService.class);
        for (String beanname:context.getBeanDefinitionNames())
        {
            System.out.println("--------"+beanname);
        }
        System.out.println("context.getBean(JobService.class) = " + context.getBean(JobService.class));
```

这点代码很简单 初始化bean，然后再来拿bean，我们点进AnnotationConfigApplicationContext来看

```java
public AnnotationConfigApplicationContext(Class<?>... annotatedClasses)
{
   this();
   register(annotatedClasses);
   refresh();
}
```

进⼊之后 会调用 this()默认无参构造方法

```java
public AnnotationConfigApplicationContext() {
   this.reader = new AnnotatedBeanDefinitionReader(this);
   this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

调⽤这个⽆参构造⽅法的同时 他会调用⽗类的构造方法，在调用父类构造⽅方法时 他new了一个对象

```java
public GenericApplicationContext() {
   this.beanFactory = new DefaultListableBeanFactory();
}
```

也就是 DefaultListableBeanFactory，当然 这个就是所谓我们平常所说的 bean工厂，其父类就是 BeanFactory，BeanFactory有很多子类，DefaultListableBeanFactory就是其中一个⼦类。 那么 bean的⽣命周期是围绕那个⽅法呢，就是refresh()⽅法。也就是bean的整个生命周期是围绕refresh() 来进行的

在refresh()我们可以看到

```java
public void refresh() throws BeansException, IllegalStateException {
   synchronized (this.startupShutdownMonitor) {
      // 准备好刷新上下文.
      prepareRefresh();

      // 返回一个Factory 为什么需要返回一个工厂  因为要对工厂进行初始化
      ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

      // 准备bean工厂，以便在此上下文中使用。
      prepareBeanFactory(beanFactory);

      try {
         // 允许在上下文子类中对bean工厂进行后处理。 在spring5  并未对此接口进行实现
         postProcessBeanFactory(beanFactory);

         // 在spring的环境中去执行已经被注册的 Factory processors
         //设置执行自定义的postProcessBeanFactory和spring内部自己定义的
         invokeBeanFactoryPostProcessors(beanFactory);

         // 注册postProcessor
         registerBeanPostProcessors(beanFactory);

         // 初始化此上下文的消息源。
         initMessageSource();

         // 初始化此上下文的事件多播程序。
         initApplicationEventMulticaster();

         // 在特定上下文子类中初始化其他特殊bean。
         onRefresh();

         //检查侦听器bean并注册它们。
         registerListeners();

         // 实例化所有剩余的(非懒加载)单例。
         //new 单例对象
         finishBeanFactoryInitialization(beanFactory);

         // 最后一步:发布相应的事件
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

那么这里面最重要就是finishBeanFactoryInitialization(beanFactory);这个方法就是描述 spring的一个bean如何初始化

```java
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
   // Initialize conversion service for this context.
   if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
         beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
      beanFactory.setConversionService(
            beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
   }

   // Register a default embedded value resolver if no bean post-processor
   // (such as a PropertyPlaceholderConfigurer bean) registered any before:
   // at this point, primarily for resolution in annotation attribute values.
   if (!beanFactory.hasEmbeddedValueResolver()) {
      beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
   }

   // Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
   String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
   for (String weaverAwareName : weaverAwareNames) {
      getBean(weaverAwareName);
   }

   // Stop using the temporary ClassLoader for type matching.
   beanFactory.setTempClassLoader(null);

   // Allow for caching all bean definition metadata, not expecting further changes.
   beanFactory.freezeConfiguration();

   // 实例化所有单例对象
   beanFactory.preInstantiateSingletons();
}
```

可以看到前面是一些判断 最重要的就是最后一个方法 beanFactory.preInstantiateSingletons();我们看下preInstantiateSingletons()方法，它是ConfigurableListableBeanFactory这个接口的一个方法 我们直接来看这个接口的实现 是由DefaultListableBeanFactory这个类 来实现

```java
@Override
public void preInstantiateSingletons() throws BeansException {
   if (logger.isDebugEnabled()) {
      logger.debug("Pre-instantiating singletons in " + this);
   }

   // Iterate over a copy to allow for init methods which in turn register new bean definitions.
   // While this may not be part of the regular factory bootstrap, it does otherwise work fine.
   //所有bean的名字
   List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

   // Trigger initialization of all non-lazy singleton beans...
   for (String beanName : beanNames) {
      RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
      if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
         if (isFactoryBean(beanName)) {
            Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
            if (bean instanceof FactoryBean) {
               final FactoryBean<?> factory = (FactoryBean<?>) bean;
               boolean isEagerInit;
               if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                  isEagerInit = AccessController.doPrivileged((PrivilegedAction<Boolean>)
                              ((SmartFactoryBean<?>) factory)::isEagerInit,
                        getAccessControlContext());
               }
               else {
                  isEagerInit = (factory instanceof SmartFactoryBean &&
                        ((SmartFactoryBean<?>) factory).isEagerInit());
               }
               if (isEagerInit) {
                  getBean(beanName);
               }
            }
         }
         else {
            getBean(beanName);
         }
      }
   }
```

我们可以看到用

List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

来保存bean的名字，那this.beanDefinitionNames是一个什么东西

```java
/** List of bean definition names, in registration order */
private volatile List<String> beanDefinitionNames = new ArrayList<>(256);
```

可以看到是beanDefinition的一个名字，那beanDefinition是个什么呢？它是spring当中非常重要的一个概念，在这里简单的提一嘴 我们传统用纯java的方式怎么new对象 是 Student stu=new Student();的方式来实例化对象，但是要交给spring的话，

先通过springScan的方式扫描到类，当他扫描到的时候 他会去new一个beanDefinition对象 他有很多子类 比如说 GenericBeanDefinition generic=new GenericBeanDefinition();然后他会把扫描到的类的各种信息给拿出来，比如说Student的名字是什么：

```java
GenericBeanDefinition generic=new GenericBeanDefinition();
    generic.setBeanClassName("studentService"); 类的名字
    generic.setBeanClass(StudentService.class); 类路径
    generic.isSingleton(); 包括是单例还是原型
```

等等很多很多，然后把这个对象放到哪里呢？ 在DefaultListableBeanFactory中有一个Map，叫做

```java
private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
```

就放到这个Map当中 也就是

beanDefinitionMap.put("studentService",generic)这样放进去之后，spring会看你有没有调用拓展接口，拓展接口我们到后面再说 如果没有拓展接口，就会继续调用我们刚刚接着继续的 preInstantiateSingletons()方法，这个方法来完成bean的实例化 总的一句话 BeanDefinition就是用来描述bean的，当然BeanDefinition的知识不仅仅是这些，今天只是简单提一嘴

那么我们现在继续 beanDefinitionNames也就是刚刚那个Map中Key的集合，然后开始循环

```java
//触发所有非延迟加载单例beans的初始化，只要步骤调用getBean
//根据List名字从Map当中把BeanDefinition依次拿出来开始new对象
for (String beanName : beanNames) {
  //合并父BeanDefinition
  //通过Map的名字拿BeanDefinition
   RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
    //判断当前类是否抽象 是否为单例  是否为懒加载  如果条件都成立 则继续
   if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
     //判断该类是否为FactoryBean FactoryBean这里不进行讲解 不懂得朋友可以去了解下
     //如果不是FactoryBean
      if (isFactoryBean(beanName)) {
         Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
         if (bean instanceof FactoryBean) {
            final FactoryBean<?> factory = (FactoryBean<?>) bean;
            boolean isEagerInit;
            if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
               isEagerInit = AccessController.doPrivileged((PrivilegedAction<Boolean>)
                           ((SmartFactoryBean<?>) factory)::isEagerInit,
                     getAccessControlContext());
            }
            else {
               isEagerInit = (factory instanceof SmartFactoryBean &&
                     ((SmartFactoryBean<?>) factory).isEagerInit());
            }
            if (isEagerInit) {
               getBean(beanName);
            }
         }
      }
      else {
        //则直接调用getBean
         getBean(beanName);
      }
   }
}
```

```java
@Override
//空方法
public Object getBean(String name) throws BeansException {
   return doGetBean(name, null, null, false);
}
```

```java
protected <T> T doGetBean(final String name, @Nullable final Class<T> requiredType,
      @Nullable final Object[] args, boolean typeCheckOnly) throws BeansException {

  /**
  *通过name获取beanName。这里不使用name直接做beanName
  * name可能会以&字符开头，表用调用者想获取FactoryBean本身，而非FactoryBean
  *实现类所创建的bean。在BeanFactory中，FactoryBean的实现类和其他的bean存储方式是一致的即
  * <beanName, bean> ,beanName中没有&字符的。所以我们需要将name的首字母&移除，这样才能取到
  *FactoryBean实例
  */
   final String beanName = transformedBeanName(name);
   Object bean;

   // Eagerly check singleton cache for manually registered singletons.
   Object sharedInstance = getSingleton(beanName);
   if (sharedInstance != null && args == null) {
      if (logger.isDebugEnabled()) {
         if (isSingletonCurrentlyInCreation(beanName)) {
            logger.debug("Returning eagerly cached instance of singleton bean '" + beanName +
                  "' that is not fully initialized yet - a consequence of a circular reference");
         }
         else {
            logger.debug("Returning cached instance of singleton bean '" + beanName + "'");
         }
      }
      bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
   }

   else {
      // Fail if we're already creating this bean instance:
      // We're assumably within a circular reference.
      if (isPrototypeCurrentlyInCreation(beanName)) {
         throw new BeanCurrentlyInCreationException(beanName);
      }

      // Check if bean definition exists in this factory.
      BeanFactory parentBeanFactory = getParentBeanFactory();
      if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
         // Not found -> check parent.
         String nameToLookup = originalBeanName(name);
         if (parentBeanFactory instanceof AbstractBeanFactory) {
            return ((AbstractBeanFactory) parentBeanFactory).doGetBean(
                  nameToLookup, requiredType, args, typeCheckOnly);
         }
         else if (args != null) {
            // Delegation to parent with explicit args.
            return (T) parentBeanFactory.getBean(nameToLookup, args);
         }
         else {
            // No args -> delegate to standard getBean method.
            return parentBeanFactory.getBean(nameToLookup, requiredType);
         }
      }

      if (!typeCheckOnly) {
         markBeanAsCreated(beanName);
      }

      try {
         final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
         checkMergedBeanDefinition(mbd, beanName, args);

         // 一个注解叫@DependsOn：A类创建 必须B类创建出来再创建A类
         String[] dependsOn = mbd.getDependsOn();
         if (dependsOn != null) {
            for (String dep : dependsOn) {
               if (isDependent(beanName, dep)) {
                  throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                        "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
               }
               registerDependentBean(dep, beanName);
               try {
                  getBean(dep);
               }
               catch (NoSuchBeanDefinitionException ex) {
                  throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                        "'" + beanName + "' depends on missing bean '" + dep + "'", ex);
               }
            }
         }

         // Create bean instance.
         if (mbd.isSingleton()) {
           //真正开始创建对象
            sharedInstance = getSingleton(beanName, () -> {
               try {
                  return createBean(beanName, mbd, args);
               }
               catch (BeansException ex) {
                  // Explicitly remove instance from singleton cache: It might have been put there
                  // eagerly by the creation process, to allow for circular reference resolution.
                  // Also remove any beans that received a temporary reference to the bean.
                  destroySingleton(beanName);
                  throw ex;
               }
            });
            bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
         }

         else if (mbd.isPrototype()) {
            // It's a prototype -> create a new instance.
            Object prototypeInstance = null;
            try {
               beforePrototypeCreation(beanName);
               prototypeInstance = createBean(beanName, mbd, args);
            }
            finally {
               afterPrototypeCreation(beanName);
            }
            bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
         }

         else {
            String scopeName = mbd.getScope();
            final Scope scope = this.scopes.get(scopeName);
            if (scope == null) {
               throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
            }
            try {
               Object scopedInstance = scope.get(beanName, () -> {
                  beforePrototypeCreation(beanName);
                  try {
                     return createBean(beanName, mbd, args);
                  }
                  finally {
                     afterPrototypeCreation(beanName);
                  }
               });
               bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
            }
            catch (IllegalStateException ex) {
               throw new BeanCreationException(beanName,
                     "Scope '" + scopeName + "' is not active for the current thread; consider " +
                     "defining a scoped proxy for this bean if you intend to refer to it from a singleton",
                     ex);
            }
         }
      }
      catch (BeansException ex) {
         cleanupAfterBeanCreationFailure(beanName);
         throw ex;
      }
   }

   // Check if required type matches the type of the actual bean instance.
   if (requiredType != null && !requiredType.isInstance(bean)) {
      try {
         T convertedBean = getTypeConverter().convertIfNecessary(bean, requiredType);
         if (convertedBean == null) {
            throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
         }
         return convertedBean;
      }
      catch (TypeMismatchException ex) {
         if (logger.isDebugEnabled()) {
            logger.debug("Failed to convert bean '" + name + "' to required type '" +
                  ClassUtils.getQualifiedName(requiredType) + "'", ex);
         }
         throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
      }
   }
   return (T) bean;
}
```

Object sharedInstance = getSingleton(beanName);重点为这行代码，我们点进去getSingleton(beanName)这个方法来看

```java
@Override
@Nullable
public Object getSingleton(String beanName) {
   return getSingleton(beanName, true);
}

/**
 * Return the (raw) singleton object registered under the given name.
 * <p>Checks already instantiated singletons and also allows for an early
 * reference to a currently created singleton (resolving a circular reference).
 * @param beanName the name of the bean to look for
 * @param allowEarlyReference whether early references should be created or not
 * @return the registered singleton object, or {@code null} if none found
 */
@Nullable
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
  
   Object singletonObject = this.singletonObjects.get(beanName);
  
      
   if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
      synchronized (this.singletonObjects) {
         singletonObject = this.earlySingletonObjects.get(beanName);
         if (singletonObject == null && allowEarlyReference) {
            ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
            if (singletonFactory != null) {
               singletonObject = singletonFactory.getObject();
               this.earlySingletonObjects.put(beanName, singletonObject);
               this.singletonFactories.remove(beanName);
            }
         }
      }
   }
   return singletonObject;
}
```

正在要new一个对象的时候 他会调用getSingleton方法，那么在这个方法中

Object singletonObject = this.singletonObjects.get(beanName);

这行代码是本质，那他是什么东西 我们点进来看

```java
/** Cache of singleton objects: bean name --> bean instance */
//用于存放完全初始化好的bean从该缓存中取出bean可以直接使用
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
```

很明显 他就是个Map ConcurrentHashMap线程安全的Map，什么意思 这个Map也就是我们常常说说的Spring的容器 如果从微观角度来讲 没错spring容器就是这个Map，如果从宏观上讲，他就不能称之为map 我希望大家能理解什么意思，因为spring环境包含很多组件，各种组件包含在一起以及singletonObjects以及一些后置处理器配合完成一些工作我们称之为Spring容器。

singletonObjects 是什么？没错他就是单例池 他什么要在单例池拿？为什么要调用他？

因为他要解决Spring当中的循环依赖问题，相信大家既然有看源码的能力，相信大家也明白什么是循环依赖这里不再细说

在这个类里也有介绍

```java
/** Cache of early singleton objects: bean name --> bean instance */
//存放原始的bean对象用于解决循环依赖，注意：存到里面的对象还没被填充到属性
private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
```

什么意思 一个bean放到singletonObjects之后，把一个对象new出来之后，如果这个对象要循环引用那spring就会先把他放到earlySingletonObjects这个当中.get是因为怕对象已经放到early当中所以先去get一遍，这里也只是简单题一嘴

那我们继续看

if (sharedInstance != null && args == null) {如果条件成立的话

bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);直接返回调

如果条件不成立直接else

if (isPrototypeCurrentlyInCreation(beanName)) 判断这个类是否正在创建，什么意思 因为spring正在创建一类的时候他会进行标识这个类我正在创建中，然后获取bean工厂

BeanFactory parentBeanFactory = getParentBeanFactory();这里不重要

我们继续来看

sharedInstance = getSingleton(beanName, () -> { try { return createBean(beanName, mbd, args); } 到这一步的时候，才开始真正创建对象，这里又一个getSingleton这个方法跟上面那个不是同一个方法，这个地方用到lambdas表达式，我们点进去看这个getSingleton

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
   Assert.notNull(beanName, "Bean name must not be null");
   synchronized (this.singletonObjects) {
      Object singletonObject = this.singletonObjects.get(beanName);
      if (singletonObject == null) {
         if (this.singletonsCurrentlyInDestruction) {
            throw new BeanCreationNotAllowedException(beanName,
                  "Singleton bean creation not allowed while singletons of this factory are in destruction " +
                  "(Do not request a bean from a BeanFactory in a destroy method implementation!)");
         }
         if (logger.isDebugEnabled()) {
            logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
         }
         beforeSingletonCreation(beanName);
         boolean newSingleton = false;
         boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
         if (recordSuppressedExceptions) {
            this.suppressedExceptions = new LinkedHashSet<>();
         }
         try {
            singletonObject = singletonFactory.getObject();
            newSingleton = true;
         }
         catch (IllegalStateException ex) {
            // Has the singleton object implicitly appeared in the meantime ->
            // if yes, proceed with it since the exception indicates that state.
            singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
               throw ex;
            }
         }
         catch (BeanCreationException ex) {
            if (recordSuppressedExceptions) {
               for (Exception suppressedException : this.suppressedExceptions) {
                  ex.addRelatedCause(suppressedException);
               }
            }
            throw ex;
         }
         finally {
            if (recordSuppressedExceptions) {
               this.suppressedExceptions = null;
            }
            afterSingletonCreation(beanName);
         }
         if (newSingleton) {
            addSingleton(beanName, singletonObject);
         }
      }
      return singletonObject;
   }
}
```

Object singletonObject = this.singletonObjects.get(beanName);

这里关键：

第一个getSingleton：单例池拿 拿不到到缓存池拿，拿不到返回null 第二个：也是先从单例池拿 如果为null， if (this.singletonsCurrentlyInDestruction)判断对象有没有开始创建，

然后 beforeSingletonCreation(beanName);我们点进去来看

protected void beforeSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }
    }

singletonsCurrentlyInCreation是什么意思

```java
/** Names of beans that are currently in creation */
private final Set<String> singletonsCurrentlyInCreation =
      Collections.newSetFromMap(new ConcurrentHashMap<>(16));
```

也是一个Lsit，什么意思 把它放到这个List当中表示正在创建

紧接着调用createBean(beanName, mbd, args);开始创建对象

我们来看createBean

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
      throws BeanCreationException {

   if (logger.isDebugEnabled()) {
      logger.debug("Creating instance of bean '" + beanName + "'");
   }
   RootBeanDefinition mbdToUse = mbd;

   // Make sure bean class is actually resolved at this point, and
   // clone the bean definition in case of a dynamically resolved Class
   // which cannot be stored in the shared merged bean definition.
   Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
   if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
      mbdToUse = new RootBeanDefinition(mbd);
      mbdToUse.setBeanClass(resolvedClass);
   }

   // Prepare method overrides.
   try {
      mbdToUse.prepareMethodOverrides();
   }
   catch (BeanDefinitionValidationException ex) {
      throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(),
            beanName, "Validation of method overrides failed", ex);
   }

   try {
      // Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
      Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
      if (bean != null) {
         return bean;
      }
   }
   catch (Throwable ex) {
      throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,
            "BeanPostProcessor before instantiation of bean failed", ex);
   }

   try {
      Object beanInstance = doCreateBean(beanName, mbdToUse, args);
      if (logger.isDebugEnabled()) {
         logger.debug("Finished creating instance of bean '" + beanName + "'");
      }
      return beanInstance;
   }
   catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
      // A previously detected exception with proper bean creation context already,
      // or illegal singleton state to be communicated up to DefaultSingletonBeanRegistry.
      throw ex;
   }
   catch (Throwable ex) {
      throw new BeanCreationException(
            mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", ex);
   }
}
```

到这里 不要以为完了，其实还没完最重要的部分

Object bean = resolveBeforeInstantiation(beanName, mbdToUse);

这个是第一次调用spring的后置处理器，spring的生命周期一共围绕了9个后置处理器，这个是第一个

我们先不管这些后置处理器是干嘛的 先把他找出来我们点进resolveBeforeInstantiation

```java
@Nullable
protected Object  resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
   Object bean = null;
   if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
      // Make sure bean class is actually resolved at this point.
      if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
         Class<?> targetType = determineTargetType(beanName, mbd);
         if (targetType != null) {
            bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
            if (bean != null) {
               bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
            }
         }
      }
      mbd.beforeInstantiationResolved = (bean != null);
   }
   return bean;
}
```

bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);点进来看

```java
@Nullable
protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
   for (BeanPostProcessor bp : getBeanPostProcessors()) {
      if (bp instanceof InstantiationAwareBeanPostProcessor) {
         InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
         Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
         if (result != null) {
            return result;
         }
      }
   }
   return null;
}
```

他会先拿出来所有后置处理器 然后判断是不是属于他

我们返回刚刚

Object beanInstance = doCreateBean(beanName, mbdToUse, args);

在doCreateBean里面调用第二个后置处理器，我这里都不再一一寻找了，直接列出来吧

 

第一次：InstantiationAwareBeanPostProcessor --postProcessBeforeInstantiation

第二次：SmartInstantiationAwareBeanPostProcessor—determineCandidateConstructors—由后置处理器决定返回那些构造方法

第三次：MergedBeanDefinitionPostProcessor——postProcessMergedBeanDefinition------缓存的

第四次：SmartInstantiationAwareBeanPostProcessor—getEarlyBeanReference----把对象放到Early当中--处理循环引用

第五次：InstantiationAwareBeanPostProcessor—postProcessAfterInstantiation---判断要不要填充属性

第六次：InstantiationAwareBeanPostProcessor—postProcessPropertyValues—处理属性的值

第七次：BeanPostProcessor—postProcessBeforeInitialization —处理AOP

第八次：BeanPostProcessor----postProcessAfterInitialization

第九次为销毁

 

那么到这里一个完整的springBean的初始化。