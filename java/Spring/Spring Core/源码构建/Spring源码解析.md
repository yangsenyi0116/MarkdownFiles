AbstractApplicationContext.java


```java
//完成扫描
invokeBeanFactoryPostProcessors(beanFactory);
//spring开始实例化单例的类
finishBeanFactoryInitialization(beanFactory);

//实例化所有的单例,非lazy
beanFactory.preInstantiateSingletons();
```

DefaultListableBeanFactory.java

```java
//得到所有的beanDefinition名字
List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);
//遍历所有的beanDefinition根据名字，继而验证beanDefinition
for (String beanName : beanNames) {
			RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
    		//验证是否是单例和烂加载
			if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
				if (isFactoryBean(beanName)) {
					Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
					if (bean instanceof FactoryBean) {
						FactoryBean<?> factory = (FactoryBean<?>) bean;
						boolean isEagerInit;
						if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
							isEagerInit = AccessController.doPrivileged(
									(PrivilegedAction<Boolean>) ((SmartFactoryBean<?>) factory)::isEagerInit,
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
                    //开始实例普通的bean
					getBean(beanName);
				}
			}
		}
```

AbstractBeanFactory.java

```java
		// Eagerly check singleton cache for manually registered singletons.
		/**
		 * 第一次调用getSingleton方法，下面Spring还会调用一次
		 * 但两次调用的不是同一个方法；属于方法的重载
		 * 第一次getSingleton(beanName)也是循环依赖最重要的方法
		 * 关于getSingleton(beanName)具体代码分析可以参考笔者后面的分析
		 * 这里给出这个方法的总结
		 * 首先spring会去单例池去根据名字获取这个bean，单例池就是一个map
		 * 如果对象被创建了则直接从map中拿出来并且返回
		 * 但是问题来饿了，为什么spring在创建一个bean的时候还会去获取一次呢？
		 * 因为作为代码的书写者肯定知道这个bean这个时候没有创建？为什么要get一次呢
		 * 关于这个问题其实原因比较复杂，需要读者对spring源码设计比较精通
		 *
		 * 我们可以分析doGetBean这个方法，顾名思义其实是用来获取bean的
		 * 为什么创建bean会调用这个doGetBean方法呢？
		 * doGetBean这个方法不仅仅在创建bean的时候会被调用，在getBean的时候也会被调用
		 * 他是创建bean和getBean通用的方法，但是这只是解释了这个方法名字的意义
		 * 并没有解释这个方法为什么会在创建bean的时候调用
		 * 和本文相关的就是因为循环引用
		 * 由于循环引用需要在创建bean的过程中去获取被调用的那个类
		 * 而被引用的这个类如果没有创建，则会调用createBean来创建这个bean
		 * 在创建这个被引用的bean的过程中会判断这个bean的对象有没有实例化
		 * bean的对象？什么意思呢？
		 * 什么是bean，什么是对象？
		 * 一个对象和bean是有区别的
		 * 对象：只要类被实例化就可以被称为对象
		 * bean：首先得是一个对象，然后这个对象需要经历一系列的bean生命周期
		 * 最后把这个对象put到代理池才能算一个bean
		 * 简而言之就是spring先new一个对象，继而对这个对象进行生命周期回调
		 * 接着对这个对象进行属性填充，也是大家说的自动注入
		 * 然后在进行AOP判断等等，这一系列操作简称------spring的生命周期
		 * 所以一个bean是一个经历了spring周期的对象，和一个对象有区别
		 * 再回到前面说的循环引用，首先spring扫描到一个需要被实例化的类A
		 * 于是spring就去创建A；A a = new A;new A的过程会调用getBean("a")
		 * 所谓的getBean方法，核心也就是现在写着注解的这个getSingleton(beanName)
		 * 这个时候get出来肯定为空？为什么为空
		 * 很多读者认为getBean就是从容器中获取，所以所以为空，其实不然
		 * 如果getA等于空，spring就会实例A，也就是上面的new A
		 * 但是在实例化A的时候会再次调用一下
		 * getSingleton(Stirng beanName,ObjectFactory<?> singletonFactoy)
		 * 笔者上面说过现在写的注释给getSingleton(beanName)
		 * 也即是第一次调用getSingleton(beanName)
		 * 实例化一共会调用两次getSingleton方法，但是是重载
		 * 第二次调用getSingleton方法的时候spring会在一个set集合当中记录一下这个类正在被创建
		 * 这个一定要记住，在调用完成第一次getSingleton完成之后
		 * spring判断这个类没有创建，然后调用第二次getSingleton
		 * 在第二次getSingleton里面记录了一下自己已经开始实例化这个类
		 * 这是循环依赖做的最牛的地方，两次getSingleton的调用
		 * 也是回答面试时候关于循环依赖必须要回答到的地方
		 * 需要说明的spring实例化一个对象底层用的是反射
		 * spring实例化一个对象的过程非常复杂，需要推断构造方法等
		 * 读者可以理解spring直接通过new关键字来实例化一个对象
		 * 但是这个时候对象a仅仅是一个对象，而不是一个完整的bean
		 * 接着让这个对象去完成spring的bean的生命周期
		 * 过程中spring会判断容器是否允许循环引用
		 * 如果允许循环依赖，spring会把这个对象(还不是bean)临时存起来，放到一个map中
		 * 注意这个map和单例池是两个map，在spring源码中单例池的map叫做singletonObejcts
		 * 所以一共是三个map，有些博客或者书中也叫做三级缓存
		 *
		 * 第一个map singletonObjects 存放单例的bean
		 * 第二个map singletonFactories 存放的临时对象，没有完整的springBean审核革命周期的对象
		 * 第三个map earlySingletonObject 存放的临时对象， 没有完整的springBean生命周期的对象
		 * 其实第二个和第三个map会有不一样的地方
		 * 第一个map主要为了直接缓存创建好的bean，方便程序员去getBean
		 * 第二个和第三个map主要为了循环引用，为什么为了方便循环引用？
		 * 把对象a缓存到第二个map之后，会接着完善生命周期
		 * 当然springbean的生命周期很多过程
		 *
		 * 当进行到对象a的属性填充这一周期的时候，发觉a依赖了一个B类
		 * 所以spring就会去判断这个B类到底有没有bean在容器当中
		 * 这里的判断就是从第一个map即单例池当中去拿一个bean
		 *
		 * 假设没有取到，那么spring先会去掉crateBean去创建这个bean
		 * 于是又回到创建A一样的流程，在创建B的时候也同样回去getBean("B")
		 * getBean核心也就是笔者现在写注释的这个getSingleton(beanName)方法
		 *
		 * 这个时候get出来肯定为空？为什么为空呢？
		 * 第一次调用完getSingleton完成之后会调用第二次getSingleton
		 * 第二次调用getSingleton同样会在set集合当中去记录B正在被创建
		 * 此时set集合至少有两个记录了A和B
		 * 如果为空就b = new B()创建一个b对象
		 *
		 * 创建完B的对象之后，接着完善B的生命周期
		 * 同样也会判断是否允许循环依赖，如果允许则把对象b存到第二个map当中
		 * 提醒一下这个时候第二个map当中至少有两个对象了a和b
		 * 接着继续生命周期，当进行到b对象的属性填充的时候发觉b需要依赖A
		 * 于是就去容器看看A有没有被创建，说白了就是从第一个map当中去找a
		 * 有人会说在上边已近创建了a吗？ 注意那只是个对象，不是bean
		 * 还不在第一个map当中,所以b判定A没有创建，于是就去创建A
		 * 那么又再次回到原点了，创建A的过程中，首先调用getBean("a")
		 * 上文说到getBean("a")的核心就是getSingleton(beanName)
		 * 上文也说了get出来a==null；但是这次却不等于空了
		 * 这次能拿出来一个a对象；注意是对象，不是bean
		 * 为什么两次不同？原因在于getSingleton(beanName)的源码
		 * getSingleton(beanName)首先从第一个map当中获取bean
		 * 这里就是获取a；但是获取不到，然后判断a是不是等于空
		 * 如果等于空则再判断a是不是正在创建？
		 * 就是判断a的那个set集合当中有没有记录A
		 * 如果这个集合当中包含了A则直接把a对象从map当中get出来并且返回
		 * 所以这一次就不等于空了，于是B就可以自动注入这个a对象了
		 * 这个时候a还只是对象，a这个对象里面依赖的B还没有注入
		 * 当b对象注入完成a之后把B的周期走完，存到容器当中
		 * 存完之后继续返回，返回到a注入b那里
		 *
		 * 因为b的创建时因为a需要注入b,于是去get b
		 * 当b创建完成一个bean之后，返回b(b已经是一个bean了)
		 * 需要说明的是b是一个bean意味着b已经注入完成了a
		 * 由于返回了一个b，故而a也能注入b了
		 * 接着a对象继续完成生命周期，当走完之后a也在容器中了
		 * 至此循环依赖搞定
		 * 那些记录set的集合的名字叫做singletonsCurrentlyInCreation
		 *
		 */
		Object sharedInstance = getSingleton(beanName);
```