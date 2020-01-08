Redis数据类型

![1562763522380](G:\onedriver\OneDrive\MarkDown\image\1562763522380.png)

![1562763584676](G:\onedriver\OneDrive\MarkDown\image\1562763584676.png)

```cmd
set {key} {value}
get {key}
incr {key}自增
decr {key} {decrement}自减
```

![1562763809521](G:\onedriver\OneDrive\MarkDown\image\1562763809521.png)

```cmd
lpush {list} {value} 左添加
rpush {list} {value} 右添加
rpop {list} 右输出
lpop {list} 左输出
llen {list} 长度

```

![1562763974536](G:\onedriver\OneDrive\MarkDown\image\1562763974536.png)

>set集合是string类型的无序集合，set是通过hashtable实现的，对集合我们可以取交集，并集，差集
>
>sadd：向名称为key的set中添加元素
>smembers：查看set中集合的元素
>srem：删除set集合元素
>spop：随机返回删除的key
>sdiff：返回两个集合的不同元素（哪个集合在前面就以哪个集合为准）
>sdiffstore：将返回的不同元素存储到另外一个集合里
>sinter：返回集合的交集
>sinterstore：返回交集结果，存入set3中
>sunion：取并集
>sunionstore：取得并集并存入set3中
>smove：从一个set集合移动到另一个set集合里

```cmd
sadd {list} {value} 往set中添加元素
scard {list} 查看set的长度
sismember {list} {value} 判断value是否存在在list中
srem {list} {value} 从list中删除value
```

![1562764157001](G:\onedriver\OneDrive\MarkDown\image\1562764157001.png)

```cmd
hset {hash} {key} {value} 往hash中插入key value
hget {hash} {key} 取出hash中的key的值
hlen {hash} 判断hash中有多少个元素
覆盖同设置值
hmget {hash} {key1} {key2}取得hash中的key的值（多个）
```

![1562764425281](G:\onedriver\OneDrive\MarkDown\image\1562764425281.png)

```cmd
zadd {sort set} {value} {key}
zcard {sort set} 查看有多少元素
zrange {sort set} {start} {end} withscores从start到end连带分数一起取出
zrank {sort set} {key} 查看key的排行
```

