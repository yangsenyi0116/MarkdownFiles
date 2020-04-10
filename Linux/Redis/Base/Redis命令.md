# 时间复杂度

| 命令   | 时间复杂度 |
| ------ | ---------- |
| keys   | O(n)       |
| dbsizi | O(1)       |
| del    | O(1)       |
| exists | O(1)       |
| expire | O(1)       |
| type   | O(1)       |



# 单线程

1. 一次只运行一条命令

2. 拒绝长（慢）命令

   keys,flushall,flushdb,slow lua script,mutil/exec,operate big value(collection)

3. 其实不是单线程

   fysnc file descriptor

   close file descriptor



# Redis API的使用和理解
------
## 基础命令
### expire、ttl、persist

- expire key seconds

  #key在seconds秒后过期

- ttl key

  #查看key剩余的过期时间

- persist key

  #去掉key的过期时间

### type

- type key 

  #返回key的数据类型

### get、set、del
- get key
  #获取key对应的value

- set key value
  #设置key-value

- del key
  #删除key-value

### incr、decr、incrby、decrvt
- incr key
  #key自增1，如果key不存在，自增后get(key) = 1
- decr key
  #key自减1，如果key不存在，自减后get（key） = -1
- incrby key k
- decr key k

> 作用当网站技术器，单线程无竞争，减少后台数据的访问量

### set setnx setxx

set key value
setnx key value
set key value xx

- #不管key是否存在，都设置
- #key不存在，才设置
- #key存在，才设置

### maget mset

mget key1 key2 key3....

mset key1 value1 key2 value2 key3 value3

- 批量获取key,原子操作
- 批量设置key-value

### getset、append、strlen
getset key newvalue
append key value
strlen key

- set key newvalue并返回旧的value
- 将value追加到旧的value
- 返回字符串的长度（注意中文）

### incrbyfloat getrange setrange
incrbyfloat key 3.5
getrange key start end
setrange key index value
- 增加key对应的值3.5
- 获取字符串指定下标所有的值
- 设置指定下标所有对应的值





# 哈希键值结构

---

### hget hset hdel

```hget key field```
```hset key field value```
```hdel key field```

- 获取hash key对应的field的value
- 设置hash key对应field的value
- 删除hash key对应field的value

### hexists hlen
```hexists key field```
```hlen key```

- 判断hash key是否有field
- 获取hash key field的数量

### hmget hmset
获取和批量设置field

### hgetall hvals hkeys
```hgetall key```
```hvals key```
```hkeys key```

- 返回hash key对应所有的field和value
- 返回hash key对应所有的field和value
- 返回hash key对应所有的field

### rpush
```rpush key value1 value2 ... valueN```

- 从列表右段插入值(1-N个)

### lpush
- 从列表左端插入值



### linset

```linset key before|after value newValue```

- 在list指定的值前|后插入newValue

### lpop

```lpop key```

- 从列表左端弹出一个item

### rpop

- 从列表右端弹出一个item

### lrem

```lrem key count value```

- 根据count值，从列表中删除所有value相等的项
  - (1)  count>0 从左到右，删除最多count个value相等的项
  - (2) count<0 从右到左，删除最多Math.abs(count)个value相等的项
  - (#) count=0 删除所有value相等的项  

### ltrim

```ltrim key start end```

- 按照索引范围修建列表

### lrange

```lrange key start end（包含end）```

- 获取列表指定索引范围所有的item

  ### lindex

```lindex key index```

- 获取列表指定索引的item

### llen

```llen key```

- 获取列表长度

### lset

```lset key index newValue```

- 设置列表指定索引值为new Value

### blpop brpop

```blpop key timeout```

```brpop key timeout```

- lpop阻塞版本，timeout是阻塞超时时间，timeout=0为永不阻塞
- rpop阻塞版本，timeout是阻塞超时时间，timeout=0为永不阻塞



## 集合 set

> 特点
> 无序、无重复、集合间操作



### sadd srem

sadd key element

srem key element

- 向集合key添加element(如果element已经存在，添加失败)
- 将集合key中的element移除掉

### scard sismember srandmemeber smembers

user:1:follow {it,music,his,sports}

```scard user:1:follow = 4 ``` #计算集合大小

```sismember user:1:follow it = 1 ```#判断it是否在集合中

```srandmember user:1:follow count=his``` #从集合中随机挑count个元素

```spop user:1:follow = sports``` #从集合中随机弹出一个元素

```smembers user:1:follow = music his sport it``` #获取集合所有元素

>  smemebers 
>
> 返回结果是无序的，需求内存多

> spop从集合弹出
>
> srandmember不会破坏集合

### sdiff sinter sunion

user:1:follow {it,music,his,sports}

user:2:follow {it,news,ent,sports}

```sidff user:1:follow user:2:follow = music his``` #差集

```sinter user:1:follow user:2:follow = it sports``` #交集

```sunion user:1:follow user:2:follow = it music his sports news ent``` #并集

```sdiff|sinter|suion + store destkey ..``` #将差集，交集，并集结果保存在destkey中



## 有序集合

| 集合       | 有序集合        |
| ---------- | --------------- |
| 无重复元素 | 无重复元素      |
| 无序       | 有序            |
| element    | element + score |

| 列表 | 有序集合 |
| ------- | ---------|
| 可以有重复元素 | 无重复元素 |
| 有序 | 有序 |
| element | element + score|



### zadd

```zadd key score element(可以是多对)``` 添加score和element

### zrem

```zrem key element```删除元素

### zscore

```zscore key element``` 返回元素的分数

### zincrby

```zincrby key increScore element``` 增加或减少元素的分数

### zcard

```zcard key``` 返回元素的总个数

### zrange

```zrange key start end [WITHSCORES]``` 返回指定索引范围内的升序元素[分值]

### zrangebyscore

```zrangebyscore key minScore maxScore [WITHSCORES]``` 返回指定分数范围内的升序元素[分值]

### zcount

```zcount key minScore maxScore``` 返回有序集合内在指定分数范围内的个数

### zremrangebyrank

```zremrangebyrank key start end``` 删除指定排名内的升序元素

### zremrangebyscore

``` zremrangebyscore key minScore maxScore``` 删除指定分数内的升序元素




