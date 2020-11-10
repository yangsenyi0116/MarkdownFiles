```java
//获取方法名
new Exception().getStackTrace()[0].getMethodName();

//获取当前类名
Thread.currentThread().getStackTrace()[1].getClassName();
```

