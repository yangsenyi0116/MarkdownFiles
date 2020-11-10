### SpringIOC初始化过程

`ClassPathXmlApplicationContext`,`FileSystemApplicationContext`

- `setConfigLocations(configLocations)`=>``AbstractRefreshableConfigApplicationContext`
  
  - 资源定位
  
- `refresh()`=>`AbstractApplicationContext`
  
  - 初始化入口
  
- `prepareRefresh()`=>`AbstartctApplicationContext`
  
  - 准备工作
  
- `obtainFreshBeanFactory()`=>`AbstractApplicationContext`

- `refreshBeanFactory`=>`AbstractRefreshableApplicationContext`

- `getBeanFactory()`=>`AbstractRefreshableApplicationContext`
  
  - 构建BeanFactory入口
  
- `LoadBeanDifinitions(beanFactory)`=>`AbstractXmlApplicationContext`
  
  - 加载BeanDefinition入口
  
- `XmlBeanDifinitionReader`
  
  - 用于解析XML配置文件
  
- `doLoadBeanDifinitions(InputSource inputSource,Resource resource)`=>`XmlBeanDifinitionReader`
  
  - 解析XML文件入口
  
- `doLoadDocument(InputSource inputSource, Resource resource)`=>`XmlBeanDifinitionReader`
  
  - 解析Document对象
  
- `Document`

  - 用于解析xml配置文件

- `registBeanDifinition(Document doc,XmlReaderContext readerContext)`=>`DefaultBeanDefinitionDocumentReader`
  
  - 构建BeanDifinition入口
  
- `doRegisterBeanDifinition(Element root)`=>`DefaultBeanDefinitionDocumentReader`

  - BeanDifinitionParserDelegate对象

- `parseBeanDifinition(Element root,BeanDifinitionParseDelegate delegate)`=>`DefaultBeanDifinitionDocumentReader`

- `parseDefaultElement(eElement ele,BeanDifinitionParseDelegate delegate)`=>`DefaultBeanDefinitionDocumentReader`

- 1. `processBeanDifinition(Element ele,BeanDefinitionParserDelegate delegate)`=>`DefaultBeanDefinitionDocumentReader`
1. BeanDefinitionHolder
  2. `parseBeanDifinitionElement(Element ele,@nullable BeanDefinition containingBean)`=>`BeanDifinitionParserDelegate`
1. AbstractBeanDefinition
   
3. `registerBeanDefinition(String beanName, BeanDefinition beanDefinition)`=>`DefaultListableBeanFactory`
  4. `Map<String,BeanDefinition> beanDefinitionMap`=>`DefaultListableBeanFactory`

​    

- 1. `processAliasRegistration(Element ele)`=>`DefaultBeanDefinitionDocumentReader`
  2. `registerAlias(String name,String alias)`=>`SimpleAliasRegistry`
  3. `Map<String,String> aliasMap`

- `prepareBeanFactory(beanFactory)`

- `postProcessBeanFactory(beanFactory)`

- `invokeBeanFactoryPostProcessors(beanFacotry)`

- `registerBeanPostProcessors(beanFactory)`

- `initMessageSource()`

- `initApplicationEventMulticaster()`

- `onRefresh()`

- `registerListeners()`

- `finishBeanFactoryInitialization(beanFactory)`

- `finishRefresh()`

- `destoryBeans()`

- `cancelRefresh(ex)`

- `resetCommonCaches()`







![image-20201006004632483](../../../../../images/image-20201006004632483.png)

![image-20201006005551735](../../../../../images/image-20201006005551735.png)



> AOP是在BeanPostProcessor过程中间实现的
>
> SpringBoot在BeanFactoryPostProcessor过程中实现的