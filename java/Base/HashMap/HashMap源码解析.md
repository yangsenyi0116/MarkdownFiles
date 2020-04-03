## Java1.7 HashMap扩容节点转移分析



JDK1.7 HashMap由数组和链表组成

JDK1.8 加入了红黑树(链表长度>=8链表转红黑树)



HashMap数组初始化长度**16**

```java
static final int DEFAULT_INITIAL_CAPACITY = 1<<4;	//16
loadFactor=0.75;
```

