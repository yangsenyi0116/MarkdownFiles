### AOP概念

>  AOP（Aspect Oriented Programming），即面向切面编程，可以说是OOP（Object Oriented Programming，面向对象编程）的补充和完善。OOP引入封装、继承、多态等概念来建立一种对象层次结构，用于模拟公共行为的一个集合。不过OOP允许开发者定义纵向的关系，但并不适合定义横向的关系，例如日志功能。日志代码往往横向地散布在所有对象层次中，而与它对应的对象的核心功能毫无关系对于其他类型的代码，如安全性、异常处理和透明的持续性也都是如此，这种散布在各处的无关的代码被称为横切（cross cutting），在OOP设计中，它导致了大量代码的重复，而不利于各个模块的重用。

> ​	AOP技术恰恰相反，它利用一种称为"横切"的技术，剖解开封装的对象内部，并将那些影响了多个类的公共行为封装到一个可重用模块，并将其命名为"Aspect"，即切面。所谓"切面"，简单说就是那些与业务无关，却为业务模块所共同调用的逻辑或责任封装起来，便于减少系统的重复代码，降低模块之间的耦合度，并有利于未来的可操作性和可维护性。

> 使用"横切"技术，AOP把软件系统分为两个部分：**核心关注点**和**横切关注点**。业务处理的主要流程是核心关注点，与之关系不大的部分是横切关注点。横切关注点的一个特点是，他们经常发生在核心关注点的多处，而各处基本相似，比如权限认证、日志、事物。AOP的作用在于分离系统中的各种关注点，将核心关注点和横切关注点分离开来。



### AOP核心概念

1、横切关注点

对哪些方法进行拦截，拦截后怎么处理，这些关注点称之为横切关注点

2、切面（aspect）

类是对物体特征的抽象，切面就是对横切关注点的抽象

3、连接点（joinpoint）

被拦截到的点，因为Spring只支持方法类型的连接点，所以在Spring中连接点指的就是被拦截到的方法，实际上连接点还可以是字段或者构造器

4、切入点（pointcut）

对连接点进行拦截的定义

5、通知（advice）

所谓通知指的就是指拦截到连接点之后要执行的代码，通知分为前置、后置、异常、最终、环绕通知五类

6、目标对象

代理的目标对象

7、织入（weave）

将切面应用到目标对象并导致代理对象创建的过程

8、引入（introduction）

在不修改代码的前提下，引入可以在**运行期**为类动态地添加一些方法或字段



### Spring对AOP的支持

**Spring中AOP代理由Spring的IOC容器负责生成、管理，其依赖关系也由IOC容器负责管理**。因此，AOP代理可以直接使用容器中的其它bean实例作为目标，这种关系可由IOC容器的依赖注入提供。Spring创建代理的规则为：

1、**默认使用Java动态代理来创建AOP代理**，这样就可以为任何接口实例创建代理了

2、**当需要代理的类不是代理接口的时候，Spring会切换为使用CGLIB代理**，也可强制使用CGLIB

AOP编程其实是很简单的事情，纵观AOP编程，程序员只需要参与三个部分：

1、定义普通业务组件

2、定义切入点，一个切入点可能横切多个业务组件

3、定义增强处理，增强处理就是在AOP框架为普通业务组件织入的处理动作

所以进行AOP编程的关键就是定义切入点和定义增强处理，一旦定义了合适的切入点和增强处理，AOP框架将自动生成AOP代理，即：**代理对象的方法=增强处理+被代理对象**的方法。

下面给出一个Spring AOP的.xml文件模板，名字叫做aop.xml，之后的内容都在aop.xml上进行扩展：



```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xmlns:tx="http://www.springframework.org/schema/tx"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop-4.2.xsd">
            
</beans>
```

![1553155108782](E:\课程文件\markdown\image\%5CUsers%5Cvip87%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1553155108782.png)



### SpringAOP

 	aop是基于代理完成的，激活自动代理

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">
<!--aop是基于代理完成的，激活自动代理-->
    <aop:aspectj-autoproxy/>
    <!--注册一个切面要使用的类-->
    <bean class="kermi.project.maven.spring.advice.BeforeAdvice" id="beforeAdvice">

    </bean>

    <bean class="kermi.project.maven.spring.service.ProviderService" id="providerService">
        
    </bean>
    <!--配置切入点等信息-->
    <aop:config>
        <aop:aspect id="beforeAspect" ref="beforeAdvice">
            <!--aop：before 表明确实是前置通知
                method：指明它使用哪个方法来切
                pointcut:切入点
                什么包什么类下边的什么方法
            -->
            <aop:before method="methodBefore" pointcut="execution(* public kermi.project.maven.spring.service.*.*(..))"></aop:before>
        </aop:aspect>
    </aop:config>
</beans>
```





### AOP注解开发

```java
@Order(1)	//设置执行等级
@Aspect //标记为切面
@Component  //标记当前的这个类是spring的一个组件，相当于在xml中注册了一个bean
public class BeforeAdvice {


    public void methodBefore(){
        System.out.println("我在方法之前执行");
    }
	
    @Before("execution(* kermi..*.*.*(..))") //与xml方式中pointcut一致
    public void before(){
        System.out.println("在一个世纪以前");
    }
}
```







```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
<!--aop是基于代理完成的，激活自动代理-->
    <aop:aspectj-autoproxy/>

    <!--配置基础扫描包-->
    <context:component-scan base-package="kermi"/>
</beans>
```


execution: 以**方法作为切入点**的声明.

```
execution(public * *(..))  //所有的public方法,..不关心参数
execution(* *(..)) //表示所有方法
execution(* login(..)) //所有的login方法

// AccountService所有方法
execution(* com.xyz.service.AccountService.*(..)) 

//service包中所有的类中所有的方法(只关心当前service包中所有类)
execution(* com.xyz.service.*.*(..))

//service包及其子包中所有类的所有方法
execution(* com.xyz.service..*.*(..))

//参数为一个String的add方法
execution(* add(String))

//第一个参数为String的login方法
execution(* login(String,..))

```



within:将类作为切入点

```
//service包下面所有类都作为切入点(不包含子包中的类)
within(com.xyz.service.*)

//service包及其子包中所有类
within(com.xyz.service..*)

//如果是声明具体的类
within(com.xyz.service.impl.UserServiceImpl)
//如果是声明接口
within(com.xyz.service.UserService+)

```



args:将参数作为切入点

```
args(String)   //参数为String的方法
切入点表达式语句可以进行逻辑组合
	&& :表示与
	|| :表示或
	! :表示非
within(com.xyz.service.*)&& args(String)
execution(* login(..))||execution(* regist(..))
!execution(* login(..))

```



AOP环绕通知

![1553328196861](E:\课程文件\markdown\image\%5CUsers%5Cvip87%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1553328196861.png)

l  环绕通知: @Around

注意:

1. 该方法必须声明一个返回值.(目标方法执行的结果)
2. 该方法的第一个参数必须是ProceedingJoinPoint,可以调用目标对象的功能