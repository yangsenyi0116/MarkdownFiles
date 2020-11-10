通过zookeeper实现**分布式锁**

## 1、创建zookeeper的client

首先通过CuratorFrameworkFactory创建一个连接zookeeper的连接CuratorFramework client

```java
public class CuratorFactoryBean implements FactoryBean<CuratorFramework>, InitializingBean, DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContractFileInfoController.class);

    private String connectionString;
    private int sessionTimeoutMs;
    private int connectionTimeoutMs;
    private RetryPolicy retryPolicy;
    private CuratorFramework client;

    public CuratorFactoryBean(String connectionString) {
        this(connectionString, 500, 500);
    }

    public CuratorFactoryBean(String connectionString, int sessionTimeoutMs, int connectionTimeoutMs) {
        this.connectionString = connectionString;
        this.sessionTimeoutMs = sessionTimeoutMs;
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    @Override
    public void destroy() throws Exception {
        LOGGER.info("Closing curator framework...");
        this.client.close();
        LOGGER.info("Closed curator framework.");
    }

    @Override
    public CuratorFramework getObject() throws Exception {
        return this.client;
    }

    @Override
    public Class<?> getObjectType() {
         return this.client != null ? this.client.getClass() : CuratorFramework.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(this.connectionString)) {
            throw new IllegalStateException("connectionString can not be empty.");
        } else {
            if (this.retryPolicy == null) {
                this.retryPolicy = new ExponentialBackoffRetry(1000, 2147483647, 180000);
            }

            this.client = CuratorFrameworkFactory.newClient(this.connectionString, this.sessionTimeoutMs, this.connectionTimeoutMs, this.retryPolicy);
            this.client.start();
            this.client.blockUntilConnected(30, TimeUnit.MILLISECONDS);
        }
    }
    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public void setClient(CuratorFramework client) {
        this.client = client;
    }
}

```

## 2、封装分布式锁

根据CuratorFramework创建InterProcessMutex（分布式可重入排它锁）对一行数据进行上锁

```java
  public InterProcessMutex(CuratorFramework client, String path) {
        this(client, path, new StandardLockInternalsDriver());
    }

```

使用 acquire方法
1、acquire() :入参为空，调用该方法后，会一直堵塞，直到抢夺到锁资源，或者zookeeper连接中断后，上抛异常。
2、acquire(long time, TimeUnit unit)：入参传入超时时间、单位，抢夺时，如果出现堵塞，会在超过该时间后，返回false。

```java
  public void acquire() throws Exception {
        if (!this.internalLock(-1L, (TimeUnit)null)) {
            throw new IOException("Lost connection while trying to acquire lock: " + this.basePath);
        }
    }

    public boolean acquire(long time, TimeUnit unit) throws Exception {
        return this.internalLock(time, unit);
    }

```

释放锁 mutex.release();

```java
  public void release() throws Exception {
        Thread currentThread = Thread.currentThread();
        InterProcessMutex.LockData lockData = (InterProcessMutex.LockData)this.threadData.get(currentThread);
        if (lockData == null) {
            throw new IllegalMonitorStateException("You do not own the lock: " + this.basePath);
        } else {
            int newLockCount = lockData.lockCount.decrementAndGet();
            if (newLockCount <= 0) {
                if (newLockCount < 0) {
                    throw new IllegalMonitorStateException("Lock count has gone negative for lock: " + this.basePath);
                } else {
                    try {
                        this.internals.releaseLock(lockData.lockPath);
                    } finally {
                        this.threadData.remove(currentThread);
                    }

                }
            }
        }
    }

```

封装后的DLock代码
1、调用InterProcessMutex processMutex = dLock.mutex(path);

2、手动释放锁processMutex.release();

3、需要手动删除路径dLock.del(path);

推荐 使用：
都是 函数式编程
在业务代码执行完毕后 会释放锁和删除path
1、这个有返回结果
public T mutex(String path, ZkLockCallback zkLockCallback, long time, TimeUnit timeUnit)
2、这个无返回结果
public void mutex(String path, ZkVoidCallBack zkLockCallback, long time, TimeUnit timeUnit)

```java
public class DLock {
    private final Logger logger;
    private static final long TIMEOUT_D = 100L;
    private static final String ROOT_PATH_D = "/dLock";
    private String lockRootPath;
    private CuratorFramework client;

    public DLock(CuratorFramework client) {
        this("/dLock", client);
    }

    public DLock(String lockRootPath, CuratorFramework client) {
        this.logger = LoggerFactory.getLogger(DLock.class);
        this.lockRootPath = lockRootPath;
        this.client = client;
    }
    public InterProcessMutex mutex(String path) {
        if (!StringUtils.startsWith(path, "/")) {
            path = Constant.keyBuilder(new Object[]{"/", path});
        }

        return new InterProcessMutex(this.client, Constant.keyBuilder(new Object[]{this.lockRootPath, "", path}));
    }

    public <T> T mutex(String path, ZkLockCallback<T> zkLockCallback) throws ZkLockException {
        return this.mutex(path, zkLockCallback, 100L, TimeUnit.MILLISECONDS);
    }

    public <T> T mutex(String path, ZkLockCallback<T> zkLockCallback, long time, TimeUnit timeUnit) throws ZkLockException {
        String finalPath = this.getLockPath(path);
        InterProcessMutex mutex = new InterProcessMutex(this.client, finalPath);

        try {
            if (!mutex.acquire(time, timeUnit)) {
                throw new ZkLockException("acquire zk lock return false");
            }
        } catch (Exception var13) {
            throw new ZkLockException("acquire zk lock failed.", var13);
        }

        T var8;
        try {
            var8 = zkLockCallback.doInLock();
        } finally {
            this.releaseLock(finalPath, mutex);
        }

        return var8;
    }

    private void releaseLock(String finalPath, InterProcessMutex mutex) {
        try {
            mutex.release();
            this.logger.info("delete zk node path:{}", finalPath);
            this.deleteInternal(finalPath);
        } catch (Exception var4) {
            this.logger.error("dlock", "release lock failed, path:{}", finalPath, var4);
//            LogUtil.error(this.logger, "dlock", "release lock failed, path:{}", new Object[]{finalPath, var4});
        }

    }

    public void mutex(String path, ZkVoidCallBack zkLockCallback, long time, TimeUnit timeUnit) throws ZkLockException {
        String finalPath = this.getLockPath(path);
        InterProcessMutex mutex = new InterProcessMutex(this.client, finalPath);

        try {
            if (!mutex.acquire(time, timeUnit)) {
                throw new ZkLockException("acquire zk lock return false");
            }
        } catch (Exception var13) {
            throw new ZkLockException("acquire zk lock failed.", var13);
        }

        try {
            zkLockCallback.response();
        } finally {
            this.releaseLock(finalPath, mutex);
        }

    }

    public String getLockPath(String customPath) {
        if (!StringUtils.startsWith(customPath, "/")) {
            customPath = Constant.keyBuilder(new Object[]{"/", customPath});
        }

        String finalPath = Constant.keyBuilder(new Object[]{this.lockRootPath, "", customPath});
        return finalPath;
    }

    private void deleteInternal(String finalPath) {
        try {
            ((ErrorListenerPathable)this.client.delete().inBackground()).forPath(finalPath);
        } catch (Exception var3) {
            this.logger.info("delete zk node path:{} failed", finalPath);
        }

    }

    public void del(String customPath) {
        String lockPath = "";

        try {
            lockPath = this.getLockPath(customPath);
            ((ErrorListenerPathable)this.client.delete().inBackground()).forPath(lockPath);
        } catch (Exception var4) {
            this.logger.info("delete zk node path:{} failed", lockPath);
        }

    }
}

```

```java
@FunctionalInterface
public interface ZkLockCallback<T> {
    T doInLock();
}

@FunctionalInterface
public interface ZkVoidCallBack {
    void response();
}

public class ZkLockException extends Exception {
    public ZkLockException() {
    }

    public ZkLockException(String message) {
        super(message);
    }

    public ZkLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

配置CuratorConfig

```java
@Configuration
public class CuratorConfig {
    @Value("${zk.connectionString}")
    private String connectionString;

    @Value("${zk.sessionTimeoutMs:500}")
    private int sessionTimeoutMs;

    @Value("${zk.connectionTimeoutMs:500}")
    private int connectionTimeoutMs;

    @Value("${zk.dLockRoot:/dLock}")
    private String dLockRoot;

    @Bean
    public CuratorFactoryBean curatorFactoryBean() {
        return new CuratorFactoryBean(connectionString, sessionTimeoutMs, connectionTimeoutMs);
    }

    @Bean
    @Autowired
    public DLock dLock(CuratorFramework client) {
        return new DLock(dLockRoot, client);
    }
}

```

测试代码

```java
@RestController
@RequestMapping("/dLock")
public class LockController {

    @Autowired
    private DLock dLock;

    @RequestMapping("/lock")
    public Map testDLock(String no){
        final String path = Constant.keyBuilder("/test/no/", no);
        Long mutex=0l;
        try {
            System.out.println("在拿锁："+path+System.currentTimeMillis());
             mutex = dLock.mutex(path, () -> {
                try {
                    System.out.println("拿到锁了" + System.currentTimeMillis());
                    Thread.sleep(10000);
                    System.out.println("操作完成了" + System.currentTimeMillis());
                } finally {
                    return System.currentTimeMillis();
                }
            }, 1000, TimeUnit.MILLISECONDS);
        } catch (ZkLockException e) {
            System.out.println("拿不到锁呀"+System.currentTimeMillis());
        }
        return Collections.singletonMap("ret",mutex);
    }

    @RequestMapping("/dlock")
    public Map testDLock1(String no){
        final String path = Constant.keyBuilder("/test/no/", no);
        Long mutex=0l;
        try {
            System.out.println("在拿锁："+path+System.currentTimeMillis());
            InterProcessMutex processMutex = dLock.mutex(path);
            processMutex.acquire();
            System.out.println("拿到锁了" + System.currentTimeMillis());
            Thread.sleep(10000);
            processMutex.release();
            System.out.println("操作完成了" + System.currentTimeMillis());
        } catch (ZkLockException e) {
            System.out.println("拿不到锁呀"+System.currentTimeMillis());
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Collections.singletonMap("ret",mutex);
    }
    @RequestMapping("/del")
    public Map delDLock(String no){
        final String path = Constant.keyBuilder("/test/no/", no);
        dLock.del(path);
        return Collections.singletonMap("ret",1);
    }
}


```

