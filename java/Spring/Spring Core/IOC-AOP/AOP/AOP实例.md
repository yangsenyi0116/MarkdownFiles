一、XML方式

\1. TestAspect：切面类

```java
package com.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

public class TestAspect {

	public void doAfter(JoinPoint jp) {
		System.out.println("log Ending method: " + jp.getTarget().getClass().getName() + "." + jp.getSignature().getName());

	}

	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

		long time = System.currentTimeMillis();
		Object retVal = pjp.proceed();
		time = System.currentTimeMillis() - time;
		System.out.println("process time: " + time + " ms");
		return retVal;
	}

	public void doBefore(JoinPoint jp) {
		System.out.println("log Begining method: " + jp.getTarget().getClass().getName() + "." + jp.getSignature().getName());
	}

	public void doThrowing(JoinPoint jp, Throwable ex) {
		System.out.println("method " + jp.getTarget().getClass().getName() + "." + jp.getSignature().getName() + " throw exception");
		System.out.println(ex.getMessage());
	}
}
```

\2. AServiceImpl：目标对象

```java
package com.spring.service;

// 使用jdk动态代理
public class AServiceImpl implements AService {
	public void barA() {
		System.out.println("AServiceImpl.barA()");
	}

	public void fooA(String _msg) {
		System.out.println("AServiceImpl.fooA(msg:" + _msg + ")");
	}
}
```

\3. BServiceImpl：目标对象

```java
package com.spring.service;

// 使用cglib
public class BServiceImpl {
	public void barB(String _msg, int _type) {
		System.out.println("BServiceImpl.barB(msg:" + _msg + " type:" + _type + ")");
		if (_type == 1)
			throw new IllegalArgumentException("测试异常");
	}

	public void fooB() {
		System.out.println("BServiceImpl.fooB()");
	}

}
```

\4. ApplicationContext：Spring配置文件

```html
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
		http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.1.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.1.xsd
		http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.1.xsd">
    <aop:config>
        <aop:aspect id="TestAspect" ref="aspectBean">
            <!--配置com.spring.service包下所有类或接口的所有方法-->
            <aop:pointcut id="businessService" expression="execution(* com.spring.service.*.*(..))" />
            <aop:before pointcut-ref="businessService" method="doBefore"/>
            <aop:after pointcut-ref="businessService" method="doAfter"/>
            <aop:around pointcut-ref="businessService" method="doAround"/>
            <aop:after-throwing pointcut-ref="businessService" method="doThrowing" throwing="ex"/>
        </aop:aspect>
    </aop:config>
    <bean id="aspectBean" class="com.spring.aop.TestAspect" />
    <bean id="aService" class="com.spring.service.AServiceImpl"></bean>
    <bean id="bService" class="com.spring.service.BServiceImpl"></bean>
</beans>
```

 

二、注解（Annotation）方式

\1. TestAnnotationAspect

```java
package com.spring.aop;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class TestAnnotationAspect {

	@Pointcut("execution(* com.spring.service.*.*(..))")
	private void pointCutMethod() {
	}

	//声明前置通知
	@Before("pointCutMethod()")
	public void doBefore() {
		System.out.println("前置通知");
	}
    
	//声明后置通知
	@AfterReturning(pointcut = "pointCutMethod()", returning = "result")
	public void doAfterReturning(String result) {
		System.out.println("后置通知");
		System.out.println("---" + result + "---");
	}
	
	//声明例外通知
	@AfterThrowing(pointcut = "pointCutMethod()", throwing = "e")
	public void doAfterThrowing(Exception e) {
		System.out.println("例外通知");
		System.out.println(e.getMessage());
	}

	//声明最终通知
	@After("pointCutMethod()")
	public void doAfter() {
		System.out.println("最终通知");
	}
	//声明环绕通知
	@Around("pointCutMethod()")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("进入方法---环绕通知");
		Object o = pjp.proceed();
		System.out.println("退出方法---环绕通知");
		return o;
	}



}
```

\2. ApplicationContext：Spring配置文件

```java
<?xml version="1.0" encoding="UTF-8"?>



<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
		http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.1.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.1.xsd
		http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.1.xsd">
    <aop:aspectj-autoproxy></aop:aspectj-autoproxy>
    <bean class="org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator" />
    <bean id="aspectBean" class="com.spring.aop.TestAnnotationAspect" />
    <bean id="aService" class="com.spring.service.AServiceImpl"></bean>
    <bean id="bService" class="com.spring.service.BServiceImpl"></bean>

</beans>
```

 

关于切入点表达式，大家需要好好练习才能深入理解其中含义。即使看的懂，但是写起来却非常麻烦，并没有想象中那么简单。

最后，再告诉大家：

任何通知（Advice）方法可以将第一个参数定义为 org.aspectj.lang.JoinPoint类型。***\*JoinPoint\****接口提供了一系列有用的方法， 比如 getArgs() （返回方法参数）、getThis() （返回代理对象）、getTarget() （返回目标）、getSignature() （返回正在被通知的方法相关信息）和 toString() （打印出正在被通知的方法的有用信息。

其中getSignature()返回的***\*Signature\****对象可强制转换为***\*MethodSignature\****，其功能非常强大，能获取包括参数名称在内的一切方法信息。