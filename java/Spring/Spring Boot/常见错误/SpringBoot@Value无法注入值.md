SpringBoot中使用@Value()只能给普通变量注入值，不能直接给静态变量赋值
例如，application-dev.properties 配置文件有如下配置：

![img](assets/20180717210618886.png)

给普通变量赋值时，直接在变量声明之上添加@Value()注解即可，如下所示：

![img](assets/20180717213012686.png)

当要给静态变量注入值的时候，若是在静态变量声明之上直接添加@Value()注解是无效的，例如：

![img](assets/20180717212631499.png)

虽然没有编译和运行上的报错，经调试可知这种注解方式mailUsername、mailPassword、mailHost的值都是null，也就是说直接给静态变量读取配置文件是无效的，如下所示：

![img](assets/20180717212912354.png)



若要给静态变量赋值，可以使用set()方法，其中需要在类上加入@Component注解，方法名（例如setMailUsername）和参数名（例如username）可以任意命名，如下所示：

![img](assets/20180717214817815.png)


调试结果如下：

![img](assets/20180717213346793.png)