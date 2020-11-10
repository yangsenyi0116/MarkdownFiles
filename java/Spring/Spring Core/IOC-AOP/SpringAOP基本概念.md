## AOP的基本概念

- (1)Aspect(切面):通常是一个类，里面可以定义切入点和通知
- (2)JointPoint(连接点):程序执行过程中明确的点，一般是方法的调用
- (3)Advice(通知):AOP在特定的切入点上执行的增强处理，有before,after,afterReturning,afterThrowing,around
- (4)Pointcut(切入点):就是带有通知的连接点，在程序中主要体现为书写切入点表达式
- (5)AOP代理：AOP框架创建的对象，代理就是目标对象的加强。Spring中的AOP代理可以使JDK动态代理，也可以是CGLIB代理，前者基于接口，后者基于子类



**通知方法:**

1. 前置通知:在我们执行目标方法之前运行(**@Before**)
2. 后置通知:在我们目标方法运行结束之后 ,不管有没有异常***\*(@After)\****
3. 返回通知:在我们的目标方法正常返回值后运行***\*(@AfterReturning)\****
4. 异常通知:在我们的目标方法出现异常后运行***\*(@AfterThrowing)\****
5. 环绕通知:动态代理, 需要手动执行joinPoint.procced()(其实就是执行我们的目标方法执行之前相当于前置通知, 执行之后就相当于我们后置通知**(@Around)**



![image-20200311205413929](F:\OneDrive\MarkDown\images\image-20200311205413929.png)