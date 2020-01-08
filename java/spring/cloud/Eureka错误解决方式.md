在根据大神的文章安装eureka过程遇到些报错，这里记录下比较好的解决方案

https://blog.csdn.net/forezp/article/details/70148833/

启动Eureka server

直接启动报错：

EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.

 

之后各种查找开始查到一个方案

https://www.cnblogs.com/moonandstar08/p/6435710.html

感觉解释的不是很清楚

开始直接 设置 eureka.server.enableSelfPreservation: false 结果又报错：

THE SELF PRESERVATION MODE IS TURNED OFF.THIS MAY NOT PROTECT INSTANCE EXPIRY IN CASE OF NETWORK/OTHER PROBLEMS.

 

后来在stackoverflow上看到一个解释的比较清楚的：

https://stackoverflow.com/questions/33921557/understanding-spring-cloud-eureka-server-self-preservation-and-renew-threshold#

大概总结的方案是

部署两台Eureka服务器并启用registerWithEureka。
如果您只想在demo / dev环境中部署，则可以设置eureka.server.renewalPercentThreshold为0.49，因此当您单独启动Eureka服务器时，阈值将为0。
下面是直接google翻译的：

每个实例都需要将其租约更新到Eureka Server，频率为每30秒一次，可以在其中定义eureka.instance.leaseRenewalIntervalInSeconds。

续订（最后一分钟）：表示在最后一分钟从Eureka实例收到的续订数量

续订阈值：Eureka服务器预期每分钟从Eureka实例收到的续订。

例如，如果registerWithEureka设置为false，eureka.instance.leaseRenewalIntervalInSeconds则设置为30并运行2 Eureka实例。两个Eureka实例每分钟将向Eureka服务器发送4次更新，Eureka服务器最小阈值为1（用代码编写），因此阈值为5（这个数字将乘以一个因子eureka.server.renewalPercentThreshold，稍后将讨论）。

自我保存模式：如果续订（最后一分钟）小于续订阈值，将激活自我保护模式。

所以在上面的示例中，SELF PRESERVATION MODE被激活，因为阈值是5，但Eureka服务器只能接收4次更新/分钟。

问题1：
SELF PRESERVATION MODE旨在避免网络连接故障。Eureka实例A和B之间的连接很好，但是由于连接问题，B很难在短时间内将租约更新到Eureka服务器，此时Eureka服务器不能简单地启动实例B.如果是，则实例尽管B可用，但无法从Eureka服务器获得注册服务。所以这就是SELF PRESERVATION MODE的目的，最好将其打开。

问题2：
最小阈值1写在代码中。registerWithEureka设置为false，因此没有Eureka实例寄存器，阈值将为1。

在生产环境中，通常我们部署两个Eureka服务器并将registerWithEureka设置为true。所以门槛将是2，Eureka服务器将续租两次/分钟，所以RENEWALS ARE LESSER THAN THRESHOLD不会有问题。

问题3：
你是对的。eureka.instance.leaseRenewalIntervalInSeconds定义每分钟发送到服务器的续订次数，但它将乘以eureka.server.renewalPercentThreshold上面提到的因子，默认值为0.85。

问题4：
是的，这是正常的，因为阈值初始值设置为1.因此，如果registerWithEureka设置为false，则续订总是低于阈值。

我有两个建议：

部署两台Eureka服务器并启用registerWithEureka。
如果您只想在demo / dev环境中部署，则可以设置eureka.server.renewalPercentThreshold为0.49，因此当您单独启动Eureka服务器时，阈值将为0。


原文：

Every instance needs to renew its lease to Eureka Server with frequency of one time per 30 seconds, which can be define in eureka.instance.leaseRenewalIntervalInSeconds.

Renews (last min): represents how many renews received from Eureka instance in last minute

Renews threshold: the renews that Eureka server expects received from Eureka instance per minute.

For example, if registerWithEureka is set to false, eureka.instance.leaseRenewalIntervalInSeconds is set to 30 and run 2 Eureka instance. Two Eureka instance will send 4 renews to Eureka server per minutes, Eureka server minimal threshold is 1 (written in code), so the threshold is 5 (this number will be multiply a factor eureka.server.renewalPercentThreshold which will be discussed later).

SELF PRESERVATION MODE: if Renews (last min) is less than Renews threshold, self preservation mode will be activated.

So in upper example, the SELF PRESERVATION MODE is activated, because threshold is 5, but Eureka server can only receive 4 renews/min.

Question 1:
The SELF PRESERVATION MODE is design to avoid poor network connectivity failure. Connectivity between Eureka instance A and B is good, but B is failed to renew its lease to Eureka server in a short period due to connectivity hiccups, at this time Eureka server can't simply just kick out instance B. If it does, instance A will not get available registered service from Eureka server despite B is available. So this is the purpose of SELF PRESERVATION MODE, and it's better to turn it on.

Question 2:
The minimal threshold 1 is written in the code. registerWithEureka is set to false so there will be no Eureka instance registers, the threshold will be 1.

In production environment, generally we deploy two Eureka server and registerWithEureka will be set to true. So the threshold will be 2, and Eureka server will renew lease to itself twice/minute, so RENEWALS ARE LESSER THAN THRESHOLD won't be a problem.

Question 3:
Yes, you are right. eureka.instance.leaseRenewalIntervalInSeconds defines how many renews sent to server per minute, but it will multiply a factor eureka.server.renewalPercentThreshold mentioned above, the default value is 0.85.

Question 4:
Yes, it's normal, because the threshold initial value is set to 1. So if registerWithEureka is set to false, renews is always below threshold.

I have two suggestions for this:

Deploy two Eureka server and enable registerWithEureka.
If you just want to deploy in demo/dev environment, you can set eureka.server.renewalPercentThreshold to 0.49, so when you start up a Eureka server alone, threshold will be 0.





# 2

SpringBoot开发中关闭Security安全策略报错:Deprecated: The security auto-configuration is no longer customizable如果在yml文件中配置

security:
  basic:
    enabled: false

idea会报错:Deprecated: The security auto-configuration is no longer customizable less... (Ctrl+F1) 
Checks Spring Boot application .yaml configuration files. Highlights unresolved and deprecated configuration keys and invalid values. Works only for Spring Boot 1.2 or higher.

这是因为在SpringBoot2.0版本后安全配置将不再是可定制的，解决办法：

1、在启动类上，移除默认自动启动的安全策略

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
2、可以将SpringBoot的版本更换为2.0之前的，更新项目依赖，就可以直接在yml文件中配置

security:
  basic:
    enabled: true
3.写一个配置类继承WebSecurityConfigurerAdapter 接口，覆盖configure方法，调用http.httpBasic().disable();方法

建议使用这种

@Configuration
public class SecurityConfiguration  extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable();
    }

}