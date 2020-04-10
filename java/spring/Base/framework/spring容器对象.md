### spring的容器对象

- BeanFacttory:对于容器中的对象采用延迟加载的策略
- ApplicationContext：ApplicationContext是BeanFactory的子接口，扩展了更多的功能，同时可以再实例化容器时会初始化容器中的对象

###  DI依赖注入

- setter注入
- 构造器注入
- 自动注入

### spring的表达式语言

通过spring 的表达式语言 快捷对对象的属性赋值

```xml
<bean p:sub="#{'haoren'.substring(3,6)}"></bean>
```

用#{}编写



### spring注解开发

设置注解扫描范围

annotation.xml

```xml
<context:compoent-scan base-package="包名"></context:compoent-scan>
```

会自动扫描annotation及其子包下的所有注解对象

```java
@Component			表明一个普通的spring组件
@Controller注解      只能用控制器类上
@Service注解         只能用在业务类上
@Repository注解      只能用在dao类上/注解一个持久层的类
```

## 常见注解

- configuration:标明一个类为配置类，程序启动时扫描这个类，就可以得到所有的配置规格
- Component：标明一个类为spring的一个组件，可以被spring容器所管理
- ComponentScan：组件扫描 ，去扫描一个包
- Bean：用于在spring容器中注册一个bean
- AutoWired:自动注入



