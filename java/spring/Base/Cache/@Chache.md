## @Cacheable

- **value：\**缓存位置名称\****，不能为空，如果使用EHCache，就是ehcache.xml中声明的cache的name
- **key：\**缓存的key\****，默认为空，既表示使用方法的参数类型及参数值作为key，支持SpEL
- **condition：\**触发条件\****，只有满足条件的情况才会加入缓存，默认为空，既表示全部都加入缓存，支持SpEL

```java

//将缓存保存进andCache，并使用参数中的userId加上一个字符串(这里使用方法名称)作为缓存的key   
@Cacheable(value="andCache",key="#userId + 'findById'")  
public SystemUser findById(String userId) {  
    SystemUser user = (SystemUser) dao.findById(SystemUser.class, userId);        
    return user ;         
}  
//将缓存保存进andCache，并当参数userId的长度小于32时才保存进缓存，默认使用参数值及类型作为缓存的key  
@Cacheable(value="andCache",condition="#userId.length < 32")  
public boolean isReserved(String userId) {  
    System.out.println("hello andCache"+userId);  
    return false;  
}
```

## @CacheEvict

- **value：\**缓存位置名称\****，不能为空，同上
- **key：\**缓存的key\****，默认为空，同上
- **condition：\**触发条件\****，只有满足条件的情况才会清除缓存，默认为空，支持SpEL
- **allEntries：**true表示清除value中的全部缓存，默认为false

**allEntries属性详解：**

​    allEntries是boolean类型，表示是否需要清除缓存中的所有元素。默认为false，表示不需要。当指定了allEntries为true时，Spring Cache将忽略指定的key。**有的时候我们需要Cache一下清除所有的元素，这比一个一个清除元素更有效率。**

``` java
 @CacheEvict(value="users", allEntries=true)
   public void delete(Integer id) {
      System.out.println("delete user by id: " + id);
   }
```

```java
//清除掉指定key的缓存  
    @CacheEvict(value="andCache",key="#user.userId + 'findById'")  
    public void modifyUserRole(SystemUser user) {  
             System.out.println("hello andCache delete"+user.getUserId());  
    }  
      
    //清除掉全部缓存  
    @CacheEvict(value="andCache",allEntries=true)  
    public final void setReservedUsers(String[] reservedUsers) {  
        System.out.println("hello andCache deleteall");  
    }
```

**一般来说，我们的更新操作只需要刷新缓存中某一个值，所以定义缓存的key值的方式就很重要，最好是能够\**唯一\**，因为这样可以\**准确的清除掉特定的缓存\**，而\**不会影响到其它缓存值\**** ，

**比如我这里针对用户的操作，使用(userId+方法名称)的方式设定key值** ，**当然，你也可以找到更适合自己的方式去设定。**



## @CachePut

既调用方法，又更新缓存数据

在之前的分享的时候对于@Cacheable注解是在方法调用之前先去插叙缓存，但是CachePut注解是先调用方法，然后将方法的返回值放入到缓存中。

------

**EhCache 是一个纯Java的进程内缓存框架，具有快速、精干等特点，是Hibernate中默认的CacheProvider。**


**Ehcache是一种广泛使用的开源Java分布式缓存。**

​     **主要面向通用缓存，Java EE和轻量级容器。**它具有内存和磁盘存储，缓存加载器，缓存扩展，缓存异常处理程序，一个gzip缓存servlet过滤器，支持REST和SOAP api等特点。

​     Ehcache最初是由Greg Luck于2003年开始开发。2009年,该项目被Terracotta购买。软件仍然是开源,但一些新的主要功能(例如，快速可重启性之间的一致性的)只能在商业产品中使用，例如Enterprise EHCache and BigMemory。维基媒体Foundationannounced目前使用的就是Ehcache技术。

