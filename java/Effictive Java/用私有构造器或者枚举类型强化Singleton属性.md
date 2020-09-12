> Singleton是指仅仅被实例化一次的类.Singleton通常被用来代表一个无状态的对象



```java
public class Elvis{
    public static final Elvis INSTANCE = new Elvis();
    
    private Elvis(){...}
   
    public void leaveTheBuilding(){
        
    }
}
```

这种方法享有特权的客户端可以借助AccessibleObject.setAccessible方法,通过反射机制调用私有构造器.

**使用静态工厂模式**

```java
public class Elvis{
    private static final Elvis INSTANCE = new Elvis();
    
    private Elvis(){...}
   
    public static Elvis getInstance(){
        return INSTANCE;
    }
    
    public void leaveTheBuilding(){
        
    }
}
```

为了将利用上述方法实现的Singleton类变成是可序列化的(Serializable)仅仅在声明中加上implements Serializable是不够的.为了维护并保证Singleton,必须声明所有实例域都是瞬时的,并提供一个readResolve方法.否则,每次反序列化一个虚拟化实例时都会创建一个新的实例

改造Elvis类

```java
private Object readResolve(){
    return INSTANCE;
}
```

实现Singleton的第三种方法是声明一个包含单个元素的枚举类型

```java
public enum Elvis{
    INSTANCE;
    
    public void leaveTheBuilding(){...}
}
```

